package Domain;

import Enums.EPaymentMethod;
import Enums.ETransactionType;

import java.util.Objects;

public class Transaction {

    private final String transactionId;
    private final String userId;
    private final ETransactionType type;
    private final Double amount;
    private final EPaymentMethod method;
    private final String accountNumber;

    public Transaction(String transactionId, String userId, ETransactionType type, Double amount, EPaymentMethod method, String accountNumber){
        this.transactionId = transactionId;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.method = method;
        this.accountNumber = accountNumber;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getUserId() {
        return userId;
    }

    public ETransactionType getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public EPaymentMethod getMethod() {
        return method;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionId=" + transactionId +
                ", userId=" + userId +
                ", type=" + type +
                ", amount=" + amount +
                ", method=" + method +
                ", accountNumber='" + accountNumber + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Transaction)) {
            return false;
        }
        Transaction other = (Transaction) obj;


        return Objects.equals(transactionId, other.transactionId);
    }
}
