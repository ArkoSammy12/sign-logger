package xd.arkosammy.signlogger.util.ducks;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import xd.arkosammy.signlogger.events.result.SignEditEventQueryResult;

import java.util.List;

public interface IInspectionModeAccess {

    boolean sign_logger$isInspecting();

    void sign_logger$setIsInspecting(boolean isInspecting);

    void sign_logger$inspect(BlockPos blockPos, ServerWorld world);

    void sign_logger$setPageIndex(int pageIndex);

    List<List<SignEditEventQueryResult>> sign_logger$getCachedSignEditResults();

    void sign_logger$showPage();

}
