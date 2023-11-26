package xd.arkosammy;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.events.SignEditCallback;
import java.time.format.DateTimeFormatter;

public class SignLogger implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("signlogger");

	@Override
	public void onInitialize() {

		SignEditCallback.SIGN_EDIT.register((editor, pos, originalText, newText, timestamp, front) -> {

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");

			LOGGER.info("{} has edited the {}-text of a sign at position [{}, {}, {}], from \"{}\", to \"{}\", on {}", editor.getDisplayName().getString(), front ? "front" : "back", pos.getX(), pos.getY(), pos.getZ(), originalText, newText, dtf.format(timestamp));

			return ActionResult.PASS;

		});

	}
}