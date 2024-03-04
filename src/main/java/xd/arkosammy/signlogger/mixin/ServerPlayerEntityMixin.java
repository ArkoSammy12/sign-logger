package xd.arkosammy.signlogger.mixin;

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
import xd.arkosammy.signlogger.events.SignEditEvent;
import xd.arkosammy.signlogger.events.result.SignEditEventQueryResult;
import xd.arkosammy.signlogger.util.DatabaseManager;
import xd.arkosammy.signlogger.util.ducks.IInspectionModeAccess;

import java.util.*;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements IInspectionModeAccess {

    @Shadow public abstract void sendMessage(Text message);

    @Unique
    private boolean isInspecting = false;

    @Unique
    private final List<List<SignEditEventQueryResult>> cachedSignEditResults = new ArrayList<>();

    @Unique
    private int pageIndex = 0;

    @Unique
    public void sign_logger$setPageIndex(int pageIndex){
        this.pageIndex = pageIndex;
    }

    @Unique
    public List<List<SignEditEventQueryResult>> sign_logger$getCachedSignEditResults(){
        return this.cachedSignEditResults;
    }

    @Unique
    public boolean sign_logger$isInspecting(){
        return this.isInspecting;
    }

    @Unique
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

        List<SignEditEventQueryResult> signEditEventQueryResults = DatabaseManager.queryFromAllTables(blockPos, world.getRegistryKey(), world.getServer());
        if(signEditEventQueryResults.isEmpty()){
            this.sendMessage(Text.literal("No logs found for this coordinate").formatted(Formatting.RED));
            return;
        }

        signEditEventQueryResults.sort(Comparator.comparing(SignEditEventQueryResult::getTimestamp).reversed());
        cachedSignEditResults.clear();
        pageIndex = 0;

        // Paginate sign edit logs
        for (int i = 0; i < signEditEventQueryResults.size(); i++) {
            if (i % 10 == 0) {
                List<SignEditEventQueryResult> page = new ArrayList<>();
                cachedSignEditResults.add(page);
            }
            cachedSignEditResults.get(cachedSignEditResults.size() - 1).add(signEditEventQueryResults.get(i));
        }

        this.sign_logger$showPage();

    }

    @Unique
    public void sign_logger$showPage(){

        String blockPosHeader = SignEditEvent.getBlockPosAsLogString(this.cachedSignEditResults.get(0).get(0).getBlockPos());
        MutableText headerText = Text.literal(String.format("-- Searching sign logs at %s --", blockPosHeader))
                .formatted(Formatting.GREEN);
        this.sendMessage(headerText);
        MutableText logLines = Text.literal("");
        Iterator<SignEditEventQueryResult> signEditEventResultIterator = this.cachedSignEditResults.get(pageIndex).iterator();

        while(signEditEventResultIterator.hasNext()){

            SignEditEventQueryResult signEditEventQueryResult = signEditEventResultIterator.next();
            MutableText logLineText  = signEditEventQueryResult.getQueryResultText();
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

        MutableText footerText = Text.empty().append(footerPrefix).append(footerPreviousPage).append(footerMiddle).append(footerNextPage).append(footerSuffix);
        this.sendMessage(footerText);

    }


}
