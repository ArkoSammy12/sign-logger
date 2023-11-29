package xd.arkosammy.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xd.arkosammy.util.DatabaseManager;
import xd.arkosammy.events.InspectionModeInterface;
import xd.arkosammy.events.SignEditEvent;
import xd.arkosammy.events.SignEditEventResult;
import xd.arkosammy.events.SignEditText;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements InspectionModeInterface {

    @Shadow public abstract void sendMessage(Text message);

    @Unique
    private boolean isInspecting;

    @Unique
    private final List<List<SignEditEventResult>> cachedSignEditResults = new ArrayList<>();

    @Unique
    private int pageIndex = 0;

    public void sign_logger$setPageIndex(int pageIndex){
        this.pageIndex = pageIndex;
    }

    public List<List<SignEditEventResult>> sign_logger$getCachedSignEditResults(){
        return this.cachedSignEditResults;
    }

    public boolean sign_logger$isInspecting(){
        return this.isInspecting;
    }

    public void sign_logger$setIsInspecting(boolean isInspecting){
        this.isInspecting = isInspecting;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void writeCustomDataToNbt (NbtCompound tag, CallbackInfo info) {
        tag.putBoolean("isInspecting", isInspecting);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void readCustomDataFromNbt(NbtCompound tag, CallbackInfo info) {
        isInspecting = tag.getBoolean("isInspecting");
    }


    @Unique
    public void sign_logger$inspect(BlockPos blockPos, ServerWorld world){
        Optional<List<SignEditEventResult>> signEditEventResultListOptional = DatabaseManager.queryFromBlockPos(blockPos, world.getServer());
        if(signEditEventResultListOptional.isEmpty()){
            this.sendMessage(Text.literal("No logs found for this coordinate").formatted(Formatting.RED));
            return;
        }
        List<SignEditEventResult> signEditEventResults = signEditEventResultListOptional.get();
        if(signEditEventResults.isEmpty()){
            this.sendMessage(Text.literal("No logs found for this coordinate").formatted(Formatting.RED));
            return;
        }

        signEditEventResults.sort(Comparator.comparing(SignEditEventResult::getLocalDateTime).reversed());

        cachedSignEditResults.clear();
        pageIndex = 0;


        for (int i = 0; i < signEditEventResults.size(); i++) {
            if (i % 10 == 0) {
                List<SignEditEventResult> page = new ArrayList<>();
                cachedSignEditResults.add(page);
            }

            cachedSignEditResults.get(cachedSignEditResults.size() - 1).add(signEditEventResults.get(i));
        }

        this.sign_logger$showPage();

    }

    @Unique
    public void sign_logger$showPage(){

        String blockPosHeader = this.cachedSignEditResults.get(0).get(0).getBlockPos();
        MutableText headerText = Text.literal(String.format("-- Searching sign logs at %s --", blockPosHeader))
                .formatted(Formatting.GREEN);
        this.sendMessage(headerText);
        MutableText logLines = Text.literal("");
        Iterator<SignEditEventResult> signEditEventResultIterator = this.cachedSignEditResults.get(pageIndex).iterator();

        while(signEditEventResultIterator.hasNext()){

            SignEditEventResult signEditEventResult = signEditEventResultIterator.next();

            String author = signEditEventResult.getAuthor();
            String pos = signEditEventResult.getBlockPos();
            String worldRegistryKey = signEditEventResult.getWorldRegistryKey();
            SignEditText originalText = signEditEventResult.getOriginalText();
            SignEditText newText = signEditEventResult.getNewText();
            LocalDateTime localDateTime = signEditEventResult.getLocalDateTime();
            boolean isFrontSide = signEditEventResult.isFrontSide();
            Duration timeSinceLog = Duration.between(localDateTime, LocalDateTime.now());

            MutableText durationText = Text.literal(formatElapsedTime(timeSinceLog) + " ")
                    .setStyle(Style.EMPTY.withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal(localDateTime.format(SignEditEvent.DTF)))))
                    .formatted(Formatting.GRAY);
            MutableText authorText = Text.literal(author + " ")
                    .formatted(Formatting.BLUE);
            MutableText editedSignText = Text.literal("edited the ")
                    .formatted(Formatting.GRAY);
            MutableText sideText = Text.literal((isFrontSide ? "front" : "back") + "-side text ")
                    .setStyle(Style.EMPTY.withHoverEvent(HoverEvent.Action.SHOW_TEXT.buildHoverEvent(Text.literal("From " + originalText.toString() + " to " + newText.toString()))))
                    .formatted(Formatting.BLUE);
            MutableText middleText = Text.literal("of a sign at ")
                    .formatted(Formatting.GRAY);
            MutableText positionText = Text.literal(pos + " ")
                    .formatted(Formatting.BLUE);
            MutableText preWorldText = Text.literal("in ")
                    .formatted(Formatting.GRAY);
            MutableText worldText = Text.literal(SignEditEvent.getWorldRegistryKeyAsAltString(worldRegistryKey))
                    .formatted(Formatting.BLUE);

            MutableText logLineText = durationText.append(authorText).append(editedSignText).append(sideText).append(middleText).append(positionText).append(preWorldText).append(worldText);
            logLines.append((signEditEventResultIterator.hasNext() ? logLineText.append("\n") : logLineText));

        }

        this.sendMessage(logLines);

        MutableText footerPrefix = Text.literal("--- ")
                .formatted(Formatting.GREEN);
        MutableText footerPreviousPage = Text.literal("<< ")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/sign-logger page %d", this.pageIndex))))
                .formatted(Formatting.DARK_GREEN);
        MutableText footerMiddle = Text.literal(String.format("Showing page [%d of %d] ", pageIndex + 1, this.cachedSignEditResults.size()))
                .formatted(Formatting.GREEN);
        MutableText footerNextPage = Text.literal(">> ")
                .setStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, String.format("/sign-logger page %d", this.pageIndex + 2))))
                .formatted(Formatting.DARK_GREEN);
        MutableText footerSuffix = Text.literal("---")
                .formatted(Formatting.GREEN);

        this.sendMessage(footerPrefix.append(footerPreviousPage.append(footerMiddle.append(footerNextPage.append(footerSuffix)))));

    }

    @Unique
    private static String formatElapsedTime(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;
        String result = seconds + "s ago";
        if (minutes > 0) {
            result = minutes + "m ago";
        }
        if (hours > 0) {
            result = hours + "h ago";
        }
        if (days > 0) {
            result = days + "d ago";
        }
        return result;
    }

}
