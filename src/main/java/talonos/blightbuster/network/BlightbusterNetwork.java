package talonos.blightbuster.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.ChannelHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.map.event.UpdateMapEvent;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.handlers.BiomeChangeHandler;
import talonos.blightbuster.network.handlers.SpawnCleanseParticleHandler;
import talonos.blightbuster.network.handlers.UpdateClientMapHandler;
import talonos.blightbuster.network.packets.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@ChannelHandler.Sharable
public class BlightbusterNetwork {
    private static final BlightbusterNetwork INSTANCE = new BlightbusterNetwork();
    private SimpleNetworkWrapper networkWrapper;

    public static void init() {
        INSTANCE.networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel(BlightBuster.MODID);
        INSTANCE.networkWrapper.registerMessage(SpawnCleanseParticleHandler.class, SpawnCleanseParticlesPacket.class, 0, Side.CLIENT);
        INSTANCE.networkWrapper.registerMessage(UpdateClientMapHandler.class, UpdateMapPacket.class, 1, Side.CLIENT);
        INSTANCE.networkWrapper.registerMessage(BiomeChangeHandler.class, BiomeChangePacket.class, 2, Side.CLIENT);

        if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
            MapScanner.instance.bus().register(INSTANCE);
        }

        FMLCommonHandler.instance().bus().register(INSTANCE);
    }

    @SubscribeEvent
    public void onUpdateMap(UpdateMapEvent event) {
        int minX = event.getX();
        int minY = event.getY();
        int width = event.getWidth();
        int height = event.getHeight();

        byte[] data = new byte[width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[(y*width)+x] = MapScanner.instance.getRawColorByte(x+minX, y+minY);
            }
        }
        sendToAllPlayers(new UpdateMapPacket(minX, minY, width, height, data));
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof  EntityPlayerMP)
            MapScanner.instance.sendEntireMap((EntityPlayerMP) event.player);
    }

    public static void sendToAllPlayers(IMessage packet) {
        INSTANCE.networkWrapper.sendToAll(packet);
    }

    public static void sendToNearbyPlayers(IMessage message, int dimension, float x, float y, float z, float radius) {
        INSTANCE.networkWrapper.sendToAllAround(message, new NetworkRegistry.TargetPoint(dimension, x, y, z, radius));
    }

    public static void sendToPlayer(IMessage message, EntityPlayerMP player) {
        INSTANCE.networkWrapper.sendTo(message, player);
    }

    public static void setBiomeAt(World world, int x, int z, BiomeGenBase biome) {
        if(biome != null) {
            if (!world.isRemote || world.getChunkProvider().chunkExists(x >> 4, z >> 4)) {
                Chunk chunk = world.getChunkFromBlockCoords(x, z);
                byte[] array = chunk.getBiomeArray();
                array[(z & 15) << 4 | x & 15] = (byte) (biome.biomeID & 255);
                chunk.setBiomeArray(array);
            }

            if(!world.isRemote) {
                sendToAllPlayers(new BiomeChangePacket(x, z, (short) biome.biomeID));
            }
        }
    }
}
