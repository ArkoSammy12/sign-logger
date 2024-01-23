package xd.arkosammy.signlogger.events;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public interface SignEditEvent {

    DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a");

    ServerPlayerEntity getAuthor();

    BlockPos getBlockPos();

    RegistryKey<World> getWorldRegistryKey();

    LocalDateTime getTimestamp();

    boolean isFrontSide();

    String getEventType();

    String getLogString();

    static String getBlockPosAsLogString(BlockPos pos) {
        return String.format("{%d, %d, %d}", pos.getX(), pos.getY(), pos.getZ());
    }

}
