package talonos.biomescanner.map;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.eventhandler.EventBus;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraftforge.event.world.WorldEvent;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.event.UpdateMapEvent;
import talonos.blightbuster.network.BlightbusterNetwork;
import talonos.blightbuster.network.packets.UpdateMapPacket;
import thaumcraft.common.config.ConfigBlocks;

public class MapScanner {
    public static MapScanner instance = new MapScanner();
    public static final int mapWidthChunks = 110;
    public static final int blockWidth = 176;
    public static final int blockHeight = 180;

    private RegionMap regionMap = new RegionMap();
    private int lastScannedChunk = 0;
    private byte[][] mapPixels = new byte[7*blockHeight][5*blockWidth];
    private EventBus eventBus = new EventBus();

    public EventBus bus() { return eventBus; }

    public MapScanner() {
    }

    public void initMap() {
        for (int y = 0; y < (7*blockHeight); y++) {
            for (int x = 0; x < (5*blockWidth); x++) {
                int usByte = 0 | this.mapPixels[y][x];
                if (usByte < 64 || (usByte >= 128 && usByte < 192))
                    this.mapPixels[y][x] = (byte)(usByte + 64);
            }
        }
        bus().post(new UpdateMapEvent(0,0,5*blockWidth,7*blockHeight));
    }

    public RegionMap getRegionMap() { return regionMap; }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            loadData(event.world.getSaveHandler());
            bus().post(regionMap.getUpdateEvent(Arrays.asList(Zone.values())));
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            saveData(new File(event.world.getSaveHandler().getWorldDirectory(), "scanner.dat"));
        }
    }

    private void loadData(ISaveHandler saveHandler) {
        File worldScannerFile = new File(saveHandler.getWorldDirectory(), "scanner.dat");
        if (loadDataFile(worldScannerFile))
            return;

        if (BiomeScanner.baselineFile != null && loadDataFile(BiomeScanner.baselineFile))
            return;

        if (!loadDataFile(worldScannerFile, true))
            fillRandomData();
    }

    private boolean loadDataFile(File loadFile) {
        return loadDataFile(loadFile, false);
    }

    private boolean loadDataFile(File loadFile, boolean forceLoad) {
        if (!loadFile.exists())
            return false;

        try {
            NBTTagCompound loadedData = CompressedStreamTools.readCompressed(new FileInputStream(loadFile));
            return readNBT(loadedData, forceLoad);
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    private void saveData(File saveFile) {
        NBTTagCompound compound = new NBTTagCompound();
        writeNBT(compound);
        try {
            CompressedStreamTools.writeCompressed(compound, new FileOutputStream(saveFile));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void fillRandomData() {
        lastScannedChunk = 0;
        Random r = new Random();
        for (int y = 0; y < 7*blockHeight; y++) {
            r.nextBytes(mapPixels[y]);
        }
    }

    private boolean readNBT(NBTTagCompound tag, boolean forceLoad) {
        lastScannedChunk = tag.getInteger("LastScannedChunk");
        NBTTagCompound dataTag = tag.getCompoundTag("Data");
        for (int y = 0; y < 7*blockHeight; y++) {
            mapPixels[y] = dataTag.getByteArray(Integer.toString(y));
        }

        if (regionMap.read(tag.getCompoundTag("RegionMap"))) {
            if (forceLoad)
                lastScannedChunk = 0;
            else
                return false;
        }

        return true;
    }

    private void writeNBT(NBTTagCompound tag) {
        tag.setInteger("LastScannedChunk", lastScannedChunk);
        NBTTagCompound dataTag = new NBTTagCompound();
        tag.setTag("Data", dataTag);
        for (int y = 0; y < 7*blockHeight; y++) {
            dataTag.setByteArray(Integer.toString(y), mapPixels[y]);
        }
        NBTTagCompound regionMapTag = new NBTTagCompound();
        regionMap.write(regionMapTag);
        tag.setTag("RegionMap", regionMapTag);
    }

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onRenderTick(TickEvent.RenderTickEvent event) {
        if (BiomeScanner.disableEverything)
            return;
        if (Minecraft.getMinecraft().theWorld != null)
            BiomeMapColors.updateFlash(Minecraft.getMinecraft().theWorld.getWorldTime());
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (BiomeScanner.disableEverything)
            return;

        if (event.phase == TickEvent.Phase.END)
            return;

        if (event.world.provider.dimensionId == 0 && !event.world.isRemote) {
            if (lastScannedChunk == 0) {
                regionMap.wipeData();
                bus().post(regionMap.getUpdateEvent(Arrays.asList(Zone.values())));
                initMap();
            }

            if (lastScannedChunk != -1 && !event.world.isRemote) //If it's "on",
                scanSomeChunks(event.world);
        }
    }

    private void scanSomeChunks(World worldObj) {
        int chunkX = (lastScannedChunk % (mapWidthChunks/5))*5;
        int chunkZ = (lastScannedChunk / (mapWidthChunks/5))*1;
        BiomeGenBase[] biomesForGeneration = worldObj.getWorldChunkManager()
                .loadBlockGeneratorData(null, chunkX*16, chunkZ*16,
                        80, 16);
        List<Zone> updatedZones = new LinkedList<Zone>();

        for (int xInChunk = 0; xInChunk < 80; xInChunk += 2)
        {
            int x = chunkX * 16 + xInChunk;
            for (int zInChunk = 0; zInChunk < 16; zInChunk += 2)
            {
                int z = chunkZ * 16 + zInChunk;
                int biomeIndex = (zInChunk * 80)+xInChunk;
                if ((biomesForGeneration != null)
                        && (biomesForGeneration[biomeIndex] != null))
                {
                    int biomeID = biomesForGeneration[biomeIndex].biomeID;
                    int color = BiomeMapColors.biomeLookup[biomeID];

                    if (getTaintAt(x, z, worldObj))
                    {
                        color += 128;
                        updatedZones.add(regionMap.incrementBlock(x,z,false));
                    } else {
                        updatedZones.add(regionMap.incrementBlock(x,z,true));
                    }

                    int newx = (mapWidthChunks * 16) - x - 1;

                    int xPix = (newx / 2);
                    int yPix = (z / 2);
                    setColor(xPix, yPix, (byte)color);

                }
                else
                {
                    System.out.println("Error!");
                    System.out.println(biomesForGeneration != null);
                    System.out.println(biomesForGeneration[biomeIndex] != null);
                }
            }
        }

        bus().post(regionMap.getUpdateEvent(updatedZones));

        int minX = ((mapWidthChunks * 8) - (chunkX * 8) - 40);
        int minY = chunkZ * 8;
        bus().post(new UpdateMapEvent(minX, minY, 40, 8));
        lastScannedChunk++;
        if (lastScannedChunk >= (22 * 135))
        {
            regionMap.updateData();
            lastScannedChunk = -1;
        }
    }

    private boolean getTaintAt(int x, int z, World worldObj)
    {
        for (int y = 0; y <= 255; y++)
        {
            Block northwest = worldObj.getBlock(x,y,z);
            Block northeast = worldObj.getBlock(x+1, y, z);
            Block southwest = worldObj.getBlock(x, y, z+1);
            Block southeast = worldObj.getBlock(x+1, y, z+1);
            if (northwest == ConfigBlocks.blockTaint
                    || northeast == ConfigBlocks.blockTaint
                    || southwest == ConfigBlocks.blockTaint
                    || southeast == ConfigBlocks.blockTaint
                    || northwest == ConfigBlocks.blockTaintFibres
                    || northeast == ConfigBlocks.blockTaintFibres
                    || southwest == ConfigBlocks.blockTaintFibres
                    || southeast == ConfigBlocks.blockTaintFibres)
            {
                return true;
            }
        }
        return false;
    }

    public boolean isActive() { return lastScannedChunk >= 0; }
    public void activate() { lastScannedChunk = 0; }

    public void setColor(int x, int y, byte color) {
        this.mapPixels[y][x] = color;
    }

    public int getColor(int x, int y) {
        byte b0 = this.mapPixels[y][x];
        int b = b0 & 0xFF;
        return BiomeMapColors.colors[b];
    }

    public byte getRawColorByte(int x, int y) {
        return this.mapPixels[y][x];
    }

    public void updateFromNetwork(int minX, int minY, int width, int height, byte[] data) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                setColor(x+minX, y+minY, data[(y*width)+x]);
            }
        }

        bus().post(new UpdateMapEvent(minX, minY, width, height));
    }

    public void sendEntireMap(EntityPlayerMP entityPlayer) {
        int width = 5*blockWidth;
        int height = 7*blockHeight;

        byte[] data = new byte[width*height];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                data[(y*width)+x] = MapScanner.instance.getRawColorByte(x, y);
            }
        }

        BlightbusterNetwork.sendToPlayer(new UpdateMapPacket(0, 0, width, height, data), entityPlayer);
    }
}
