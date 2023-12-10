package xd.arkosammy.signlogger.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public abstract class SearchCommand {

    private SearchCommand(){}

    static void register(LiteralCommandNode<ServerCommandSource> signLoggerNode){

        LiteralCommandNode<ServerCommandSource> searchNode = CommandManager
                .literal("search")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        ArgumentCommandNode<ServerCommandSource, String> searchArgumentNode = CommandManager
                .argument("query" , StringArgumentType.string())
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(SearchCommand::searchCommand)
                .build();

        //Root connections
        signLoggerNode.addChild(searchNode);

        //Argument connections
        searchNode.addChild(searchArgumentNode);

    }

    private static int searchCommand(CommandContext<ServerCommandSource> ctx){

        String query = StringArgumentType.getString(ctx, "query");

        //SearchQuery searchQuery = new SearchQuery(query);

        //System.out.println(searchQuery);
        return Command.SINGLE_SUCCESS;
    }

}
