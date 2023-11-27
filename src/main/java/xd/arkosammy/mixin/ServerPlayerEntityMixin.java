package xd.arkosammy.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xd.arkosammy.events.InspectionModeInterface;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin implements InspectionModeInterface {

    @Unique
    private boolean isInspecting;

    public boolean sign_logger$isInspecting(){
        return this.isInspecting;
    }

    public void sign_logger$setIsInspecting(boolean isInspecting){
        this.isInspecting = isInspecting;
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    public void writeCustomDataToNbt (NbtCompound tag, CallbackInfo info) {
        tag.putBoolean("isInspecting", isInspecting);
    }

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    public void readCustomDataFromNbt(NbtCompound tag, CallbackInfo info) {
        isInspecting = tag.getBoolean("isInspecting");
    }

}
