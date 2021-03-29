package talonos.blightbuster.tileentity;

import cofh.api.energy.IEnergyHandler;
import cofh.thermalexpansion.block.cell.TileCellCreative;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import talonos.blightbuster.DawnChargerProperties;

public class DawnChargerTileEntity extends TileEntity implements IEnergyHandler {
	private int energyStored = 0;
	private final String RF_TAG = "Current RF";
	private final int MAX_RF = 0;
	private final int MAX_OUT = DawnChargerProperties.MAX_OUT;
	private final int MAX_IN = DawnChargerProperties.MAX_IN;
	private int[] dawnMachineCoords = {0, 0, 0, 0};
	private boolean dawnMachinePaired = false;
	DawnMachineTileEntity dawnMachine;
	
	public DawnChargerTileEntity() {
		
	}

	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		// TODO Auto-generated method stub
		return from == ForgeDirection.UP;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int amount, boolean simulate) {
		// TODO Auto-generated method stub

//		int actualExtract = Math.min(this.energyStored, Math.min(this.MAX_OUT, amount));
//	    if (!simulate) {
//	    	this.energyStored -= actualExtract; 
//	    }
//	    return actualExtract;
		return 0;
	}
	
	@Override
	public void updateEntity() {
	//	int amount = Math.min(MAX_OUT, energyStored);
		if (!dawnMachinePaired) {
			if (!(this.getWorldObj().getTileEntity(dawnMachineCoords[0], dawnMachineCoords[1], dawnMachineCoords[2]) == null)) {
				dawnMachinePaired = true;
			}
		}
		if (dawnMachinePaired) {
			if (dawnMachine == null) {
				return;
			}
//			if (dawnMachine == null) {
//				dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(dawnMachineCoords[0], dawnMachineCoords[1], dawnMachineCoords[2]);
//			}
//			else {
//				energyStored -= dawnMachine.receiveEnergy(amount, false);
//			}
			//energyStored -= dawnMachine.receiveEnergy(amount, false);
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

				dawnMachine.receiveEnergy(actual, false);
			}
			
		}		
	}

	@Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

//        int room = MAX_RF - energyStored;
//
//        int actualReceive = Math.min(MAX_IN, Math.min(maxReceive, room));
//
//        if (!simulate) {
//        	energyStored += actualReceive;
//        }
//        signalUpdate();
//        return actualReceive;
		return 0;
	}

	private void signalUpdate() {
		// TODO Auto-generated method stub
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        markDirty();
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return 0;
	}
	
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger(RF_TAG, energyStored);
        NBTTagCompound dawnMachineTag = new NBTTagCompound();
//        if (dawnMachinePaired) {
//	        dawnMachineTag.setInteger("X", dawnMachineCoords[0]);
//	        dawnMachineTag.setInteger("Y", dawnMachineCoords[1]);
//	        dawnMachineTag.setInteger("Z", dawnMachineCoords[2]);
//	        dawnMachineTag.setInteger("Dimension", dawnMachineCoords[3]);
//	        tag.setTag("DawnMachine", dawnMachineTag);
//        }

        super.writeToNBT(tag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag) {
    	System.out.println("readFromNBT");
    	energyStored = tag.getInteger(RF_TAG);
    	if (dawnMachinePaired) {
    		dawnMachine = (DawnMachineTileEntity) this.getWorldObj().getTileEntity(dawnMachineCoords[0], dawnMachineCoords[1], dawnMachineCoords[2]);
    	}
    	super.readFromNBT(tag);
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
