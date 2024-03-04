package xd.arkosammy.signlogger.events.result;

import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.MutableText;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;

public interface SignEditEventQueryResult {

    MutableText getQueryResultText();

    LocalDateTime getTimestamp();

    BlockPos getBlockPos();

    RegistryKey<World> getWorldRegistryKey();

    static RegistryKey<World> fromResourceKeyString(String resourceKey) {
        String[] identifierValue = resourceKey.replaceAll("]", "").split("/")[1].split(":");
        return RegistryKey.of(RegistryKeys.WORLD, new Identifier(identifierValue[0].trim(), identifierValue[1].trim()));
    }

    static BlockPos fromBlockPosLogString(String pos){
        String trimmed = pos.replace("{", "").replace("}", "");
        Integer[] nums = Arrays.stream(trimmed.split(",")).map(Integer::parseInt).toList().toArray(Integer[]::new);
        return new BlockPos(nums[0], nums[1], nums[2]);
    }

     static String formatElapsedTime(Duration duration) {
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.toSeconds() % 60;
        String result = seconds + "s ago";
        if (minutes > 0) {
            result = minutes + "m ago";
        }
        if (hours > 0) {
            result = hours + "h ago";
        }
        if (days > 0) {
            result = days + "d ago";
        }
        return result;
    }

}
