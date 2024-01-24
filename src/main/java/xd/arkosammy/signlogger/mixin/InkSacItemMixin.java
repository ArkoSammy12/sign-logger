package xd.arkosammy.signlogger.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.InkSacItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.signlogger.events.GlowedSignEvent;
import xd.arkosammy.signlogger.events.callbacks.SignEditCallback;

import java.time.LocalDateTime;

@Mixin(InkSacItem.class)
public abstract class InkSacItemMixin {

    @Inject(method = "useOnSign", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;playSound(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/sound/SoundEvent;Lnet/minecraft/sound/SoundCategory;FF)V"))
    private void onSignUnGlowed(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if(world == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayerEntity)){
            return;
        }
        BlockPos blockPos = signBlockEntity.getPos();
        LocalDateTime now = LocalDateTime.now();
        RegistryKey<World> worldRegistryKey = world.getRegistryKey();
        MinecraftServer server = world.getServer();
        GlowedSignEvent waxedSignEvent = new GlowedSignEvent(serverPlayerEntity, blockPos, worldRegistryKey, false, now, front);
        SignEditCallback.EVENT.invoker().onSignEditedCallback(waxedSignEvent, server);
    }

}
