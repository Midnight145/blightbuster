package talonos.blightbuster.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

import cofh.api.energy.IEnergyHandler;

public class DawnChargerTileEntity extends TileEntity implements IEnergyHandler {

    private final int[] dawnMachineCoords = new int[4];
    private boolean dawnMachinePaired = false;
    DawnMachineTileEntity dawnMachine;

    public DawnChargerTileEntity() {

    }

    @Override
    public void updateEntity() {
        if (this.dawnMachine == null && this.worldObj.provider.dimensionId == this.dawnMachineCoords[3]) {
            this.dawnMachine = (DawnMachineTileEntity) this.getWorldObj()
                .getTileEntity(this.dawnMachineCoords[0], this.dawnMachineCoords[1], this.dawnMachineCoords[2]);
        }
    }

    @Override
    public boolean canConnectEnergy(ForgeDirection from) {
        return true;
    }

    @Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
        return this.dawnMachine == null ? 0 : this.dawnMachine.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(ForgeDirection from, int amount, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored(ForgeDirection arg0) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(ForgeDirection arg0) {
        return 0;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        this.writeCustomNBT(tag);
    }

    public void writeCustomNBT(NBTTagCompound tag) {
        final NBTTagCompound dawnMachineTag = new NBTTagCompound();
        dawnMachineTag.setInteger("X", this.dawnMachineCoords[0]);
        dawnMachineTag.setInteger("Y", this.dawnMachineCoords[1]);
        dawnMachineTag.setInteger("Z", this.dawnMachineCoords[2]);
        dawnMachineTag.setInteger("Dimension", this.dawnMachineCoords[3]);

        tag.setTag("DawnMachine", dawnMachineTag);

    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        this.getDawnMachine(tag);
    }

    private void getDawnMachine(NBTTagCompound tag) {
        this.dawnMachineCoords[0] = tag.getCompoundTag("DawnMachine")
            .getInteger("X");
        this.dawnMachineCoords[1] = tag.getCompoundTag("DawnMachine")
            .getInteger("Y");
        this.dawnMachineCoords[2] = tag.getCompoundTag("DawnMachine")
            .getInteger("Z");
        this.dawnMachineCoords[3] = tag.getCompoundTag("DawnMachine")
            .getInteger("Dimension");

        try {
            this.dawnMachine = (DawnMachineTileEntity) this.getWorldObj()
                .getTileEntity(this.dawnMachineCoords[0], this.dawnMachineCoords[1], this.dawnMachineCoords[2]);
        } catch (final NullPointerException e) {
            return;
        }
    }

    public void setDawnMachinePaired(boolean paired) {
        this.dawnMachinePaired = paired;
    }

    public void pairDawnMachine(int x, int y, int z, int dimension) {
        this.dawnMachineCoords[0] = x;
        this.dawnMachineCoords[1] = y;
        this.dawnMachineCoords[2] = z;
        this.dawnMachineCoords[3] = dimension;
        this.dawnMachine = (DawnMachineTileEntity) this.getWorldObj()
            .getTileEntity(this.dawnMachineCoords[0], this.dawnMachineCoords[1], this.dawnMachineCoords[2]);
        this.writeToNBT(new NBTTagCompound());
        this.markDirty();
    }
}
