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
        spawnX = in.readInt();
        spawnZ = in.readInt();
        useFlameParticles = in.readBoolean();
        largeArea = in.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf out) {
        out.writeInt(spawnX);
        out.writeInt(spawnZ);
        out.writeBoolean(useFlameParticles);
        out.writeBoolean(largeArea);
    }

    public int getSpawnX() { return spawnX; }
    public int getSpawnZ() { return spawnZ; }
    public boolean doUseFlameParticles() { return useFlameParticles; }
    public boolean isLargeArea() { return largeArea; }
}
