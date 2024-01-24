package xd.arkosammy.signlogger.mixin;

import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoneycombItem;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xd.arkosammy.signlogger.events.WaxedSignEvent;
import xd.arkosammy.signlogger.events.callbacks.SignEditCallback;

import java.time.LocalDateTime;

@Mixin(HoneycombItem.class)
public abstract class HoneycombItemMixin {

    @Inject(method = "useOnSign", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;syncWorldEvent(Lnet/minecraft/entity/player/PlayerEntity;ILnet/minecraft/util/math/BlockPos;I)V"))
    private void onSignWaxed(World world, SignBlockEntity signBlockEntity, boolean front, PlayerEntity player, CallbackInfoReturnable<Boolean> cir){
        if(world == null || world.isClient() || !(player instanceof ServerPlayerEntity serverPlayerEntity)){
            return;
        }
        BlockPos blockPos = signBlockEntity.getPos();
        LocalDateTime now = LocalDateTime.now();
        RegistryKey<World> worldRegistryKey = world.getRegistryKey();
        MinecraftServer server = world.getServer();
        WaxedSignEvent waxedSignEvent = new WaxedSignEvent(serverPlayerEntity, blockPos, worldRegistryKey, now, front);
        SignEditCallback.EVENT.invoker().onSignEditedCallback(waxedSignEvent, server);
    }

}
