package xd.arkosammy.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.ArgumentCommandNode;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import xd.arkosammy.events.InspectionModeInterface;

public abstract class InspectModeToggle {

    private InspectModeToggle(){}

     static void register(LiteralCommandNode<ServerCommandSource> signLoggerNode){

        //Inspect mode node
        LiteralCommandNode<ServerCommandSource> inspectModeNode = CommandManager
                .literal("inspect")
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(InspectModeToggle::getInspectModeCommand)
                .build();

        //Inspect mode argument node
        ArgumentCommandNode<ServerCommandSource, Boolean> inspectModeArgumentNode = CommandManager
                .argument("value", BoolArgumentType.bool())
                .requires(serverCommandSource -> serverCommandSource.hasPermissionLevel(4))
                .executes(InspectModeToggle::setInspectModeCommand)
                .build();

        //Root node connections
        signLoggerNode.addChild(inspectModeNode);

        //Argument nodes
        inspectModeNode.addChild(inspectModeArgumentNode);

    }

    private static int setInspectModeCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        ((InspectionModeInterface)ctx.getSource().getPlayerOrThrow()).sign_logger$setIsInspecting(BoolArgumentType.getBool(ctx, "value"));
        ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("Inspect mode has been: " + (BoolArgumentType.getBool(ctx, "value") ? "enabled" : "disabled")));
        return Command.SINGLE_SUCCESS;

    }

    private static int getInspectModeCommand(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {

        ctx.getSource().getPlayerOrThrow().sendMessage(Text.literal("Inspect mode is currently: " + ((((InspectionModeInterface)ctx.getSource().getPlayerOrThrow()).sign_logger$isInspecting()) ? "enabled" : "disabled")));
        return Command.SINGLE_SUCCESS;

    }


}
