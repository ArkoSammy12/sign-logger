package xd.arkosammy.signlogger.events;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import xd.arkosammy.signlogger.util.visitors.SignEditEventVisitor;

import java.time.LocalDateTime;

public record WaxedSignEvent(ServerPlayerEntity author,
                             BlockPos blockPos,
                             RegistryKey<World> worldRegistryKey,
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
    public boolean isFrontSide() {
        return this.isFrontSide;
    }

    @Override
    public String getLogString() {
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
    public String toString(){

        return String.format("[%s] %s waxed a sign at %s in %s",
                DTF.format(this.timestamp()),
                this.author().getDisplayName().getString(),
                SignEditEvent.getBlockPosAsLogString(this.blockPos),
                this.getWorldRegistryKeyAsString());
    }

}
