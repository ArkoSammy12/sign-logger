package xd.arkosammy;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.commands.SignLoggerCommandManager;
import xd.arkosammy.database.DatabaseManager;
import xd.arkosammy.events.callbacks.BlockBreakStartCallback;
import xd.arkosammy.events.InspectionModeInterface;
import xd.arkosammy.events.callbacks.SignEditCallback;
import xd.arkosammy.events.SignEditEventResult;

import java.util.List;
import java.util.Optional;

public class SignLogger implements DedicatedServerModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("sign-logger");

	@Override
	public void onInitializeServer() {

		SignEditCallback.EVENT.register((signEditEvent, server) -> {
			LOGGER.info(signEditEvent.toString());
			DatabaseManager.storeSignEditEvent(signEditEvent, server);
			return ActionResult.PASS;
		});

		ServerLifecycleEvents.SERVER_STARTING.register(SignLogger::onServerStarting);
		ServerLifecycleEvents.SERVER_STOPPING.register(SignLogger::onServerStopping);
		CommandRegistrationCallback.EVENT.register(SignLoggerCommandManager::registerCommands);
		PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) -> !((InspectionModeInterface) player).sign_logger$isInspecting()));
		BlockBreakStartCallback.EVENT.register(((world, pos, state, playerEntity) -> {

			if(((InspectionModeInterface)playerEntity).sign_logger$isInspecting()){

				Optional<List<SignEditEventResult>> signEditEventResultListOptional = DatabaseManager.queryFromBlockPos(pos, world.getServer());
				if(signEditEventResultListOptional.isEmpty()){
					return ActionResult.PASS;
				}
				List<SignEditEventResult> signEditEventResults = signEditEventResultListOptional.get();
				if(signEditEventResults.isEmpty()){
					return ActionResult.PASS;
				}

				for(SignEditEventResult signEditEventResult : signEditEventResults){
					playerEntity.sendMessage(Text.literal(signEditEventResult.toString()));
				}
				return ActionResult.PASS;
			}

			return ActionResult.PASS;

		}));

	}

	private static void onServerStarting(MinecraftServer server) {

		DatabaseManager.initDatabase(server);

	}

	private static void onServerStopping(MinecraftServer server) {



	}


}