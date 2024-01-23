package xd.arkosammy.signlogger.util.visitors;

import xd.arkosammy.signlogger.events.*;

import java.sql.*;

public record SignEditEventDatabaseVisitor(Connection databaseConnection) implements SignEditEventVisitor {

    @Override
    public void visit(ChangedTextSignEvent changedTextSignEvent) {
        try (PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO sign_edit_events (author_name, block_pos, world_registry_key, original_text_line_1, original_text_line_2, original_text_line_3, original_text_line_4, new_text_line_1, new_text_line_2, new_text_line_3, new_text_line_4, timestamp, is_front_side) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, changedTextSignEvent.author().getDisplayName().getString());
            preparedStatement.setString(2, SignEditEvent.getBlockPosAsLogString(changedTextSignEvent.blockPos()));
            preparedStatement.setString(3, changedTextSignEvent.worldRegistryKey().toString());
            preparedStatement.setString(4, changedTextSignEvent.originalText().getTextLines()[0]);
            preparedStatement.setString(5, changedTextSignEvent.originalText().getTextLines()[1]);
            preparedStatement.setString(6, changedTextSignEvent.originalText().getTextLines()[2]);
            preparedStatement.setString(7, changedTextSignEvent.originalText().getTextLines()[3]);
            preparedStatement.setString(8, changedTextSignEvent.newText().getTextLines()[0]);
            preparedStatement.setString(9, changedTextSignEvent.newText().getTextLines()[1]);
            preparedStatement.setString(10, changedTextSignEvent.newText().getTextLines()[2]);
            preparedStatement.setString(11, changedTextSignEvent.newText().getTextLines()[3]);
            preparedStatement.setTimestamp(12, Timestamp.valueOf(changedTextSignEvent.timestamp()));
            preparedStatement.setBoolean(13, changedTextSignEvent.isFrontSide());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void visit(WaxedSignEvent waxedSignEvent) {

        try(PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO waxed_sign_events (author_name, block_pos, world_registry_key, timestamp) " +
                "VALUES (?, ?, ?, ?)")){

            preparedStatement.setString(1, waxedSignEvent.getAuthor().getDisplayName().getString());
            preparedStatement.setString(2, SignEditEvent.getBlockPosAsLogString(waxedSignEvent.getBlockPos()));
            preparedStatement.setString(3, waxedSignEvent.getWorldRegistryKey().toString());
            preparedStatement.setTimestamp(4, Timestamp.valueOf(waxedSignEvent.getTimestamp()));

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void visit(DyedSignEvent dyedSignEvent) {

        try(PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO dyed_sign_events (author_name, block_pos, world_registry_key, old_color, new_color, timestamp, is_front_side) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, dyedSignEvent.getAuthor().getDisplayName().getString());
            preparedStatement.setString(2, SignEditEvent.getBlockPosAsLogString(dyedSignEvent.getBlockPos()));
            preparedStatement.setString(3, dyedSignEvent.getWorldRegistryKey().toString());
            preparedStatement.setString(4, dyedSignEvent.oldColorName());
            preparedStatement.setString(5, dyedSignEvent.newColorName());
            preparedStatement.setTimestamp(6, Timestamp.valueOf(dyedSignEvent.getTimestamp()));
            preparedStatement.setBoolean(7, dyedSignEvent.isFrontSide());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void visit(GlowedSignEvent glowedSignEvent) {

        try(PreparedStatement preparedStatement = databaseConnection.prepareStatement(
                "INSERT INTO glowed_sign_events (author_name, block_pos, world_registry_key, is_applying, timestamp, is_front_side)  " +
                        "VALUES (?, ?, ?, ?, ?, ?)")) {

            preparedStatement.setString(1, glowedSignEvent.getAuthor().getDisplayName().getString());
            preparedStatement.setString(2, SignEditEvent.getBlockPosAsLogString(glowedSignEvent.getBlockPos()));
            preparedStatement.setString(3, glowedSignEvent.getWorldRegistryKey().toString());
            preparedStatement.setBoolean(4, glowedSignEvent.isApplying());
            preparedStatement.setTimestamp(5, Timestamp.valueOf(glowedSignEvent.getTimestamp()));
            preparedStatement.setBoolean(6, glowedSignEvent.isFrontSide());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
