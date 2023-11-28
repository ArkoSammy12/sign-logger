package xd.arkosammy;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xd.arkosammy.commands.SignLoggerCommandManager;
import xd.arkosammy.database.DatabaseManager;
import xd.arkosammy.events.callbacks.BlockBreakStartCallback;
import xd.arkosammy.events.InspectionModeInterface;
import xd.arkosammy.events.callbacks.BlockPlacedCallback;
import xd.arkosammy.events.callbacks.SignEditCallback;
import xd.arkosammy.util.InspectMode;

//TODO: HANDLE NPE WARNINGS
//TODO: CREATE CONFIG
//TODO: FORMAT CHAT QUERY RESULTS
//TODO: IMPLEMENT DATABASE PURGE
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
		PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) -> {
			if(!((InspectionModeInterface) player).sign_logger$isInspecting()){
				return true;
			}
			if(player.isCreative()){
				InspectMode.handleInspectModeQuery(player, pos, world);
			}
			return false;
		}));
		BlockBreakStartCallback.EVENT.register(((world, pos, state, playerEntity) -> {
			if(((InspectionModeInterface)playerEntity).sign_logger$isInspecting()){
				InspectMode.handleInspectModeQuery(playerEntity, pos, world);
			}
			return ActionResult.PASS;
		}));

		BlockPlacedCallback.EVENT.register((itemPlacementContext) -> {
			ActionResult result = ActionResult.PASS;
			PlayerEntity playerEntity = itemPlacementContext.getPlayer();
			if(playerEntity instanceof ServerPlayerEntity serverPlayerEntity &&  ((InspectionModeInterface)serverPlayerEntity).sign_logger$isInspecting()) {
				InspectMode.handleInspectModeQuery(serverPlayerEntity, itemPlacementContext.getBlockPos(), itemPlacementContext.getWorld());
				result = ActionResult.FAIL;
			}
			return result;
		});


		ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
			((InspectionModeInterface)handler.getPlayer()).sign_logger$setIsInspecting(false);
		}));

		ServerLivingEntityEvents.AFTER_DEATH.register(((entity, damageSource) -> {
			if(entity instanceof ServerPlayerEntity serverPlayerEntity){
				((InspectionModeInterface)serverPlayerEntity).sign_logger$setIsInspecting(false);
			}
		}));

	}

	private static void onServerStarting(MinecraftServer server) {

		DatabaseManager.initDatabase(server);

	}

	private static void onServerStopping(MinecraftServer server) {



	}


}