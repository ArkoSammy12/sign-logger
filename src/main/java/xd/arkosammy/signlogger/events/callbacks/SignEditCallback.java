package xd.arkosammy.signlogger.events.callbacks;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import xd.arkosammy.signlogger.events.SignEditEvent;


public interface SignEditCallback {

    Event<SignEditCallback> EVENT = EventFactory.createArrayBacked(SignEditCallback.class,
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
