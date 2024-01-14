package xd.arkosammy.signlogger.events.callbacks;

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
                    boolean result = listener.onBlockBreakStartCallback(world, pos, state, playerEntity);
                    if(!result){
                        return false;
                    }
                }
                return true;
            })));

    boolean onBlockBreakStartCallback(World world, BlockPos pos, BlockState state, PlayerEntity playerEntity);

}
