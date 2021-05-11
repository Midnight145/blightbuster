package talonos.blightbuster.tileentity;

import cofh.api.energy.IEnergyHandler;
import cofh.thermalexpansion.block.cell.TileCellCreative;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;

public class DawnChargerTileEntity extends TileEntity implements IEnergyHandler {
	private int[] dawnMachineCoords = {0, 0, 0, 0};
	private boolean dawnMachinePaired = false;
	DawnMachineTileEntity dawnMachine;
	
	public DawnChargerTileEntity() {
		
	}

	@Override
	public void updateEntity() {
		if (dawnMachine == null) {
			return;
		}
		TileEntity te = this.getWorldObj().getTileEntity(this.xCoord, this.yCoord - 1, this.zCoord);
		if (te instanceof IEnergyHandler) {
			IEnergyHandler handler = (IEnergyHandler) te;
			
			if (handler instanceof TileCellCreative) {
				dawnMachine.receiveEnergy(dawnMachine.getMaxEnergyStored(), false);
				return;
			}
			
			int amount = handler.getEnergyStored(ForgeDirection.UP);
			int extract = dawnMachine.receiveEnergy(amount, true);
			int actual = handler.extractEnergy(ForgeDirection.UP, extract, false);	
			if (actual != 0) {
				this.getWorldObj().getBlock(xCoord, yCoord, zCoord).setBlockTextureName("dawnChargerActivated");
			}

			dawnMachine.receiveEnergy(actual, false);
			signalUpdate();
		}	
	}		

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		return from == ForgeDirection.UP;
	}
	
	@Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) { return 0; }

	@Override
	public int extractEnergy(ForgeDirection from, int amount, boolean simulate) { return 0; }
	
	@Override
	public int getEnergyStored(ForgeDirection arg0) { return 0; }

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) { return 0; }
	
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag) {
    	if (dawnMachinePaired) {
    		dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(dawnMachineCoords[0], 
    																			   dawnMachineCoords[1], 
    																			   dawnMachineCoords[2]);
    	}
    	super.readFromNBT(tag);
    }

	private void signalUpdate() {
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        markDirty();
	}
    
    public void setDawnMachinePaired(boolean paired) {
    	dawnMachinePaired = paired;
    }
    
    public void pairDawnMachine(NBTTagCompound dawnMachineTag) {
    	NBTTagCompound tag = new NBTTagCompound();
    	tag.setTag("DawnMachine", dawnMachineTag);
		dawnMachineCoords[0] = tag.getCompoundTag("DawnMachine").getInteger("X");
		dawnMachineCoords[1] = tag.getCompoundTag("DawnMachine").getInteger("Y");
		dawnMachineCoords[2] = tag.getCompoundTag("DawnMachine").getInteger("Z");
		dawnMachineCoords[3] = tag.getCompoundTag("DawnMachine").getInteger("Dimension");
		dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(dawnMachineCoords[0], dawnMachineCoords[1], dawnMachineCoords[2]);
    	this.writeToNBT(tag);
    }
}
