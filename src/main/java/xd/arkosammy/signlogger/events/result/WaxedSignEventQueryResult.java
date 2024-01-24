package xd.arkosammy.signlogger.events.result;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xd.arkosammy.signlogger.events.SignEditEvent;

import java.time.Duration;
import java.time.LocalDateTime;

public record WaxedSignEventQueryResult(String author,
                                        String blockPos,
                                        String worldRegistryKey,
                                        LocalDateTime timestamp) implements SignEditEventQueryResult {

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
        LocalDateTime localDateTime = this.timestamp();
        Duration timeSinceLog = Duration.between(localDateTime, LocalDateTime.now());

        MutableText durationText = Text.literal(SignEditEventQueryResult.formatElapsedTime(timeSinceLog) + " ")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(localDateTime.format(SignEditEvent.DTF)))))
                .formatted(Formatting.GRAY);
        MutableText authorText = Text.literal(author + " ")
                .formatted(Formatting.BLUE);
        MutableText editedSignText = Text.literal("waxed a sign at ")
                .formatted(Formatting.GRAY);
        MutableText positionText = Text.literal(pos + " ")
                .formatted(Formatting.BLUE);
        MutableText preWorldText = Text.literal("in ")
                .formatted(Formatting.GRAY);
        MutableText worldText = Text.literal(worldRegistryKey)
                .formatted(Formatting.BLUE);

        MutableText logLineText = Text.empty().append(durationText).append(authorText).append(editedSignText).append(positionText).append(preWorldText).append(worldText);
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
