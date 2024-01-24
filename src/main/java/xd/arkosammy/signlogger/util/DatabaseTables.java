package xd.arkosammy.signlogger.util;

import xd.arkosammy.signlogger.events.SignEditText;
import xd.arkosammy.signlogger.events.result.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public enum DatabaseTables {
    CHANGED_TEXT_EVENTS("sign_edit_events", (resultSet) -> {
        ArrayList<SignEditEventQueryResult> changedTextSignEventQueryResults = new ArrayList<>();
        try {
            while (resultSet.next()) {

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

                ChangedTextSignEventQueryResult changedTextSignEventQueryResult = new ChangedTextSignEventQueryResult(author, pos, world, new SignEditText(originalText), new SignEditText(newText), timestamp, isFrontSide);
                changedTextSignEventQueryResults.add(changedTextSignEventQueryResult);
            }
            return Optional.of(changedTextSignEventQueryResults);
        } catch (SQLException e){
            return Optional.empty();
        }
    }),
    WAXED_SIGN_EVENTS("waxed_sign_events", (resultSet) -> {
        ArrayList<SignEditEventQueryResult> waxedSignEventQueryResults = new ArrayList<>();
        try {
            while (resultSet.next()) {

                String author = resultSet.getString("author_name");
                String pos = resultSet.getString("block_pos");
                String world = resultSet.getString("world_registry_key");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();

                WaxedSignEventQueryResult waxedSignEventQueryResult = new WaxedSignEventQueryResult(author, pos, world, timestamp);
                waxedSignEventQueryResults.add(waxedSignEventQueryResult);
            }
            return Optional.of(waxedSignEventQueryResults);
        } catch (SQLException e){
            return Optional.empty();
        }
    }),
    DYED_SIGN_EVENTS("dyed_sign_events", (resultSet) -> {
        ArrayList<SignEditEventQueryResult> dyedSignEventQueryResults = new ArrayList<>();
        try {
            while (resultSet.next()) {

                String author = resultSet.getString("author_name");
                String pos = resultSet.getString("block_pos");
                String world = resultSet.getString("world_registry_key");
                String oldColorName = resultSet.getString("old_color");
                String newColorName = resultSet.getString("new_color");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
                boolean isFrontSide = resultSet.getBoolean("is_front_side");

                DyedSignEventQueryResult dyedSignEventQueryResult = new DyedSignEventQueryResult(author, pos, world, oldColorName, newColorName, timestamp, isFrontSide);
                dyedSignEventQueryResults.add(dyedSignEventQueryResult);
            }
            return Optional.of(dyedSignEventQueryResults);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }),
    GLOWED_SIGN_EVENTS("glowed_sign_events", (resultSet) -> {
        ArrayList<SignEditEventQueryResult> glowedSignEventQueryResults = new ArrayList<>();
        try {
            while (resultSet.next()) {

                String author = resultSet.getString("author_name");
                String pos = resultSet.getString("block_pos");
                String world = resultSet.getString("world_registry_key");
                boolean isApplying = resultSet.getBoolean("is_applying");
                LocalDateTime timestamp = resultSet.getTimestamp("timestamp").toLocalDateTime();
                boolean isFrontSide = resultSet.getBoolean("is_front_side");

                GlowedSignEventQueryResult glowedSignEventQueryResult = new GlowedSignEventQueryResult(author, pos, world, isApplying, timestamp, isFrontSide);
                glowedSignEventQueryResults.add(glowedSignEventQueryResult);
            }
            return Optional.of(glowedSignEventQueryResults);
        } catch (SQLException e){
            return Optional.empty();
        }
    });

    private final String tableName;

    private final Function<ResultSet, Optional<List<SignEditEventQueryResult>>> resultSetProcessor;

    DatabaseTables(String tableName, Function<ResultSet, Optional<List<SignEditEventQueryResult>>> resultSetProcessor){
        this.tableName = tableName;
        this.resultSetProcessor = resultSetProcessor;
    }

    public String getTableName(){
        return this.tableName;
    }

    Optional<List<SignEditEventQueryResult>> processResultSet(ResultSet resultSet){
        return resultSetProcessor.apply(resultSet);
    }

}
