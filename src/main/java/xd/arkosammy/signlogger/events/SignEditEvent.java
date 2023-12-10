package xd.arkosammy.signlogger.events;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public record SignEditEvent(PlayerEntity author, BlockPos blockPos, RegistryKey<World> worldRegistryKey, SignEditText originalText, SignEditText newText, LocalDateTime timestamp, boolean isFrontSide) {

    public static final DateTimeFormatter DTF = DateTimeFormatter.ofPattern("dd/MM/yyyy, hh:mm:ss a");

    public static String getWorldRegistryKeyAsAltString(RegistryKey<World> worldRegistryKey) {

        if (worldRegistryKey == null) {
            return "NO WORLD";
        }
        String worldString = worldRegistryKey.toString();
        int colonCharIndex = worldString.lastIndexOf(':');

        if (colonCharIndex != -1) {
            worldString = worldString.substring(colonCharIndex + 1, worldString.length() - 1);
        }
        return worldString;

    }

    public static String getWorldRegistryKeyAsAltString(String worldRegistryKey) {

        int colonCharIndex = worldRegistryKey.lastIndexOf(':');
        if (colonCharIndex != -1) {
            worldRegistryKey = worldRegistryKey.substring(colonCharIndex + 1, worldRegistryKey.length() - 1);
        }
        return worldRegistryKey;

    }

    public static String getBlockPosAsAltString(BlockPos pos) {
        return String.format("{%d, %d, %d}", pos.getX(), pos.getY(), pos.getZ());
    }

    @Override
    public String toString() {
        return String.format("[%s] %s edited the %s-side text of a sign at %s in %s, from %s, to %s",
                DTF.format(this.timestamp()),
                this.author().getDisplayName().getString(),
                this.isFrontSide() ? "front" : "back",
                getBlockPosAsAltString(this.blockPos),
                getWorldRegistryKeyAsAltString(this.worldRegistryKey),
                this.originalText().toString(),
                this.newText().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SignEditEvent that)) return false;
        return isFrontSide == that.isFrontSide && Objects.equals(author, that.author) && Objects.equals(blockPos, that.blockPos) && Objects.equals(originalText, that.originalText) && Objects.equals(newText, that.newText) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, blockPos, originalText, newText, timestamp, isFrontSide);
    }

}
