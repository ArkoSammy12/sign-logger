package xd.arkosammy.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import xd.arkosammy.commands.categories.InspectModeToggle;

public abstract class SignLoggerCommandManager {

    //TODO: FINALIZE COMMAND SYSTEM

    private SignLoggerCommandManager(){}

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){

        LiteralCommandNode<ServerCommandSource> signLoggerNode = CommandManager
                .literal("sign_logger")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        dispatcher.getRoot().addChild(signLoggerNode);
        InspectModeToggle.register(signLoggerNode);

    }

}
