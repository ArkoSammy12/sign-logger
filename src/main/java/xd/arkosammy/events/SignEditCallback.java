package xd.arkosammy.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;


public interface SignEditCallback {

    Event<SignEditCallback> SIGN_EDIT = EventFactory.createArrayBacked(SignEditCallback.class,
            (listeners) -> ((signEditEvent, server) -> {
                for(SignEditCallback listener : listeners){
                    ActionResult result = listener.onSignEditedCallback(signEditEvent, server);
                    if(result != ActionResult.PASS){
                        return result;
                    }
                }
                return ActionResult.PASS;
            }));

    ActionResult onSignEditedCallback(SignEditEvent signEditEvent, MinecraftServer server);

}
