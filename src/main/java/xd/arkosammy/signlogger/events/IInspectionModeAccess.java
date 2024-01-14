package xd.arkosammy.signlogger.events;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public interface IInspectionModeAccess {

    boolean sign_logger$isInspecting();

    void sign_logger$setIsInspecting(boolean isInspecting);

    void sign_logger$inspect(BlockPos blockPos, ServerWorld world);

    void sign_logger$setPageIndex(int pageIndex);

    List<List<SignEditEventResult>> sign_logger$getCachedSignEditResults();

    void sign_logger$showPage();

}
