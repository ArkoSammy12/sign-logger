package xd.arkosammy.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.database.DatabaseManager;
import xd.arkosammy.events.InspectionModeInterface;
import xd.arkosammy.events.SignEditEventResult;

import java.util.List;
import java.util.Optional;

public abstract class InspectMode {

    private InspectMode(){}

    public static boolean handleInspectModeQuery(PlayerEntity playerEntity, BlockPos blockPos, World world){

        Optional<List<SignEditEventResult>> signEditEventResultListOptional = DatabaseManager.queryFromBlockPos(blockPos, world.getServer());
        if(signEditEventResultListOptional.isEmpty()){
            return false;
        }
        List<SignEditEventResult> signEditEventResults = signEditEventResultListOptional.get();
        if(signEditEventResults.isEmpty()){
            return false;
        }
        for(SignEditEventResult signEditEventResult : signEditEventResults){
            playerEntity.sendMessage(Text.literal(signEditEventResult.toString()));
        }
        return true;

    }

}
