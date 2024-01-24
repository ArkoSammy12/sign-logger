package xd.arkosammy.signlogger.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.signlogger.events.DyedSignEvent;
import xd.arkosammy.signlogger.events.callbacks.SignEditCallback;

import java.time.LocalDateTime;

@Mixin(DyeItem.class)
public abstract class DyeItemMixin {

    @Shadow @Final private DyeColor color;

    @Inject(method = "useOnSign", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;changeText(Ljava/util/function/UnaryOperator;Z)Z", shift = At.Shift.BEFORE))
    private void captureOldSignColor(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player, CallbackInfoReturnable<Boolean> cir, @Share("oldColorName") LocalRef<String> oldColorNameRef){
        oldColorNameRef.set(signBlockEntity.getText(front).getColor().asString());
    }

    @Inject(method = "useOnSign", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void onSignDyed(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player, CallbackInfoReturnable<Boolean> cir, @Share("oldColorName") LocalRef<String> oldColorNameRef) {
        if(world == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayerEntity)){
            return;
        }
        String oldColorName = oldColorNameRef.get();
        String newColorName = this.color.asString();
        BlockPos blockPos = signBlockEntity.getPos();
        LocalDateTime now = LocalDateTime.now();
        RegistryKey<World> worldRegistryKey = world.getRegistryKey();
        MinecraftServer server = world.getServer();
        DyedSignEvent dyedSignEvent = new DyedSignEvent(serverPlayerEntity, blockPos, worldRegistryKey, oldColorName, newColorName, now, front);
        SignEditCallback.EVENT.invoker().onSignEditedCallback(dyedSignEvent, server);
    }

}
