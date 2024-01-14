package xd.arkosammy.signlogger.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xd.arkosammy.signlogger.events.callbacks.BlockBreakStartCallback;

@Mixin(targets = "net.minecraft.block.AbstractBlock$AbstractBlockState")
public abstract class BlockBreakStartMixin {

    @Shadow protected abstract BlockState asBlockState();

    @Inject(method = "onBlockBreakStart", at = @At("HEAD"), cancellable = true)
    private void onBlockBreakStart(World world, BlockPos pos, PlayerEntity player, CallbackInfo ci){
        boolean result = BlockBreakStartCallback.EVENT.invoker().onBlockBreakStartCallback(world, pos, this.asBlockState(), player);
        if(!result){
            ci.cancel();
        }
    }

}
