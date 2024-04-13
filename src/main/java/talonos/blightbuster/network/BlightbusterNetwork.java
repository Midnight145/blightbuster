package talonos.blightbuster.network;

import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.handlers.BiomeChangeHandler;
import talonos.blightbuster.network.handlers.SpawnCleanseParticleHandler;
import talonos.blightbuster.network.packets.BiomeChangePacket;
import talonos.blightbuster.network.packets.SpawnCleanseParticlesPacket;

@ChannelHandler.Sharable
public class BlightbusterNetwork {

    private static final BlightbusterNetwork INSTANCE = new BlightbusterNetwork();
    private SimpleNetworkWrapper networkWrapper;

    public static void init() {
        INSTANCE.networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(BlightBuster.MODID);
        INSTANCE.networkWrapper
            .registerMessage(SpawnCleanseParticleHandler.class, SpawnCleanseParticlesPacket.class, 0, Side.CLIENT);
        INSTANCE.networkWrapper.registerMessage(BiomeChangeHandler.class, BiomeChangePacket.class, 2, Side.CLIENT);

        FMLCommonHandler.instance()
            .bus()
            .register(INSTANCE);
    }

    public static void sendToAllPlayers(IMessage packet) {
        INSTANCE.networkWrapper.sendToAll(packet);
    }

    public static void sendToNearbyPlayers(IMessage message, int dimension, float x, float y, float z, float radius) {
        INSTANCE.networkWrapper.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, radius));
    }

    // public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
    // INSTANCE.networkWrapper.sendTo(message, player);
    // }

    public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
        if (biome != null) {
            if (!world.isRemote || world.getChunkProvider()
                .chunkExists(x >> 4, z >> 4)) {
                final Chunk chunk = world.getChunkFromBlockCoords(x, z);
                final byte[] array = chunk.getBiomeArray();
                array[(z & 15) << 4 | x & 15] = (byte) (biome.biomeID & 255);
                chunk.setBiomeArray(array);
            }

            if (!world.isRemote) {
                sendToAllPlayers(new BiomeChangePacket(x, z, (short) biome.biomeID));
            }
        }
    }
}
