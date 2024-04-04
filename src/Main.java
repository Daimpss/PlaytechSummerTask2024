import BLL.TransactionProcessor;
import DataManagement.DataReader;
import DataManagement.DataWriter;
import Domain.BinMap;
import Domain.Transaction;
import Domain.User;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class Main {

    public static void main(final String[] args) throws IOException {
        List<User> users = DataReader.readUsers(Paths.get(args[0]));
        List<Transaction> transactions = DataReader.readTransactions(Paths.get(args[1]));
        List<BinMap> binMaps = DataReader.readBinMappings(Paths.get(args[2]));
        var processor = new TransactionProcessor(users, transactions, binMaps);
        var events = processor.validateTransactions(transactions);

        DataWriter.writeBalances(Paths.get(args[3]), users);
        DataWriter.writeEvents(Paths.get(args[4]), events);
    }
}