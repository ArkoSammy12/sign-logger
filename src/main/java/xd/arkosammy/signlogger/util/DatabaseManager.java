package xd.arkosammy.signlogger.util;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.signlogger.SignLogger;
import xd.arkosammy.signlogger.events.*;
import xd.arkosammy.signlogger.events.result.SignEditEventQueryResult;
import xd.arkosammy.signlogger.util.visitors.SignEditEventDatabaseVisitor;
import xd.arkosammy.signlogger.util.visitors.SignEditEventVisitor;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public abstract class DatabaseManager {

    private DatabaseManager(){}

    public static void initDatabase(MinecraftServer server){

        String changedTextEventTable = """
                CREATE TABLE IF NOT EXISTS %s (
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
                );""".formatted(DatabaseTables.CHANGED_TEXT_EVENTS.getTableName());

        String waxedSignEventTable = """
                CREATE TABLE IF NOT EXISTS %s (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    timestamp TIMESTAMP
                );""".formatted(DatabaseTables.WAXED_SIGN_EVENTS.getTableName());

        String dyedSignEventTable = """
                CREATE TABLE IF NOT EXISTS %s (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    old_color TEXT,
                    new_color TEXT,
                    timestamp TIMESTAMP,
                    is_front_side BOOLEAN
                );""".formatted(DatabaseTables.DYED_SIGN_EVENTS.getTableName());

        String glowedSignEventTable = """
                CREATE TABLE IF NOT EXISTS %s (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    author_name TEXT,
                    block_pos TEXT,
                    world_registry_key TEXT,
                    is_applying BOOLEAN,
                    timestamp TIMESTAMP,
                    is_front_side BOOLEAN
                );""".formatted(DatabaseTables.GLOWED_SIGN_EVENTS.getTableName());

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

    public static List<SignEditEventQueryResult> queryFromAllTables(BlockPos queryPos, RegistryKey<World> queryWorld, MinecraftServer server){

        EnumSet<DatabaseTables> allTables = EnumSet.allOf(DatabaseTables.class);
        return queryFromTables(queryPos, queryWorld, server, allTables);

    }

    public static List<SignEditEventQueryResult> queryFromTables(BlockPos queryPos, RegistryKey<World> queryWorld,MinecraftServer server, EnumSet<DatabaseTables> selectedTables){

        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");
        String blockPosAsString = SignEditEvent.getBlockPosAsLogString(queryPos);
        String worldRegistryKeyAsString = queryWorld.toString();
        List<SignEditEventQueryResult> signEditEventQueryResults = new ArrayList<>();

        try(Connection connection = DriverManager.getConnection(url)){

            for(DatabaseTables signEditEvent : selectedTables){

                try(PreparedStatement preparedStatement = connection.prepareStatement(
                        "SELECT * FROM %s WHERE block_pos=? AND world_registry_key=?".formatted(signEditEvent.getTableName()))){

                    preparedStatement.setString(1, blockPosAsString);
                    preparedStatement.setString(2, worldRegistryKeyAsString);

                    try (ResultSet resultSet = preparedStatement.executeQuery()){

                        Optional<List<SignEditEventQueryResult>> signEditEventQueryResultOptional = signEditEvent.processResultSet(resultSet);
                        signEditEventQueryResultOptional.ifPresent(signEditEventQueryResults::addAll);

                    }

                }

            }

        } catch (SQLException e) {
            SignLogger.LOGGER.error("Error querying from sign-logger database " + e);
        }
        return signEditEventQueryResults;
    }

    public static int purgeOldEntries(int daysThreshold, MinecraftServer server) {
        String url = "jdbc:sqlite:" + server.getSavePath(WorldSavePath.ROOT).resolve("sign-logger.db");
        int totalDeletedRows = 0;
        try (Connection connection = DriverManager.getConnection(url)) {
            for(DatabaseTables signEditEvent : DatabaseTables.values()){
                totalDeletedRows += purgeTable(connection, signEditEvent.getTableName(), daysThreshold);
            }
        } catch (SQLException e) {
            SignLogger.LOGGER.error("Error attempting to purge databases: " + e);
        }
        return totalDeletedRows;
    }

    private static int purgeTable(Connection connection, String tableName, int daysThreshold) throws SQLException {
        int deletedRows = 0;
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM " + tableName + " WHERE timestamp < ?")) {

            LocalDateTime thresholdDateTime = LocalDateTime.now().minusDays(daysThreshold);
            Timestamp thresholdTimestamp = Timestamp.valueOf(thresholdDateTime);
            preparedStatement.setTimestamp(1, thresholdTimestamp);
            deletedRows = preparedStatement.executeUpdate();

        } catch (SQLException e) {
            SignLogger.LOGGER.error("Error attempting to purge " + tableName + " table: " + e);
        }
        return deletedRows;
    }

}
