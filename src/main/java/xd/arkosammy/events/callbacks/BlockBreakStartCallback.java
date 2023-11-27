package xd.arkosammy.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

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

    ActionResult onBlockBreakStartCallback(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity);

}
