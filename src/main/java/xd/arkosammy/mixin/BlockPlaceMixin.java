package xd.arkosammy.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.events.callbacks.BlockPlacedCallback;

@Mixin(BlockItem.class)
public abstract class BlockPlaceMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void onBlockPlaced(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir){
        if(context.getWorld() instanceof ServerWorld) {
            ActionResult result = BlockPlacedCallback.EVENT.invoker().onBlockPlacedCallback(context);
            if (result == ActionResult.FAIL) {
                cir.setReturnValue(result);
            }
        }
    }

}
