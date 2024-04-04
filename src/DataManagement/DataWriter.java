package DataManagement;

import Domain.Event;
import Domain.User;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class DataWriter {
    public static void writeBalances(Path filePath, List<User> users) throws IOException{
        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("user_id,balance\n");
            for (final var user : users) {
                writer.append(user.getUserId()).append(",").append(String.format("%.2f", user.getBalance())).append("\n");
            }
        }
    }

    public static void writeEvents(final Path filePath, final List<Event> events) throws IOException {
        try (final FileWriter writer = new FileWriter(filePath.toFile(), false)) {
            writer.append("transaction_id,status,message\n");
            for (final var event : events) {
                writer.append(event.transactionId).append(",").append(event.status).append(",").append(event.message).append("\n");
            }
        }
    }
}
