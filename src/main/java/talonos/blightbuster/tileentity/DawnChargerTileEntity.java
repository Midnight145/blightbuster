package talonos.blightbuster.tileentity;

import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import talonos.blightbuster.DawnChargerProperties;

public class DawnChargerTileEntity extends TileEntity implements IEnergyReceiver, IEnergyStorage, IEnergyHandler {
	private int energyStored = 0;
	private final int MAX_RF = DawnChargerProperties.MAX_RF;
	private final int MAX_OUT = DawnChargerProperties.MAX_OUT;
	private final int MAX_IN = DawnChargerProperties.MAX_IN;
	
	public DawnChargerTileEntity() {
		
	}
		
	@Override
	public boolean canConnectEnergy(ForgeDirection from) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public int extractEnergy(ForgeDirection from, int amount, boolean simulate) {
		// TODO Auto-generated method stub

		int actualExtract = Math.min(this.energyStored, Math.min(this.MAX_OUT, amount));
	    if (!simulate) {
	    	this.energyStored -= actualExtract; 
	    }
	    return actualExtract;
	}
	
	@Override
	public void updateEntity() {
		if (!(this.getWorldObj().getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord) instanceof IEnergyReceiver)) {
			return;
		}
		IEnergyReceiver te = (IEnergyReceiver) this.getWorldObj().getTileEntity(this.xCoord, this.yCoord + 1, this.zCoord);
		int amount = Math.min(MAX_OUT, energyStored);
		energyStored -= te.receiveEnergy(ForgeDirection.DOWN, amount, false);
	}

	@Override
    public int receiveEnergy(ForgeDirection from, int maxReceive, boolean simulate) {

        int room = MAX_RF - energyStored;

        int actualReceive = Math.min(MAX_IN, Math.min(maxReceive, room));

        if (!simulate) {
        	energyStored += actualReceive;
        }
        signalUpdate();
        return actualReceive;
    }

	private void signalUpdate() {
		// TODO Auto-generated method stub
        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
        markDirty();
	}

	@Override
	public int extractEnergy(int amount, boolean simulate) {
		// TODO Auto-generated method stub
		int i = Math.min(this.energyStored, Math.min(this.MAX_OUT, amount));
	    if (!simulate) {
	    	this.energyStored -= i;
	    }
	      
	    return i;
	}

	@Override
	public int getEnergyStored() {
		// TODO Auto-generated method stub
		return energyStored;
	}

	public void setEnergyStored(int amount) {
		energyStored = amount;
	}

	@Override
	public int getMaxEnergyStored() {
		// TODO Auto-generated method stub
		return MAX_RF;
	}

	@Override
	public int receiveEnergy(int amount, boolean simulate) {
		// TODO Auto-generated method stub
        int room = MAX_RF - energyStored;

        int actualReceive = Math.min(amount, Math.min(MAX_IN, room));

        if (!simulate) {
        	energyStored += actualReceive;
        }
            
        signalUpdate();
        return actualReceive;
	}

	@Override
	public int getEnergyStored(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return energyStored;
	}

	@Override
	public int getMaxEnergyStored(ForgeDirection arg0) {
		// TODO Auto-generated method stub
		return MAX_RF;
	}
	
    @Override
    public void writeToNBT(NBTTagCompound tag) {
        tag.setInteger("Energy", energyStored);
        super.writeToNBT(tag);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag) {
    	energyStored = tag.getInteger("Energy");
    	super.readFromNBT(tag);
    }
    
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		this.writeToNBT(nbtTag);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
	}
}
