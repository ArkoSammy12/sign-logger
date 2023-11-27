package xd.arkosammy;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.events.SignEditCallback;

public class SignLogger implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");

	@Override
	public void onInitialize() {

		SignEditCallback.SIGN_EDIT.register((signEditEvent) -> {
			LOGGER.info(signEditEvent.toString());
			return ActionResult.PASS;
		});

	}
}