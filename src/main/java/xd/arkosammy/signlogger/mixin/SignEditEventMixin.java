package xd.arkosammy.signlogger.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xd.arkosammy.signlogger.events.callbacks.SignEditCallback;
import xd.arkosammy.signlogger.events.SignEditEvent;
import xd.arkosammy.signlogger.events.SignEditText;

import java.time.LocalDateTime;
import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignEditEventMixin extends BlockEntity {


	private SignEditEventMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Shadow public abstract SignText getFrontText();

	@Shadow public abstract SignText getBackText();

	@Inject(method = "tryChangeText", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;changeText(Ljava/util/function/UnaryOperator;Z)Z", shift = At.Shift.BEFORE, ordinal = 0))
	private void onSignEdited(PlayerEntity player, boolean front, List<FilteredMessage> messages, CallbackInfo ci){
		if(this.getWorld() == null || this.getWorld().isClient() || !(player instanceof ServerPlayerEntity serverPlayerEntity)){
			return;
		}
		SignText originalTextAsSignText = front ? this.getFrontText() : this.getBackText();
		BlockPos blockPos = this.getPos();
		LocalDateTime now = LocalDateTime.now();
		SignEditText originalText = new SignEditText(originalTextAsSignText);
		SignEditText  newText = new SignEditText(messages);
		RegistryKey<World> worldRegistryKey = this.getWorld().getRegistryKey();
		MinecraftServer server = this.getWorld().getServer();
		if(originalText.equals(newText)){
			return;
		}
		SignEditEvent signEditEvent = new SignEditEvent(serverPlayerEntity, blockPos, worldRegistryKey, originalText, newText, now, front);
		SignEditCallback.EVENT.invoker().onSignEditedCallback(signEditEvent, server);

	}

}