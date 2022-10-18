package talonos.blightbuster.tileentity;

import cofh.api.energy.IEnergyHandler;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class DawnChargerTileEntity extends TileEntity implements IEnergyHandler {
	private int[] dawnMachineCoords = { 0, 0, 0, 0 };
	private boolean dawnMachinePaired = false;
	DawnMachineTileEntity dawnMachine;

	public DawnChargerTileEntity() {

	}

	@Override
	public void updateEntity() {
		if (this.dawnMachine == null) { return; }
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return true;
	}

	@Override
	public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {
		if (this.dawnMachine == null) { return 0; }
		return this.dawnMachine.receiveEnergy(from, maxReceive, simulate);
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
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		if (this.dawnMachinePaired) {
			this.dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(this.dawnMachineCoords[0],
					this.dawnMachineCoords[1], this.dawnMachineCoords[2]);
		}
		super.readFromNBT(tag);
	}

	private void signalUpdate() {
		this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
		this.markDirty();
	}

	public void setDawnMachinePaired(boolean paired) { this.dawnMachinePaired = paired; }

	public void pairDawnMachine(NBTTagCompound dawnMachineTag) {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setTag("DawnMachine", dawnMachineTag);
		this.dawnMachineCoords[0] = tag.getCompoundTag("DawnMachine").getInteger("X");
		this.dawnMachineCoords[1] = tag.getCompoundTag("DawnMachine").getInteger("Y");
		this.dawnMachineCoords[2] = tag.getCompoundTag("DawnMachine").getInteger("Z");
		this.dawnMachineCoords[3] = tag.getCompoundTag("DawnMachine").getInteger("Dimension");
		this.dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(this.dawnMachineCoords[0], this.dawnMachineCoords[1],
				this.dawnMachineCoords[2]);
		this.writeToNBT(tag);
	}
}
