package xd.arkosammy.signlogger.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.signlogger.SignLogger;
import xd.arkosammy.signlogger.events.*;
import xd.arkosammy.signlogger.util.visitors.SignEditEventDatabaseVisitor;
import xd.arkosammy.signlogger.util.visitors.SignEditEventVisitor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class DatabaseManager {

    private DatabaseManager(){}

    public static void initDatabase(MinecraftServer server){

        String changedTextEventTable = """
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

        String waxedSignEventTable = """
                CREATE TABLE IF NOT EXISTS waxed_sign_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    timestamp TIMESTAMP,
                );""";

        String dyedSignEventTable = """
                CREATE TABLE IF NOT EXISTS dyed_sign_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    old_color TEXT,
                    new_color TEXT,
                    timestamp TIMESTAMP,
                    is_front_side BOOLEAN
                );""";

        String glowedSignEventTable = """
                CREATE TABLE IF NOT EXISTS glowed_sign_events (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    is_applying TEXT,
                    timestamp TIMESTAMP,
                    is_front_side BOOLEAN
                );""";

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try(Connection c = DriverManager.getConnection(url)){

            Class.forName("org.sqlite.JDBC");
            Statement statement = c.createStatement();
            statement.execute(changedTextEventTable);
            statement.execute(waxedSignEventTable);
            statement.execute(dyedSignEventTable);
            statement.execute(glowedSignEventTable);

        } catch (ClassNotFoundException | SQLException e) {
            SignLogger.LOGGER.error("Error initializing database: " + e);
        }

    }

    public static void storeSignEditEvent(SignEditEvent signEditEvent, MinecraftServer server){

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try(Connection connection = DriverManager.getConnection(url)){

            SignEditEventVisitor signEditEventVisitor = new SignEditEventDatabaseVisitor(connection);
            signEditEvent.accept(signEditEventVisitor);

        } catch (SQLException e) {
            SignLogger.LOGGER.error("Error attempting to store sign-edit event log: " + e);
        }

    }

    public static Optional<List<SignEditEventResult>> queryFromBlockPos(BlockPos blockPos, MinecraftServer server, RegistryKey<World> worldRegistryKey){

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");

        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "SELECT * FROM sign_edit_events WHERE block_pos=? AND world_registry_key=?")) {

            String blockPosAsString = SignEditEvent.getBlockPosAsLogString(blockPos);
            String worldRegistryKeyAsString = worldRegistryKey.toString();

            preparedStatement.setString(1, blockPosAsString);
            preparedStatement.setString(2, worldRegistryKeyAsString);

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
            SignLogger.LOGGER.error("Error querying from database: " + e);
        }
        return Optional.empty();

    }

    public static int purgeOldEntries(int daysThreshold, MinecraftServer server) {

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");
        int deletedRows = 0;
        try (Connection connection = DriverManager.getConnection(url);
             PreparedStatement preparedStatement = connection.prepareStatement(
                     "DELETE FROM sign_edit_events WHERE timestamp < ?")) {

            LocalDateTime thresholdDateTime = LocalDateTime.now().minusDays(daysThreshold);
            Timestamp thresholdTimestamp = Timestamp.valueOf(thresholdDateTime);
            preparedStatement.setTimestamp(1, thresholdTimestamp);
            deletedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SignLogger.LOGGER.error("Error attempting to purge database: " + e);
        }
        return deletedRows;

    }

}
