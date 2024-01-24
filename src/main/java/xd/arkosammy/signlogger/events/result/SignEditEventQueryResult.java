package xd.arkosammy.signlogger.events.result;

import net.minecraft.text.MutableText;

import java.time.Duration;
import java.time.LocalDateTime;

public interface SignEditEventQueryResult {

    MutableText getQueryResultText();

    LocalDateTime getTimestamp();

    String getBlockPos();

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
