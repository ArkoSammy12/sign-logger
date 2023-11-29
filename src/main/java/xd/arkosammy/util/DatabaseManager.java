package xd.arkosammy.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import xd.arkosammy.events.SignEditEvent;
import xd.arkosammy.events.SignEditEventResult;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DatabaseManager {

    private DatabaseManager(){}

    public static void initDatabase(MinecraftServer server){

        String sql = """
                CREATE TABLE IF NOT EXISTS sign_edit_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    original_text_line_1 TEXT,
                    original_text_line_2 TEXT,
                    original_text_line_3 TEXT,
                    original_text_line_4 TEXT,
                    new_text_line_1 TEXT,
                    new_text_line_2 TEXT,
                    new_text_line_3 TEXT,
                    new_text_line_4 TEXT,
                    timestamp TIMESTAMP,
                    is_front_side BOOLEAN
                );""";

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try(Connection c = DriverManager.getConnection(url)){

            Class.forName("org.sqlite.JDBC");
            Statement statement = c.createStatement();
            statement.execute(sql);

        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public static void storeSignEditEvent(SignEditEvent signEditEvent, MinecraftServer server){

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "INSERT INTO sign_edit_events (author_name, block_pos, world_registry_key, original_text_line_1, original_text_line_2, original_text_line_3, original_text_line_4, new_text_line_1, new_text_line_2, new_text_line_3, new_text_line_4, timestamp, is_front_side) " +
                             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, signEditEvent.author().getDisplayName().getString());
            preparedStatement.setString(2, SignEditEvent.getBlockPosAsAltString(signEditEvent.blockPos()));
            preparedStatement.setString(3, signEditEvent.worldRegistryKey() != null ? signEditEvent.worldRegistryKey().toString() : "NULL");
            preparedStatement.setString(4, signEditEvent.originalText().getTextLines()[0]);
            preparedStatement.setString(5, signEditEvent.originalText().getTextLines()[1]);
            preparedStatement.setString(6, signEditEvent.originalText().getTextLines()[2]);
            preparedStatement.setString(7, signEditEvent.originalText().getTextLines()[3]);
            preparedStatement.setString(8, signEditEvent.newText().getTextLines()[0]);
            preparedStatement.setString(9, signEditEvent.newText().getTextLines()[1]);
            preparedStatement.setString(10, signEditEvent.newText().getTextLines()[2]);
            preparedStatement.setString(11, signEditEvent.newText().getTextLines()[3]);
            preparedStatement.setTimestamp(12, Timestamp.valueOf(signEditEvent.timestamp()));
            preparedStatement.setBoolean(13, signEditEvent.isFrontSide());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }

    public static Optional<List<SignEditEventResult>> queryFromBlockPos(BlockPos blockPos, MinecraftServer server){

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");
        String blockPosAsString = SignEditEvent.getBlockPosAsAltString(blockPos);

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM sign_edit_events WHERE block_pos=?")) {

            preparedStatement.setString(1, blockPosAsString);

            try(ResultSet resultSet = preparedStatement.executeQuery()){

                ArrayList<SignEditEventResult> signEditEventResults = new ArrayList<>();

                while(resultSet.next()) {

                    String[] originalText = new String[]{"", "", "", ""};
                    String[] newText = new String[]{"", "", "", ""};

                    String author = resultSet.getString("author_name");
                    String pos = resultSet.getString("block_pos");
                    String world = resultSet.getString("world_registry_key");

                    originalText[0] = resultSet.getString("original_text_line_1");
                    originalText[1] = resultSet.getString("original_text_line_2");
                    originalText[2] = resultSet.getString("original_text_line_3");
                    originalText[3] = resultSet.getString("original_text_line_4");

                    newText[0] = resultSet.getString("new_text_line_1");
                    newText[1] = resultSet.getString("new_text_line_2");
                    newText[2] = resultSet.getString("new_text_line_3");
                    newText[3] = resultSet.getString("new_text_line_4");


                    LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
                    boolean isFrontSide = resultSet.getBoolean("is_front_side");

                    SignEditEventResult signEditEventResult = new SignEditEventResult.Builder(timestamp, isFrontSide).withAuthor(author).withBlockPos(pos).withOriginalText(originalText).withNewText(newText).withWorldRegistryKey(world).build();
                    signEditEventResults.add(signEditEventResult);
                }

                return Optional.of(signEditEventResults);

            }


        } catch (SQLException e){
            e.printStackTrace();
        }

        return Optional.empty();

    }

    public static void purgeOldEntries(int daysThreshold, MinecraftServer server) {

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM sign_edit_events WHERE timestamp < ?")) {

            LocalDateTime thresholdDateTime = LocalDateTime.now().minusDays(daysThreshold);
            Timestamp thresholdTimestamp = Timestamp.valueOf(thresholdDateTime);

            preparedStatement.setTimestamp(1, thresholdTimestamp);

            int deletedRows = preparedStatement.executeUpdate();

            System.out.println("Purged " + deletedRows + " old sign edit event(s) from the database.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
