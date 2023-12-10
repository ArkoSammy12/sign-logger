package xd.arkosammy.signlogger.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xd.arkosammy.signlogger.configuration.SignLoggerConfig;

import java.io.IOException;

public abstract class SignLoggerCommandManager {

    private SignLoggerCommandManager(){}

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandRegistryAccess, CommandManager.RegistrationEnvironment registrationEnvironment){

        LiteralCommandNode<ServerCommandSource> signLoggerNode = CommandManager
                .literal("sign-logger")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        //Reload config node
        LiteralCommandNode<ServerCommandSource> reloadNode = CommandManager
                .literal("reload_config")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(context -> {

                    try {
                        SignLoggerCommandManager.reload(context);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    return Command.SINGLE_SUCCESS;

                })
                .build();

        dispatcher.getRoot().addChild(signLoggerNode);
        signLoggerNode.addChild(reloadNode);
        InspectModeToggle.register(signLoggerNode);
        PageCommands.register(signLoggerNode);
        SettingsCommands.register(signLoggerNode);
        DatabaseCommands.register(signLoggerNode);
        SearchCommand.register(signLoggerNode);

    }

    private static void reload(CommandContext<ServerCommandSource> ctx) throws IOException {
        //If this returns true, then the config file exists, and we can update our values from it
        if(SignLoggerConfig.reloadConfigSettingsInMemory(ctx)) ctx.getSource().sendMessage(Text.literal("SignLoggerConfig successfully reloaded"));
        else ctx.getSource().sendMessage(Text.literal("Found no existing config file to reload values from").formatted(Formatting.RED));
    }

}
