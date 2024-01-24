package xd.arkosammy.signlogger.mixin;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.signlogger.events.callbacks.AttemptedBlockPlaceCallback;

@Mixin(BlockItem.class)
public abstract class BlockItemMixin {

    @Inject(method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;", at = @At("HEAD"), cancellable = true)
    private void onBlockAttemptedPlace(ItemPlacementContext context, CallbackInfoReturnable<ActionResult> cir){
        ActionResult result = AttemptedBlockPlaceCallback.EVENT.invoker().onBlockAttemptedPlace(context);
        if (result != ActionResult.PASS) {
            cir.setReturnValue(result);
        }
    }

}
