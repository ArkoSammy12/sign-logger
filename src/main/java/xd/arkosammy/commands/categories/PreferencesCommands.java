package xd.arkosammy.commands.categories;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xd.arkosammy.configuration.tables.PreferencesConfig;

public abstract class PreferencesCommands {

    private PreferencesCommands(){}

    public static void register(LiteralCommandNode<ServerCommandSource> signLogger){

        LiteralCommandNode<ServerCommandSource> preferencesNode = CommandManager
                .literal("preferences")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        LiteralCommandNode<ServerCommandSource> doConsoleLoggingNode = CommandManager
                .literal("do_console_logging")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(PreferencesCommands::getDoConsoleLoggingCommand)
                .build();

        ArgumentCommandNode<ServerCommandSource, Boolean> doConsoleLoggingArgumentNode = CommandManager
                .argument("value", BoolArgumentType.bool())
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(PreferencesCommands::setDoConsoleLoggingCommand)
                .build();

        //Root node connection
        signLogger.addChild(preferencesNode);

        //Preferences node connection
        preferencesNode.addChild(doConsoleLoggingNode);

        //Argument node connection
        doConsoleLoggingNode.addChild(doConsoleLoggingArgumentNode);

    }

    private static int setDoConsoleLoggingCommand(CommandContext<ServerCommandSource> ctx){
        boolean doConsoleLogging = BoolArgumentType.getBool(ctx, "value");
        PreferencesConfig.DO_CONSOLE_LOGGING.getEntry().setValue(doConsoleLogging);
        ctx.getSource().sendMessage(Text.literal("\"do_console_logging\" has been set to: " + doConsoleLogging));
        return Command.SINGLE_SUCCESS;
    }

    private static int getDoConsoleLoggingCommand(CommandContext<ServerCommandSource> ctx){
        ctx.getSource().sendMessage(Text.literal("\"do_console_logging\" currently set to: " + PreferencesConfig.DO_CONSOLE_LOGGING.getEntry().getValue()));
        return Command.SINGLE_SUCCESS;
    }

}
