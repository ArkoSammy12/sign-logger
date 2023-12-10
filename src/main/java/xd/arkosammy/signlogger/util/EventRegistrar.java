package xd.arkosammy.signlogger.util;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import xd.arkosammy.signlogger.SignLogger;
import xd.arkosammy.signlogger.commands.SignLoggerCommandManager;
import xd.arkosammy.signlogger.configuration.DatabaseConfig;
import xd.arkosammy.signlogger.configuration.SettingsConfig;
import xd.arkosammy.signlogger.events.InspectionModeInterface;
import xd.arkosammy.signlogger.events.callbacks.BlockBreakStartCallback;
import xd.arkosammy.signlogger.events.callbacks.BlockPlacedCallback;
import xd.arkosammy.signlogger.events.callbacks.SignEditCallback;

public abstract class EventRegistrar {

    private EventRegistrar(){}

    public static void registerEvents() {
        registerSignEditEvent();
        registerServerLifecycleEvents();
        registerCommandCallbacks();
        registerPlayerBlockBreakEvent();
        registerBlockBreakStartCallback();
        registerBlockPlacedCallback();
        registerServerPlayConnectionEvents();
        registerServerLivingEntityEvents();
    }

    private static void registerSignEditEvent() {
        SignEditCallback.EVENT.register((signEditEvent, server) -> {
            if(SettingsConfig.DO_CONSOLE_LOGGING.getEntry().getValue()) {
                SignLogger.LOGGER.info(signEditEvent.toString());
            }
            DatabaseManager.storeSignEditEvent(signEditEvent, server);
            return ActionResult.PASS;
        });
    }

    private static void registerServerLifecycleEvents() {
        ServerLifecycleEvents.SERVER_STARTING.register(DatabaseManager::initDatabase);
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            SignLogger.LOGGER.info("Initiating purge...");
            int deletedRows = DatabaseManager.purgeOldEntries(DatabaseConfig.PURGE_LOGS_OLDER_THAN_X_AMOUNT.getEntry().getValue(), server);
            SignLogger.LOGGER.info("Deleted " + deletedRows + " old sign-edit logs from the database");
        });
    }

    private static void registerCommandCallbacks() {
        CommandRegistrationCallback.EVENT.register(SignLoggerCommandManager::registerCommands);
    }

    private static void registerPlayerBlockBreakEvent() {
        PlayerBlockBreakEvents.BEFORE.register(((world, player, pos, state, blockEntity) -> {
            if (!((InspectionModeInterface) player).sign_logger$isInspecting()) {
                return true;
            }
            if (player instanceof ServerPlayerEntity serverPlayerEntity && serverPlayerEntity.isCreative() && world instanceof ServerWorld serverWorld) {
                ((InspectionModeInterface) serverPlayerEntity).sign_logger$inspect(pos, serverWorld);
            }
            return false;
        }));
    }

    private static void registerBlockBreakStartCallback() {
        BlockBreakStartCallback.EVENT.register(((world, pos, state, playerEntity) -> {
            if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity && ((InspectionModeInterface) serverPlayerEntity).sign_logger$isInspecting()) {
                ((InspectionModeInterface) serverPlayerEntity).sign_logger$inspect(pos, world);
            }
            return ActionResult.PASS;
        }));
    }

    private static void registerBlockPlacedCallback() {
        BlockPlacedCallback.EVENT.register((itemPlacementContext) -> {
            ActionResult result = ActionResult.PASS;
            PlayerEntity playerEntity = itemPlacementContext.getPlayer();
            if (playerEntity instanceof ServerPlayerEntity serverPlayerEntity && ((InspectionModeInterface) serverPlayerEntity).sign_logger$isInspecting()) {
                ((InspectionModeInterface) serverPlayerEntity).sign_logger$inspect(itemPlacementContext.getBlockPos(), (ServerWorld) itemPlacementContext.getWorld());
                result = ActionResult.FAIL;
            }
            return result;
        });
    }

    private static void registerServerPlayConnectionEvents() {
        ServerPlayConnectionEvents.DISCONNECT.register(((handler, server) -> {
            ((InspectionModeInterface) handler.getPlayer()).sign_logger$setIsInspecting(false);
        }));
    }

    private static void registerServerLivingEntityEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register(((entity, damageSource) -> {
            if (entity instanceof ServerPlayerEntity serverPlayerEntity) {
                ((InspectionModeInterface) serverPlayerEntity).sign_logger$setIsInspecting(false);
            }
        }));
    }


}
