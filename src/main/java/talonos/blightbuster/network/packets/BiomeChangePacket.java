package talonos.blightbuster.network.packets;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.utils.Utils;

public class BiomeChangePacket implements IMessage {
    private int x;
    private int z;
    private short biomeID;

    public BiomeChangePacket() {}
    public BiomeChangePacket(int x, int z, short biomeID) {
        this.x = x;
        this.z = z;
        this.biomeID = biomeID;
    }

    @Override
    public void fromBytes(ByteBuf in) {
        x = in.readInt();
        z = in.readInt();
        biomeID = in.readShort();
    }

    @Override
    public void toBytes(ByteBuf out) {
        out.writeInt(x);
        out.writeInt(z);
        out.writeShort(biomeID);
    }

    public int getX() { return x; }
    public int getZ() { return z; }
    public short getBiomeID() { return biomeID; }
}
