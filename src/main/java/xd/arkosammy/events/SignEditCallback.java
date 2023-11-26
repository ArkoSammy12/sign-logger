package xd.arkosammy.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

import java.time.LocalDateTime;

public interface SignEditCallback {

    Event<SignEditCallback> SIGN_EDIT = EventFactory.createArrayBacked(SignEditCallback.class,
            (listeners) -> ((editor, pos, originalText, newText, timestamp, front) -> {
                for(SignEditCallback listener : listeners){
                    ActionResult result = listener.onSignEditedCallback(editor, pos, originalText, newText, timestamp, front);
                    if(result != ActionResult.PASS){
                        return result;
                    }
                }
                return ActionResult.PASS;
            }));

    ActionResult onSignEditedCallback(PlayerEntity editor, BlockPos pos, String originalText, String newText, LocalDateTime timestamp, boolean front);

}
