package DataManagement;

import Domain.BinMap;
import Domain.Transaction;
import Domain.User;
import Enums.ECardType;
import Enums.EPaymentMethod;
import Enums.ETransactionType;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
public class DataReader {
    private final Path filePath;
    public DataReader(Path path) {
        this.filePath = path;
    }


    public List<String> readLines() throws IOException {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath.toFile(), StandardCharsets.ISO_8859_1))) {
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
        return lines;
    }

    public static List<BinMap> readBinMappings(Path path) throws IOException{
        List<BinMap> maps = new ArrayList<>();
        List<String> lines = new DataReader(path).readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            var map = getBinMap(parts);
            maps.add(map);
        }
        return maps;
    }

    private static BinMap getBinMap(String[] parts) {
        String name = parts[0];
        long rangeFrom = Long.parseLong(parts[1]);
        long rangeTo = Long.parseLong(parts[2]);
        ECardType type = ECardType.valueOf(parts[3]);
        String country = parts[4];
        return new BinMap(name, rangeFrom, rangeTo, type, country);
    }

    public static List<User> readUsers(Path path) throws IOException
    {
        List<User> users = new ArrayList<>();
        List<String> lines = new DataReader(path).readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            var user = getUser(parts);
            users.add(user);
        }
        return users;
    }

    private static User getUser(String[] parts) {
        String userId = parts[0];
        String username = parts[1];
        Double balance = Double.parseDouble(parts[2]);
        String country = parts[3];
        Integer frozen = Integer.parseInt(parts[4]);
        Double depositMin = Double.parseDouble(parts[5]);
        Double depositMax = Double.parseDouble(parts[6]);
        Double withdrawMin = Double.parseDouble(parts[7]);
        Double withdrawMax = Double.parseDouble(parts[8]);
        return new User(userId, username, balance, country, frozen, depositMin, depositMax, withdrawMin, withdrawMax);
    }

    public static List<Transaction> readTransactions(Path path) throws IOException
    {
        List<Transaction> transactions = new ArrayList<>();
        List<String> lines = new DataReader(path).readLines();
        for (String line : lines) {
            String[] parts = line.split(",");
            var transaction = getTransaction(parts);
            transactions.add(transaction);
        }
        return transactions;
    }

    private static Transaction getTransaction(String[] parts) {
        String transactionId = parts[0];
        String userId = parts[1];
        ETransactionType type = ETransactionType.valueOf(parts[2]);
        Double amount = Double.parseDouble(parts[3]);
        EPaymentMethod method = EPaymentMethod.valueOf(parts[4]);
        String accountNumber = parts[5];
        return new Transaction(transactionId, userId, type, amount, method, accountNumber);
    }
}
