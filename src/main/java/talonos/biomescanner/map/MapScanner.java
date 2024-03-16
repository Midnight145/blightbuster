package talonos.biomescanner.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.event.world.WorldEvent;

import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.event.UpdateMapEvent;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.network.packets.UpdateMapPacket;
import thaumcraft.common.config.ConfigBlocks;

public class MapScanner {

    public static MapScanner instance = new MapScanner();
    public int chunkX = -62, chunkZ = -64;
    public World world;
    public static final int mapWidthChunks = 110;
    public static final int blockWidth = 176;
    public static final int blockHeight = 180;

    private final RegionMap regionMap = new RegionMap();
    private int lastScannedChunk = 0;
    private final byte[][] mapPixels = new byte[7 * blockHeight][5 * blockWidth];
    private final EventBus eventBus = new EventBus();

    public EventBus bus() {
        return this.eventBus;
    }

    public MapScanner() {}

    public void initMap() {
        for (int y = 0; y < 7 * blockHeight; y++) {
            for (int x = 0; x < 5 * blockWidth; x++) {
                final int usByte = 0 | this.mapPixels[y][x];
                if (usByte < 64 || usByte >= 128 && usByte < 192) {
                    this.mapPixels[y][x] = (byte) (usByte + 64);
                }
            }
        }
        this.bus()
            .post(new UpdateMapEvent(0, 0, 5 * blockWidth, 7 * blockHeight));
    }

    public RegionMap getRegionMap() {
        return this.regionMap;
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        this.world = event.world;
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            this.loadData(event.world.getSaveHandler());
            this.bus()
                .post(this.regionMap.getUpdateEvent(Arrays.asList(Zone.values())));
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            this.saveData(
                new File(
                    event.world.getSaveHandler()
                        .getWorldDirectory(),
                    "scanner.dat"));
        }
    }

    private void loadData(ISaveHandler saveHandler) {
        final File worldScannerFile = new File(saveHandler.getWorldDirectory(), "scanner.dat");
        if (this.loadDataFile(worldScannerFile)
            || BiomeScanner.baselineFile != null && this.loadDataFile(BiomeScanner.baselineFile)) {
            return;
        }

        if (!this.loadDataFile(worldScannerFile, true)) {
            this.fillRandomData();
        }
    }

    private boolean loadDataFile(File loadFile) {
        return this.loadDataFile(loadFile, false);
    }

    private boolean loadDataFile(File loadFile, boolean forceLoad) {
        if (!loadFile.exists()) {
            return false;
        }

        try {
            final NBTTagCompound loadedData = CompressedStreamTools.readCompressed(new FileInputStream(loadFile));
            return this.readNBT(loadedData, forceLoad);
        } catch (final Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void saveData(File saveFile) {
        final NBTTagCompound compound = new NBTTagCompound();
        this.writeNBT(compound);
        try {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(saveFile));
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
    }

    private void fillRandomData() {
        this.lastScannedChunk = 0;
        final Random r = new Random();
        for (int y = 0; y < 7 * blockHeight; y++) {
            r.nextBytes(this.mapPixels[y]);
        }
    }

    private boolean readNBT(NBTTagCompound tag, boolean forceLoad) {
        this.lastScannedChunk = tag.getInteger("LastScannedChunk");
        final NBTTagCompound dataTag = tag.getCompoundTag("Data");
        for (int y = 0; y < 7 * blockHeight; y++) {
            this.mapPixels[y] = dataTag.getByteArray(Integer.toString(y));
        }

        if (this.regionMap.read(tag.getCompoundTag("RegionMap"))) {
            if (!forceLoad) {
                return false;
            }
            this.lastScannedChunk = 0;
        }

        return true;
    }

    private void writeNBT(NBTTagCompound tag) {
        tag.setInteger("LastScannedChunk", this.lastScannedChunk);
        final NBTTagCompound dataTag = new NBTTagCompound();
        tag.setTag("Data", dataTag);
        for (int y = 0; y < 7 * blockHeight; y++) {
            dataTag.setByteArray(Integer.toString(y), this.mapPixels[y]);
        }
        final NBTTagCompound regionMapTag = new NBTTagCompound();
        this.regionMap.write(regionMapTag);
        tag.setTag("RegionMap", regionMapTag);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (BiomeScanner.disableEverything) {
            return;
        }
        if (Minecraft.getMinecraft().theWorld != null) {
            BiomeMapColors.updateFlash(Minecraft.getMinecraft().theWorld.getWorldTime());
        }
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        // if (!this.failedInitializedChunkloading && !this.hasInitializedChunkloading
        // && this.world != null) {
        // this.initalizeChunkloading();
        // }
        if (BiomeScanner.disableEverything || event.phase == TickEvent.Phase.END) {
            return;
        }

        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            if (this.lastScannedChunk == 0) {
                this.regionMap.wipeData();
                this.bus()
                    .post(this.regionMap.getUpdateEvent(Arrays.asList(Zone.values())));
                this.initMap();
            }

            if (this.lastScannedChunk != -1 && !event.world.isRemote) {
                // if (!this.isChunkloaded()) {
                // this.loadChunk();
                // }
                this.scanSomeChunks(event.world);
            }
        }
    }

    private void scanSomeChunks(World worldObj) {
        final int chunkX = this.lastScannedChunk % (mapWidthChunks / 5) * 5;
        final int chunkZ = this.lastScannedChunk / (mapWidthChunks / 5) * 1;
        final BiomeGenBase[] biomesForGeneration = worldObj.getWorldChunkManager()
            .loadBlockGeneratorData(null, chunkX * 16, chunkZ * 16, 80, 16);
        final List<Zone> updatedZones = new LinkedList<Zone>();

        for (int xInChunk = 0; xInChunk < 80; xInChunk += 2) {
            final int x = chunkX * 16 + xInChunk;
            for (int zInChunk = 0; zInChunk < 16; zInChunk += 2) {
                final int z = chunkZ * 16 + zInChunk;
                final int biomeIndex = zInChunk * 80 + xInChunk;
                if (biomesForGeneration != null && biomesForGeneration[biomeIndex] != null) {
                    final int biomeID = biomesForGeneration[biomeIndex].biomeID;
                    int color = BiomeMapColors.biomeLookup[biomeID];

                    if (this.getTaintAt(x, z, worldObj)) {
                        color += 128;
                        updatedZones.add(this.regionMap.incrementBlock(x, z, false));
                    } else {
                        updatedZones.add(this.regionMap.incrementBlock(x, z, true));
                    }

                    final int newx = mapWidthChunks * 16 - x - 1;

                    final int xPix = newx / 2;
                    final int yPix = z / 2;
                    this.setColor(xPix, yPix, (byte) color);

                } else {
                    System.out.println("Error!");
                    System.out.println(biomesForGeneration != null);
                    System.out.println(biomesForGeneration[biomeIndex] != null);
                }
            }
        }

        this.bus()
            .post(this.regionMap.getUpdateEvent(updatedZones));

        final int minX = mapWidthChunks * 8 - chunkX * 8 - 40;
        final int minY = chunkZ * 8;
        this.bus()
            .post(new UpdateMapEvent(minX, minY, 40, 8));
        this.lastScannedChunk++;
        if (this.lastScannedChunk >= 22 * 135) {
            this.regionMap.updateData();
            this.lastScannedChunk = -1;
            // this.unloadChunk();
        }
    }

    private boolean getTaintAt(int x, int z, World worldObj) {
        for (int y = 0; y <= 255; y++) {
            final Block northwest = worldObj.getBlock(x, y, z);
            final int northwestMeta = worldObj.getBlockMetadata(x, y, z);

            final Block northeast = worldObj.getBlock(x + 1, y, z);
            final int northeastMeta = worldObj.getBlockMetadata(x + 1, y, z);

            final Block southwest = worldObj.getBlock(x, y, z + 1);
            final int southwestMeta = worldObj.getBlockMetadata(x, y, z + 1);

            final Block southeast = worldObj.getBlock(x + 1, y, z + 1);
            final int southeastMeta = worldObj.getBlockMetadata(x + 1, y, z + 1);

            if (northwest == ConfigBlocks.blockTaint && northwestMeta != 2
                || northeast == ConfigBlocks.blockTaint && northeastMeta != 2
                || southwest == ConfigBlocks.blockTaint && southwestMeta != 2
                || southeast == ConfigBlocks.blockTaint && southeastMeta != 2
                || northwest == ConfigBlocks.blockTaintFibres
                || northeast == ConfigBlocks.blockTaintFibres
                || southwest == ConfigBlocks.blockTaintFibres
                || southeast == ConfigBlocks.blockTaintFibres) {
                return true;
            }
        }
        return false;
    }

    public boolean isActive() {
        return this.lastScannedChunk >= 0;
    }

    public void activate() {
        this.lastScannedChunk = 0;
    }

    public void setColor(int x, int y, byte color) {
        this.mapPixels[y][x] = color;
    }

    public int getColor(int x, int y) {
        final byte b0 = this.mapPixels[y][x];
        final int b = b0 & 0xFF;
        return BiomeMapColors.colors[b];
    }

    public byte getRawColorByte(int x, int y) {
        return this.mapPixels[y][x];
    }

    public void updateFromNetwork(int minX, int minY, int width, int height, byte[] data) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                this.setColor(x + minX, y + minY, data[y * width + x]);
            }
        }

        this.bus()
            .post(new UpdateMapEvent(minX, minY, width, height));
    }

    public void sendEntireMap(EntityPlayerMP entityPlayer) {
        final int width = 5 * blockWidth;
        final int height = 7 * blockHeight;

        final byte[] data = new byte[width * height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[y * width + x] = MapScanner.instance.getRawColorByte(x, y);
            }
        }

        BlightbusterNetwork.sendToPlayer(new UpdateMapPacket(0, 0, width, height, data), entityPlayer);
    }
}
