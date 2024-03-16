package talonos.biomescanner.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import talonos.biomescanner.block.BSBlock;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.map.Zone;
import talonos.biomescanner.map.event.UpdateCompletionEvent;

public class TileEntityGauge extends TileEntity {

    private Zone targetZone = Zone.SwampBay;
    private float completion = 0;

    public TileEntityGauge() {
        super();
        register();
        completion = MapScanner.instance.getRegionMap()
            .getZoneCompletion(targetZone);
    }

    public Zone getTargetZone() {
        return targetZone;
    }

    public float getCompletion() {
        return completion;
    }

    @Override
    public void onChunkUnload() {
        unregister();
    }

    protected void register() {
        MapScanner.instance.bus()
            .register(this);
    }

    public void unregister() {
        MapScanner.instance.bus()
            .unregister(this);
    }

    public void cycleZone() {
        if (!worldObj.isRemote) {
            int ordinal = targetZone.ordinal();
            int newOrdinal = ordinal + 1;
            newOrdinal = (newOrdinal >= Zone.values().length) ? 0 : newOrdinal;
            targetZone = Zone.values()[newOrdinal];

            updateZone(
                MapScanner.instance.getRegionMap()
                    .getZoneCompletion(targetZone));
            worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
            markDirty();
        }
    }

    @SubscribeEvent
    public void onCompletionUpdate(UpdateCompletionEvent event) {
        if (!event.hasZone(targetZone)) return;

        updateZone(event.getZoneCompletion(targetZone));
    }

    protected void updateZone(float completion) {
        this.completion = completion;

        updateGauge();
    }

    private void updateGauge() {
        if (this.worldObj == null) return;
        if (this.worldObj.getBlock(xCoord, yCoord - 3, zCoord) != BSBlock.gaugeBot) return;
        if (this.worldObj.getBlock(xCoord, yCoord - 2, zCoord) != BSBlock.gaugeMid) return;
        if (this.worldObj.getBlock(xCoord, yCoord - 1, zCoord) != BSBlock.gaugeTop) return;

        int totalStages = (14 + 16 + 14) - 1;

        int currentStage = totalStages;
        if (completion != 1.0f) currentStage = (int) Math.floor(totalStages * completion);
        if (completion != 0 && currentStage == 0) currentStage = 1;

        int targetBottom = (currentStage < 14) ? currentStage : 13;
        currentStage -= 14;
        int targetMid = Math.max((currentStage < 16) ? currentStage : 15, 0);
        currentStage -= 16;
        int targetTop = Math.max(currentStage, 0);

        if (this.worldObj.getBlockMetadata(xCoord, yCoord - 3, zCoord) != targetBottom)
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord - 3, zCoord, targetBottom, 3);
        if (this.worldObj.getBlockMetadata(xCoord, yCoord - 2, zCoord) != targetMid)
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord - 2, zCoord, targetMid, 3);
        if (this.worldObj.getBlockMetadata(xCoord, yCoord - 1, zCoord) != targetTop)
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord - 1, zCoord, targetTop, 3);
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        int ordinal = par1.getInteger("Zone");
        targetZone = Zone.values()[ordinal];
        completion = MapScanner.instance.getRegionMap()
            .getZoneCompletion(targetZone);
    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setInteger("Zone", targetZone.ordinal());
    }

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
