package xd.arkosammy.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.filter.FilteredMessage;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xd.arkosammy.events.SignEditCallback;

import java.time.LocalDateTime;
import java.util.List;

@Mixin(SignBlockEntity.class)
public abstract class SignEditEventMixin extends BlockEntity {


	public SignEditEventMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
		super(type, pos, state);
	}

	@Shadow public abstract SignText getFrontText();

	@Shadow public abstract SignText getBackText();

	@Inject(method = "tryChangeText", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/entity/SignBlockEntity;changeText(Ljava/util/function/UnaryOperator;Z)Z", shift = At.Shift.BEFORE, ordinal = 0), cancellable = true)
	private void onSignEdited(PlayerEntity player, boolean front, List<FilteredMessage> messages, CallbackInfo ci){

		SignText originalText = front ? this.getFrontText() : this.getBackText();

		StringBuilder stringBuilder = new StringBuilder();

		for(Text text : originalText.getMessages(false)){
			stringBuilder.append(text.getString());
		}
		String originalTextAsString = stringBuilder.toString();
		stringBuilder.setLength(0);

		for(FilteredMessage message : messages){
			stringBuilder.append(message.getString());
		}
		String newTextAsString = stringBuilder.toString();
		stringBuilder.setLength(0);

		if(originalTextAsString.equals(newTextAsString)){
			return;
		}

		ActionResult result = SignEditCallback.SIGN_EDIT.invoker().onSignEditedCallback(player, this.getPos() , originalTextAsString, newTextAsString, LocalDateTime.now(), front);

		if(result == ActionResult.FAIL) {
			ci.cancel();
		}

	}

}