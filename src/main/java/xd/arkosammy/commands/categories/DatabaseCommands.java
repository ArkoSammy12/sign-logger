package xd.arkosammy.commands.categories;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xd.arkosammy.configuration.tables.DatabaseConfig;
import xd.arkosammy.util.DatabaseManager;

public abstract class DatabaseCommands {

    private DatabaseCommands(){}

    public static void register(LiteralCommandNode<ServerCommandSource> signLoggerNode){

        LiteralCommandNode<ServerCommandSource> databaseNode = CommandManager
                .literal("database")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        LiteralCommandNode<ServerCommandSource> purgeNode = CommandManager
                .literal("purge")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(DatabaseCommands::purgeCommand)
                .build();

        LiteralCommandNode<ServerCommandSource> purgeOlderThanXDaysNode = CommandManager
                .literal("purge_older_than_x_days")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(DatabaseCommands::getPurgeOlderThanXDaysCommand)
                .build();

        ArgumentCommandNode<ServerCommandSource, Integer> purgeOlderThanXDaysArgumentNode = CommandManager
                .argument("value", IntegerArgumentType.integer())
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(DatabaseCommands::setPurgeOlderThanXDaysCommand)
                .build();


        //Root node connections
        signLoggerNode.addChild(databaseNode);

        //Data base node connection
        databaseNode.addChild(purgeNode);
        databaseNode.addChild(purgeOlderThanXDaysNode);

        //Argument node connections
        purgeOlderThanXDaysNode.addChild(purgeOlderThanXDaysArgumentNode);

    }

    private static int purgeCommand(CommandContext<ServerCommandSource> ctx){

        ctx.getSource().sendMessage(Text.literal("Initiating purge...").formatted(Formatting.AQUA));
        DatabaseManager.purgeOldEntries(DatabaseConfig.PURGE_LOGS_OLDER_THAN_X_AMOUNT.getEntry().getValue(), ctx.getSource().getServer());
        return Command.SINGLE_SUCCESS;

    }

    private static int setPurgeOlderThanXDaysCommand(CommandContext<ServerCommandSource> ctx){
        int purgeOlderThanXDays = IntegerArgumentType.getInteger(ctx, "value");
        if(purgeOlderThanXDays < 1){
            ctx.getSource().sendMessage(Text.literal("Cannot set \"purge_older_than_x_days\" setting to 0 or lower").formatted(Formatting.RED));
        } else {
            DatabaseConfig.PURGE_LOGS_OLDER_THAN_X_AMOUNT.getEntry().setValue(purgeOlderThanXDays);
            ctx.getSource().sendMessage(Text.literal("\"purge_older_than_x_days\" setting has been set to: " + purgeOlderThanXDays));

        }
        return Command.SINGLE_SUCCESS;
    }

    private static int getPurgeOlderThanXDaysCommand(CommandContext<ServerCommandSource> ctx){
        ctx.getSource().sendMessage(Text.literal("\"purge_older_than_x_days\" setting currently set to: " + DatabaseConfig.PURGE_LOGS_OLDER_THAN_X_AMOUNT.getEntry().getValue()));
        return Command.SINGLE_SUCCESS;
    }

}
