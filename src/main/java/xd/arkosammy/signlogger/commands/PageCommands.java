package xd.arkosammy.signlogger.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import xd.arkosammy.signlogger.events.InspectionModeInterface;

public abstract class PageCommands {

    private PageCommands(){}

     static void register(LiteralCommandNode<ServerCommandSource> signLoggerNode){

        LiteralCommandNode<ServerCommandSource> pageNode = CommandManager
                .literal("page")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .build();

        ArgumentCommandNode<ServerCommandSource, Integer> pageArgumentNode = CommandManager
                .argument("page", IntegerArgumentType.integer())
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(PageCommands::turnPage)
                .build();

        //Root node connections
        signLoggerNode.addChild(pageNode);

        //Argument nodes
        pageNode.addChild(pageArgumentNode);

    }

    private static int turnPage(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        int newPageIndex = IntegerArgumentType.getInteger(ctx, "page") - 1;

        if(newPageIndex < 0 || newPageIndex > ((InspectionModeInterface)ctx.getSource().getPlayerOrThrow()).sign_logger$getCachedSignEditResults().size() - 1){
            ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("No more pages to show").formatted(Formatting.RED));
            return Command.SINGLE_SUCCESS;
        }

        ((InspectionModeInterface)ctx.getSource().getPlayerOrThrow()).sign_logger$setPageIndex(newPageIndex);
        ((InspectionModeInterface)ctx.getSource().getPlayerOrThrow()).sign_logger$showPage();
        return Command.SINGLE_SUCCESS;

    }

}
