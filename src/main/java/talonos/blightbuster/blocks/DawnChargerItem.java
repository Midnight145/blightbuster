package talonos.blightbuster.blocks;

import java.util.List;
import talonos.blightbuster.DawnChargerProperties;
import cofh.api.energy.IEnergyContainerItem;
import cofh.lib.util.helpers.EnergyHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class DawnChargerItem extends ItemBlock implements IEnergyContainerItem {         
	private int energyStored = 50000000;                                                            
	private final int MAX_RF = DawnChargerProperties.MAX_RF;                             
	private final int MAX_OUT = DawnChargerProperties.MAX_OUT;                           
	private final int MAX_IN = DawnChargerProperties.MAX_IN;                             
	                                                                                     
	public DawnChargerItem(Block block) {                                                
		super(block);                                                                    
		this.setMaxDamage(DawnChargerProperties.MAX_RF);                                 
		this.setMaxStackSize(64);
		this.setNoRepair();
		this.setTextureName("blightbuster:dawnCharger");
	}

	public static ItemStack setDefaultTag(ItemStack paramItemStack, int paramInt) {
		EnergyHelper.setDefaultEnergyTag(paramItemStack, paramInt);
		return paramItemStack;
	}

	@Override
	public String getUnlocalizedName(ItemStack stack) {
		return "blightbuster.dawnCharger.tile";
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
		if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
			System.out.println("in placeBlockAt");
			if (stack.stackTagCompound == null) {
				stack.setTagCompound(new NBTTagCompound());
				stack.stackTagCompound.setInteger("Energy",  energyStored);

				System.out.println("stack.stackTagCompound == null");
			}
			NBTTagCompound temp = new NBTTagCompound();
			temp.setInteger("Energy", energyStored);
			System.out.println("temp.setInteger... energy = " + temp.getInteger("Energy"));
			world.getTileEntity(x,  y,  z).writeToNBT(temp);
			System.out.println(world.getTileEntity(x,  y,  z).blockType.getUnlocalizedName());
			return true;
		}
		return false;
		
	}
	
	@Override
    public int receiveEnergy(ItemStack stack, int maxReceive, boolean simulate) {
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.stackTagCompound.setInteger("Energy",  50000000);
		}
        int room = MAX_RF - energyStored;

        int actualReceive = Math.min(MAX_IN, Math.min(maxReceive, room));

        if (!simulate) {
        	energyStored += actualReceive;
			stack.stackTagCompound.setInteger("Energy", energyStored);
        }
        return actualReceive;
    }
	
	@Override
	public int extractEnergy(ItemStack stack, int amount, boolean simulate) {
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.stackTagCompound.setInteger("Energy",  50000000);
			return 0;
		}
		// TODO Auto-generated method stub
		int actualExtract = Math.min(this.energyStored, Math.min(this.MAX_OUT, amount));
	    if (!simulate) {
	    	this.energyStored -= actualExtract;
		    stack.stackTagCompound.setInteger("Energy", energyStored);

	    }

	    return actualExtract;
	}

	@Override
	public int getEnergyStored(ItemStack arg0) {
		// TODO Auto-generated method stub
		//return arg0.stackTagCompound.getInteger("Energy");
		return energyStored;
	}

	@Override
	public int getMaxEnergyStored(ItemStack arg0) {
		// TODO Auto-generated method stub
		return DawnChargerProperties.MAX_RF;
	}

	@Override
	public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean check) {
	if (stack.stackTagCompound == null) {
		stack.setTagCompound(new NBTTagCompound());
		stack.stackTagCompound.setInteger("Energy",  50000000);
	}
		energyStored = stack.stackTagCompound.getInteger("Energy");
		list.add("RF: " + energyStored + "/" + DawnChargerProperties.MAX_RF);
	}
	
	@Override
	public boolean showDurabilityBar(ItemStack stack) {
		return true;
	}
	
	@Override
	public boolean isDamaged(ItemStack stack) {
		return true;
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		return DawnChargerProperties.MAX_RF;
	}
	
	@Override
	public int getDisplayDamage(ItemStack stack) {
		if (stack.stackTagCompound == null) {
			stack.setTagCompound(new NBTTagCompound());
			stack.stackTagCompound.setInteger("Energy",  50000000);
		}
		return DawnChargerProperties.MAX_RF - stack.stackTagCompound.getInteger("Energy");
	}
}
