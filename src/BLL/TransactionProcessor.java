package BLL;

import Domain.BinMap;
import Domain.Event;
import Domain.Transaction;
import Domain.User;
import Enums.ECardType;
import Enums.ECountryCode;
import Enums.EPaymentMethod;
import Enums.ETransactionType;

import java.util.*;

public class TransactionProcessor {
    private final List<User> users;
    private final List<Transaction> transactions;
    private final List<BinMap> binMaps;

    public List<Event> events = new ArrayList<>();

    private Map<String, User> userByPaymentAccount = new HashMap<>();

    Set<String> processedTransactionIds = new HashSet<>();
    private Map<String, Boolean> successfulDepositsByAccount = new HashMap<>();

    public TransactionProcessor(List<User> users, List<Transaction> transactions, List<BinMap> binMaps) {
        this.users = users;
        this.transactions = transactions;
        this.binMaps = binMaps;
    }

    public List<Event> validateTransactions(List<Transaction> transactions) {
        String message = "OK";
        for (Transaction transaction : transactions) {
            // Unique transaction control
            if (!transactionIsUnique(transaction)) {
                message = "Transaction ID is not unique!";
                events.add(new Event(transaction.getTransactionId(), Event.STATUS_DECLINED, message));
                continue;
            }
            // User controls (user exists and is not frozen)
            else if (!userValidationSuccess(transaction)) {
                continue;
            }
            User user = getUserById(transaction.getUserId());
            if (!paymentMethodSuccess(transaction)) {
                continue;
            }
            // User country has to be same as card or account number used.
            else if (!countryMatches(transaction, user)) {
                continue;
            }
            // Validate that amount is positive number and within deposit/withdraw limits
            else if (!validateAmount(transaction, user)) {
                continue;
            }
            events.add(new Event(transaction.getTransactionId(), Event.STATUS_APPROVED, "OK"));
            handleMoneyMovement(transaction, user);
            processedTransactionIds.add(transaction.getTransactionId());
        }
        return events;
    }

    private void handleMoneyMovement(Transaction transaction, User user) {
        var userOldBalance = user.getBalance();
        if (transaction.getType().equals(ETransactionType.DEPOSIT)) {
            user.setBalance(userOldBalance + transaction.getAmount());
        }
        else if (transaction.getType().equals(ETransactionType.WITHDRAW)) {
            user.setBalance(userOldBalance - transaction.getAmount());
        }
    }

    private boolean validateAmount(Transaction transaction, User user) {
        if (transaction.getAmount() < 0.0) {
            var message = "The amount has to be a positive number!";
            EventHandler(transaction, message);
            return false;
        }
        else return ControlMoneyMovement(transaction, user);

    }

    private boolean countryMatches(Transaction transaction, User user) {
        if (transaction.getMethod().equals(EPaymentMethod.TRANSFER)) {
            var accountCountry = getCountryFromAccountNumber(transaction);
            if (!accountCountry.equals(user.getCountry())) {
                var message = "The user country and account number country is not matching!";
                EventHandler(transaction, message);
                return false;
            }
        }
        if (transaction.getMethod().equals(EPaymentMethod.CARD)) {
            var bin = getBinMap(getTenFirstDigits(transaction.getAccountNumber()));
            if (!ECountryCode.isValidCountryCode(user.getCountry(), bin.getCountry())) {
                var message = "The user country and account number country is not matching!";
                EventHandler(transaction, message);
                return false;
            }
        }
        return true;
    }

    private boolean paymentMethodSuccess(Transaction transaction) {
        // TRANSFER - validate the transfer account number's check digit validity
        // CARD - only allowed DEBIT CARDS (DC)
        var method = transaction.getMethod();
        if (method.equals(EPaymentMethod.CARD)) {
            // CC or DC
            return handleCardMethod(transaction);
        }
        else if (method.equals(EPaymentMethod.TRANSFER)) {
            if (!IbanValidator.validateIBAN(transaction.getAccountNumber())) {
                var message = "The entered IBAN is invalid!";
                EventHandler(transaction, message);
                return false;
            }
        }
        return true;
    }


    private boolean handleCardMethod(Transaction transaction) {
        ECardType cardType = getCardType(transaction);
        if (cardType.equals(ECardType.CC)) {
            var message = "Only allowed DC but used is CC!";
            EventHandler(transaction, message);
            return false;
        }
        return true;
    }

    private ECardType getCardType(Transaction transaction) {
        String accountTenDigits = getTenFirstDigits(transaction.getAccountNumber());
        BinMap bin = getBinMap(accountTenDigits);
        if (bin != null) {
            return bin.getType();
        }
        throw new RuntimeException("No such card number found!");
    }

    private boolean userValidationSuccess(Transaction transaction) {
        User user = getUserById(transaction.getUserId());
        // Exists or not
        if (user == null) {
            var message = "No user for this transaction found!";
            EventHandler(transaction, message);
            return false;
        }
        // Frozen or not
        if (user.getFrozen().equals(1)) {
            var message = "User account is frozen!";
            EventHandler(transaction, message);
            return false;
        }
        return true;
    }


    private boolean transactionIsUnique(Transaction transaction) {
        if (processedTransactionIds.isEmpty()) {
            return true;
        }
        for (String id : processedTransactionIds) {
            if (transaction.getTransactionId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    private boolean ControlMoneyMovement(Transaction transaction, User user) {
        if (transaction.getType().equals(ETransactionType.DEPOSIT)) {
            if (!isVerifiedUser(transaction, user)) {
                return false;
            }
            if (!InDepositLimits(transaction, user)) {
                String message = String.format("Can't deposit (%s) more or less than the set limits!", transaction.getAmount());
                EventHandler(transaction, message);
                return false;
            }
            successfulDepositsByAccount.put(transaction.getAccountNumber(), true);
        } else if (transaction.getType().equals(ETransactionType.WITHDRAW)) {
            if (!isVerifiedUser(transaction, user)) {
                return false;
            }
            Boolean hasSuccessfulDeposit = successfulDepositsByAccount.get(transaction.getAccountNumber());
            if (hasSuccessfulDeposit == null || !hasSuccessfulDeposit) {
                String message = "This account has not done initial money deposit in order to withdraw!";
                EventHandler(transaction,message);
                return false;
            }
            if (!InWithdrawLimits(transaction, user)) {
                String message = String.format("Can't withdraw (%s) more or less than the set limits!", transaction.getAmount());
                EventHandler(transaction, message);
                return false;
            }
            if (transaction.getAmount() > user.getBalance()) {
                String message = "Can't withdraw more than you have!";
                EventHandler(transaction, message);
                return false;
            }
        }
        return true;
    }

    private boolean isVerifiedUser(Transaction transaction, User user) {
        if (!userByPaymentAccount.containsKey(transaction.getAccountNumber())) {
            // It is initial occurrence
            userByPaymentAccount.put(transaction.getAccountNumber(), user);
        }
        else {
            var accountOwner = userByPaymentAccount.get(transaction.getAccountNumber());
            if (!accountOwner.equals(user)) {
                var message = "This account number already is assigned to different user!";
                EventHandler(transaction, message);
                return false;
            }
        }
        return true;
    }

    private void EventHandler(Transaction transaction, String message) {
        events.add(new Event(transaction.getTransactionId(), Event.STATUS_DECLINED, message));
        processedTransactionIds.add(transaction.getTransactionId());
    }

    private boolean InDepositLimits(Transaction transaction, User user) {
        return transaction.getAmount() <= user.getDepositMax() && transaction.getAmount() >= user.getDepositMin();
    }

    private boolean InWithdrawLimits(Transaction transaction, User user) {
        return transaction.getAmount() <= user.getWithdrawMax() && transaction.getAmount() >= user.getWithdrawMin();
    }

    private String getCountryFromAccountNumber(Transaction transaction) {
        return transaction.getAccountNumber().substring(0,2);
    }

    private User getUserById(String userId) {
        for (User user : users) {
            if (userId.equals(user.getUserId())) {
                return user;
            }
        }
        return null;
    }



    private BinMap getBinMap(String accountTenDigits) {
        for (BinMap binMap : binMaps) {
            if (Long.parseLong(accountTenDigits) >= binMap.getRangeFrom() && Long.parseLong(accountTenDigits) <= binMap.getRangeTo()) {
                return binMap;
            }
        }
        return null;
    }

    private String getTenFirstDigits(String accountNumber) {
        if (accountNumber == null || accountNumber.length() < 10) {
            return null;
        }
        return accountNumber.substring(0, 10);
    }

}
