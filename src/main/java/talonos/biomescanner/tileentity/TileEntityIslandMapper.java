package talonos.biomescanner.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.map.event.UpdateMapEvent;

public class TileEntityIslandMapper extends TileEntity {

    private int mapX;
    private int mapY;
    private boolean isRenderDirty = true;

    public boolean isRenderDirty() {
        return isRenderDirty;
    }

    public void setRenderDirty(boolean renderDirty) {
        isRenderDirty = renderDirty;
    }

    public int getMapX() {
        return mapX;
    }

    public int getMapY() {
        return mapY;
    }

    public void setMapCoords(int x, int y) {
        this.mapX = x;
        this.mapY = y;
    }

    public TileEntityIslandMapper() {
        super();
        MapScanner.instance.bus()
            .register(this);
    }

    @Override
    public void onChunkUnload() {
        super.onChunkUnload();
        MapScanner.instance.bus()
            .unregister(this);
    }

    @SubscribeEvent
    public void onMapUpdated(UpdateMapEvent event) {
        // If the updated area of the map intersects with our area of the map,
        // it's time to update the dynamic texture
        boolean shouldUpdate = (Math.abs(mapX - event.getX()) * 2 < MapScanner.blockWidth + event.getWidth())
            && (Math.abs(mapY - event.getY()) * 2 < MapScanner.blockHeight + event.getHeight());

        if (shouldUpdate) setRenderDirty(true);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        mapX = par1.getInteger("mapX");
        mapY = par1.getInteger("mapY");
        setRenderDirty(true);

    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setInteger("mapX", mapX);
        par1.setInteger("mapY", mapY);
    }

    @Override
    public void updateEntity() {}

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound tag = new NBTTagCompound();
        writeToNBT(tag);
        return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
    }

    /**
     * Called when you receive a TileEntityData packet for the location this
     * TileEntity is currently in. On the client, the NetworkManager will always
     * be the remote server. On the server, it will be whomever is responsible for
     * sending the packet.
     *
     * @param net The NetworkManager the packet originated from
     * @param pkt The data packet
     */
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        readFromNBT(pkt.func_148857_g());
    }
}
