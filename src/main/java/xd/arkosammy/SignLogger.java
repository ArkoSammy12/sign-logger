package xd.arkosammy;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.commands.SignLoggerCommandManager;
import xd.arkosammy.database.DatabaseManager;
import xd.arkosammy.events.SignEditCallback;

public class SignLogger implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");

	@Override
	public void onInitializeServer() {

		SignEditCallback.SIGN_EDIT.register((signEditEvent, server) -> {
			LOGGER.info(signEditEvent.toString());
			DatabaseManager.storeSignEditEvent(signEditEvent, server);
			return ActionResult.PASS;
		});

		ServerLifecycleEvents.SERVER_STARTING.register(SignLogger::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPING.register(SignLogger::onServerStopping);
		CommandRegistrationCallback.EVENT.register(SignLoggerCommandManager::registerCommands);

	}

	private static void onServerStarting(MinecraftServer server) {

		DatabaseManager.initDatabase(server);

	}

	private static void onServerStopping(MinecraftServer server) {



	}


}