package xd.arkosammy.signlogger.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public interface BlockBreakStartCallback {

    Event<BlockBreakStartCallback> EVENT = EventFactory.createArrayBacked(BlockBreakStartCallback.class,
            (listeners) -> (((world, pos, state, playerEntity) -> {
                for(BlockBreakStartCallback listener : listeners){
                    ActionResult result = listener.onBlockBreakStartCallback(world, pos, state, playerEntity);
                    if(result != ActionResult.PASS){
                        return result;
                    }
                }
                return ActionResult.PASS;
            })));

    ActionResult onBlockBreakStartCallback(ServerWorld world, BlockPos pos, BlockState state, PlayerEntity playerEntity);

}
