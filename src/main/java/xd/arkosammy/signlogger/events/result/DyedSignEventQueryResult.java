package xd.arkosammy.signlogger.events.result;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xd.arkosammy.signlogger.events.SignEditEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public record DyedSignEventQueryResult(String author,
                                       String blockPos,
                                       String worldRegistryKey,
                                       String oldColorName,
                                       String newColorName,
                                       LocalDateTime timestamp,
                                       boolean isFrontSide) implements SignEditEventQueryResult {

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getBlockPos() {
        return this.blockPos;
    }

    @Override
    public MutableText getQueryResultText() {

        String author = this.author();
        String pos = this.blockPos();
        String worldRegistryKey = this.getWorldRegistryKeyForDisplay();
        String oldColorName = this.oldColorName;
        String newColorName = this.newColorName;
        LocalDateTime localDateTime = this.timestamp();
        boolean isFrontSide = this.isFrontSide();
        Duration timeSinceLog = Duration.between(localDateTime, LocalDateTime.now());

        MutableText durationText = Text.literal(SignEditEventQueryResult.formatElapsedTime(timeSinceLog) + " ")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(localDateTime.format(SignEditEvent.DTF)))))
                .formatted(Formatting.GRAY);
        MutableText authorText = Text.literal(author + " ")
                .formatted(Formatting.BLUE);
        MutableText editedSignText = Text.literal("changed the color of the ")
                .formatted(Formatting.GRAY);
        MutableText sideText = Text.literal((isFrontSide ? "front" : "back") + "-side text ")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("From " + oldColorName + " to " + newColorName))))
                .formatted(Formatting.BLUE);
        MutableText middleText = Text.literal("of a sign at ")
                .formatted(Formatting.GRAY);
        MutableText positionText = Text.literal(pos + " ")
                .formatted(Formatting.BLUE);
        MutableText preWorldText = Text.literal("in ")
                .formatted(Formatting.GRAY);
        MutableText worldText = Text.literal(worldRegistryKey)
                .formatted(Formatting.BLUE);

        MutableText logLineText = Text.empty().append(durationText).append(authorText).append(editedSignText).append(sideText).append(middleText).append(positionText).append(preWorldText).append(worldText);
        return logLineText;
    }

    private String getWorldRegistryKeyForDisplay() {

        String worldName = this.worldRegistryKey;
        int colonCharIndex = worldRegistryKey.lastIndexOf(':');
        if (colonCharIndex != -1) {
            worldName = worldRegistryKey.substring(colonCharIndex + 1, worldRegistryKey.length() - 1);
        }
        return worldName;

    }

}
