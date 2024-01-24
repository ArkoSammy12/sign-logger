package xd.arkosammy.signlogger.events;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.signlogger.util.visitors.SignEditEventVisitor;

import java.time.LocalDateTime;
import java.util.Objects;

public record ChangedTextSignEvent(ServerPlayerEntity author,
                                   BlockPos blockPos,
                                   RegistryKey<World> worldRegistryKey,
                                   SignEditText originalText,
                                   SignEditText newText,
                                   LocalDateTime timestamp,
                                   boolean isFrontSide) implements SignEditEvent {

    @Override
    public ServerPlayerEntity getAuthor() {
        return this.author;
    }

    @Override
    public BlockPos getBlockPos() {
        return this.blockPos;
    }

    @Override
    public RegistryKey<World> getWorldRegistryKey() {
        return this.worldRegistryKey;
    }

    @Override
    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @Override
    public String getLogString(){
        return this.toString();
    }

    @Override
    public void accept(SignEditEventVisitor signEditEventVisitor) {
        signEditEventVisitor.visit(this);
    }

    private String getWorldRegistryKeyAsString() {

        String worldString = worldRegistryKey.toString();
        int colonCharIndex = worldString.lastIndexOf(':');

        if (colonCharIndex != -1) {
            worldString = worldString.substring(colonCharIndex + 1, worldString.length() - 1);
        }
        return worldString;

    }

    @Override
    public String toString() {
        return String.format("[%s] %s edited the %s-side text of a sign at %s in %s, from %s, to %s",
                DTF.format(this.timestamp()),
                this.author().getDisplayName().getString(),
                this.isFrontSide() ? "front" : "back",
                SignEditEvent.getBlockPosAsLogString(this.blockPos),
                this.getWorldRegistryKeyAsString(),
                this.originalText().toString(),
                this.newText().toString());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChangedTextSignEvent that)) return false;
        return isFrontSide == that.isFrontSide && Objects.equals(author, that.author) && Objects.equals(blockPos, that.blockPos) && Objects.equals(originalText, that.originalText) && Objects.equals(newText, that.newText) && Objects.equals(timestamp, that.timestamp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(author, blockPos, originalText, newText, timestamp, isFrontSide);
    }

}
