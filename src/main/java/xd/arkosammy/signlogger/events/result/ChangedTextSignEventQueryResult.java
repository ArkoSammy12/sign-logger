package xd.arkosammy.signlogger.events.result;

import net.minecraft.registry.RegistryKey;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.signlogger.events.SignEditEvent;
import xd.arkosammy.signlogger.events.SignEditText;

import java.time.Duration;
import java.time.LocalDateTime;

public record ChangedTextSignEventQueryResult(String author,
                                              String blockPosLogString,
                                              String worldResourceKey,
                                              SignEditText originalText,
                                              SignEditText newText,
                                              LocalDateTime timestamp,
                                              boolean isFrontSide) implements SignEditEventQueryResult {

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public BlockPos getBlockPos() {
        return SignEditEventQueryResult.fromBlockPosLogString(this.blockPosLogString);
    }

    @Override
    public RegistryKey<World> getWorldRegistryKey(){
        return SignEditEventQueryResult.fromResourceKeyString(this.worldResourceKey);
    }

    @Override
    public MutableText getQueryResultText() {

        String author = this.author();
        String pos = this.blockPosLogString();
        String worldRegistryKey = this.getWorldRegistryKey().getValue().toString();
        SignEditText originalText = this.originalText();
        SignEditText newText = this.newText();
        LocalDateTime localDateTime = this.timestamp();
        boolean isFrontSide = this.isFrontSide();
        Duration timeSinceLog = Duration.between(localDateTime, LocalDateTime.now());

        MutableText durationText = Text.literal(SignEditEventQueryResult.formatElapsedTime(timeSinceLog) + " ")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal(localDateTime.format(SignEditEvent.DTF)))))
                .formatted(Formatting.GRAY);
        MutableText authorText = Text.literal(author + " ")
                .formatted(Formatting.BLUE);
        MutableText editedSignText = Text.literal("edited the ")
                .formatted(Formatting.GRAY);
        MutableText sideText = Text.literal((isFrontSide ? "front" : "back") + "-side text ")
                .setStyle(Style.EMPTY.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("From " + originalText.toString() + " to " + newText.toString()))))
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

}
