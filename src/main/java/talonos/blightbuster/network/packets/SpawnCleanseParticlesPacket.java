package talonos.blightbuster.network.packets;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;

public class SpawnCleanseParticlesPacket implements IMessage {

    private int spawnX;
    private int spawnZ;
    private boolean useFlameParticles;
    private boolean largeArea;

    public SpawnCleanseParticlesPacket() {}

    public SpawnCleanseParticlesPacket(int x, int y) {
        this(x, y, false, false);
    }

    public SpawnCleanseParticlesPacket(int x, int z, boolean useFlameParticles, boolean largeArea) {
        this.spawnX = x;
        this.spawnZ = z;
        this.useFlameParticles = useFlameParticles;
        this.largeArea = largeArea;
    }

    @Override
    public void fromBytes(ByteBuf in) {
        this.spawnX = in.readInt();
        this.spawnZ = in.readInt();
        this.useFlameParticles = in.readBoolean();
        this.largeArea = in.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf out) {
        out.writeInt(this.spawnX);
        out.writeInt(this.spawnZ);
        out.writeBoolean(this.useFlameParticles);
        out.writeBoolean(this.largeArea);
    }

    public int getSpawnX() {
        return this.spawnX;
    }

    public int getSpawnZ() {
        return this.spawnZ;
    }

    public boolean doUseFlameParticles() {
        return this.useFlameParticles;
    }

    public boolean isLargeArea() {
        return this.largeArea;
    }
}
