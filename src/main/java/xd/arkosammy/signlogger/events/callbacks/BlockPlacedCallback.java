package xd.arkosammy.signlogger.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;

public interface BlockPlacedCallback {

    Event<BlockPlacedCallback> EVENT = EventFactory.createArrayBacked(BlockPlacedCallback.class,
            (listeners) -> (context -> {
                for(BlockPlacedCallback listener : listeners){
                    ActionResult result = listener.onBlockPlacedCallback(context);
                    if(result != ActionResult.PASS){
                        return result;
                    }
                }
                return ActionResult.PASS;
            })) ;

    ActionResult onBlockPlacedCallback(ItemPlacementContext context);

}
