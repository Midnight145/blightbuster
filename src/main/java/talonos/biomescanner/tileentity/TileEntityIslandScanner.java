package talonos.biomescanner.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import talonos.biomescanner.BSItems;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.map.Zone;
import talonos.biomescanner.map.event.UpdateCompletionEvent;

public class TileEntityIslandScanner extends TileEntity implements ISidedInventory {

    private ItemStack[] tempInventory;
    private boolean hasScanned = false;

    private int[] noSlots = new int[] {};
    private int[] allSlots;

    public TileEntityIslandScanner() {
        super();
        tempInventory = new ItemStack[getSizeInventory()];
        MapScanner.instance.bus()
            .register(this);

        int size = getSizeInventory();
        allSlots = new int[size];
        for (int i = 0; i < size; i++) {
            allSlots[i] = i;
        }

        updateTotalCompletion(
            MapScanner.instance.getRegionMap()
                .getCompletion());
        for (Zone zone : Zone.values()) {
            updateZoneCompletion(
                zone.ordinal(),
                MapScanner.instance.getRegionMap()
                    .getZoneCompletion(zone));
        }
        updateSlot(Zone.values().length * 3 + 1, hasScanned);
    }

    public void scanOrdered() {
        hasScanned = true;
        markDirty();
        updateSlot(Zone.values().length * 3 + 1, true);
    }

    @Override
    public void onChunkUnload() {
        unregister();
    }

    public void unregister() {
        MapScanner.instance.bus()
            .unregister(this);
    }

    @Override
    public int getSizeInventory() {
        // Bronze, Silver, and Gold medals for each zone, plus a total completion &
        // participation medal
        return Zone.values().length * 3 + 2;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return tempInventory[slot];
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        tempInventory[slot] = stack;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return getStackInSlot(slot);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public int getInventoryStackLimit() {
        return 1;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this
            && player.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @Override
    public String getInventoryName() {
        return "gui.inventory.scanner";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack itemStack, int side) {
        return false;
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack itemStack, int side) {
        if (side == 3) return true;

        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        if (side == 3) return allSlots;

        return noSlots;
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack itemStack) {
        return false;
    }

    @SubscribeEvent
    public void onCompletionUpdate(UpdateCompletionEvent event) {
        updateTotalCompletion(event.getTotalCompletion());

        for (Zone zone : Zone.values()) {
            if (event.hasZone(zone)) {
                updateZoneCompletion(zone.ordinal(), event.getZoneCompletion(zone));
            }
        }
    }

    private void updateTotalCompletion(float completion) {
        boolean complete = completion >= 0.9999f;

        updateSlot(Zone.values().length * 3, complete);
    }

    private void updateZoneCompletion(int zoneOrdinal, float completion) {
        boolean bronzeCompletion = completion >= 0.1999f;
        boolean silverCompletion = completion >= 0.4999f;
        boolean goldCompletion = completion >= 0.9999f;

        updateSlot(zoneOrdinal * 3, bronzeCompletion);
        updateSlot(zoneOrdinal * 3 + 1, silverCompletion);
        updateSlot(zoneOrdinal * 3 + 2, goldCompletion);
    }

    private void updateSlot(int slot, boolean complete) {
        boolean update = complete != (tempInventory[slot] != null);

        if (update) {
            if (complete) tempInventory[slot] = new ItemStack(BSItems.badge, 1, slot);
            else tempInventory[slot] = null;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound par1) {
        super.readFromNBT(par1);
        hasScanned = par1.getBoolean("HasScanned");
        updateSlot((Zone.values().length * 3) + 1, hasScanned);

    }

    @Override
    public void writeToNBT(NBTTagCompound par1) {
        super.writeToNBT(par1);
        par1.setBoolean("HasScanned", hasScanned);
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
