package talonos.blightbuster.handlers;

import org.apache.commons.lang3.tuple.Pair;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;
import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.multiblock.BlockMultiblock;
import talonos.blightbuster.multiblock.entries.MultiblockEntry;
import talonos.blightbuster.tileentity.DawnChargerTileEntity;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;

public class TalonosWandTriggerManager implements IWandTriggerManager {

    private BlockTransportManager blockTransportManager = new BlockTransportManager();

    @Override
    public boolean performTrigger(World world, ItemStack wand,
                                  EntityPlayer player, int x, int y, int z, int side, int event) {

        switch (event) {
            case 0:
                if (ResearchManager.isResearchComplete(
                        player.getCommandSenderName(), "DAWNMACHINE"))
                {
                    boolean success = createDawnMachine(wand, player, world, x, y, z);
                    if (success) {
                        Block convertedBlock = world.getBlock(x, y, z);
                        if (convertedBlock instanceof BlockMultiblock) {
                            TileEntity controller = ((BlockMultiblock)convertedBlock).getMultiblockController(world, x, y, z);
                            if (controller != null) {
                                pairDawnMachineToWand(world, wand, controller.getWorldObj().provider.dimensionId, controller.xCoord, controller.yCoord, controller.zCoord);
                                return true;
                            }
                        }

                        player.addChatMessage(new ChatComponentTranslation("gui.offering.pairFailed"));
                    }
                    return success;
                }
                break;
            case 1:
                Block convertedBlock = world.getBlock(x, y, z);

                if (convertedBlock instanceof BlockMultiblock) {
                    TileEntity controller = ((BlockMultiblock)convertedBlock).getMultiblockController(world, x, y, z);
                    if (controller != null) {
                        if (world.isRemote)
                            return false;

                        if (!isWandPaired(wand, controller.getWorldObj().provider.dimensionId, controller.xCoord, controller.yCoord, controller.zCoord)) {
                            pairDawnMachineToWand(world, wand, controller.getWorldObj().provider.dimensionId, controller.xCoord, controller.yCoord, controller.zCoord);
                            player.addChatMessage(new ChatComponentTranslation("gui.offering.pairSucceeded"));
                        } else
                            player.addChatMessage(new ChatComponentTranslation("gui.offering.pairAlreadyExists"));
                        return true;
                    }
                }

                break;
            case 2:
                if (world.isRemote)
                    return false;

                NBTTagCompound wandTag = wand.getTagCompound();
                if (wandTag == null) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.noPairing"));
                    return false;
                }

                if (!wandTag.hasKey("DawnMachine", 10)) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.noPairing"));
                    return false;
                }

                NBTTagCompound pairingTag = wandTag.getCompoundTag("DawnMachine");
                if (pairingTag.getInteger("Dimension") != world.provider.dimensionId) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.wrongDimension"));
                    return false;
                }

                int dawnMachineX = pairingTag.getInteger("X");
                int dawnMachineY = pairingTag.getInteger("Y");
                int dawnMachineZ = pairingTag.getInteger("Z");

                if (world.getBlock(dawnMachineX, dawnMachineY, dawnMachineZ) != BBBlock.dawnMachine) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.pairingDestroyed"));
                    return false;
                }

                for (int clearY = 0; clearY < 5; clearY++) {
                    for (int clearX = -2; clearX <= 2; clearX++) {
                        for (int clearZ = -2; clearZ <= 2; clearZ++) {
                            if (clearY == 0 && clearX == 0 && clearZ == 0)
                                continue;

                            if (!world.isAirBlock(x+clearX, y+clearY, z+clearZ)) {
                                Block clearBlock = world.getBlock(x + clearX, y + clearY, z + clearZ);
                                if (!clearBlock.getMaterial().isReplaceable()) {
                                    player.addChatMessage(new ChatComponentTranslation("gui.offering.clearArea"));
                                    return false;
                                }
                            }
                        }
                    }
                }

                boolean result = blockTransportManager.transport(world, dawnMachineX, dawnMachineY+1, dawnMachineZ, x, y+2, z);

                if (result) {
                    TileEntity controller = world.getTileEntity(x, y+1, z);
                    NBTTagCompound chargerTag = wand.stackTagCompound.getCompoundTag("DawnCharger");
                    pairDawnMachineToWand(world, wand, controller.getWorldObj().provider.dimensionId, controller.xCoord, controller.yCoord, controller.zCoord);
                    moveDawnCharger(world, wand, controller.getWorldObj());
                }
                
                break;
            case 3:
                if (world.isRemote)
                    return false;
                
                if (!ResearchManager.isResearchComplete(player.getCommandSenderName(), "DAWNCHARGER")) {
                	return false;
                }
                TileEntity controller = world.getTileEntity(x, y, z);

                pairDawnChargerToDawnMachine(controller.getWorldObj(), wand, controller.getWorldObj().provider.dimensionId, x, y, z);
            	player.addChatMessage(new ChatComponentTranslation("gui.charger.pairSuccessful"));

        }
        return false;
    }

    private void moveDawnCharger(World world, ItemStack wand, World worldObj) {
		NBTTagCompound wandTag = wand.getTagCompound();
		if (wandTag.getTag("DawnCharger") == null) {
			return;
		}
		NBTTagCompound chargerTag = wandTag.getCompoundTag("DawnCharger");
		DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world.getTileEntity(chargerTag.getInteger("X"), chargerTag.getInteger("Y"), chargerTag.getInteger("Z"));
		if (dawnCharger == null) { return; }
		
		System.out.println(wandTag.getCompoundTag("DawnMachine"));
		dawnCharger.pairDawnMachine(wandTag.getCompoundTag("DawnMachine"));
		dawnCharger.setDawnMachinePaired(true);
		
	}

	private boolean isWandPaired(ItemStack wand, int dimension, int x, int y, int z) {
        if (wand.getTagCompound() == null)
            return false;

        NBTTagCompound wandTag = wand.getTagCompound();

        if (!wandTag.hasKey("DawnMachine", 10)) {
            return false;
        }

        NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
        if (dawnMachineTag.getInteger("Dimension") != dimension)
            return false;
        if (dawnMachineTag.getInteger("X") != x)
            return false;
        if (dawnMachineTag.getInteger("Y") != y)
            return false;
        if (dawnMachineTag.getInteger("Z") != z)
            return false;
        return true;
    }
    
    private boolean isWandPairedToCharger(ItemStack wand, int dimension, int x, int y, int z) {
        if (wand.getTagCompound() == null)
            return false;

        NBTTagCompound wandTag = wand.getTagCompound();

        if (!wandTag.hasKey("DawnCharger")) {
            return false;
        }

        NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnCharger");
        if (dawnMachineTag.getInteger("Dimension") != dimension)
            return false;
        if (dawnMachineTag.getInteger("X") != x)
            return false;
        if (dawnMachineTag.getInteger("Y") != y)
            return false;
        if (dawnMachineTag.getInteger("Z") != z)
            return false;
        return true;
    }

    private void pairDawnMachineToWand(World world, ItemStack wand, int dimension, int x, int y, int z) {
        NBTTagCompound wandTag = wand.getTagCompound();
        if (wandTag == null) {
            wandTag = new NBTTagCompound();
            wand.setTagCompound(wandTag);
        }

        NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
        dawnMachineTag.setInteger("Dimension", dimension);
        dawnMachineTag.setInteger("X", x);
        dawnMachineTag.setInteger("Y", y);
        dawnMachineTag.setInteger("Z", z);
        wandTag.setTag("DawnMachine", dawnMachineTag);
        
        NBTTagCompound wandChargerTag = wandTag.getCompoundTag("DawnCharger");
    	DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world.getTileEntity(wandChargerTag.getInteger("X"), wandChargerTag.getInteger("Y"), wandChargerTag.getInteger("Z"));

    	if (dawnCharger == null) { return; }
    	
    	NBTTagCompound dawnChargerTag = createChargerTag(dawnMachineTag);
    	
    	dawnCharger.pairDawnMachine(dawnChargerTag);
    	
    }

    private void pairDawnChargerToDawnMachine(World world, ItemStack wand, int dimension, int x, int y, int z) {
    	NBTTagCompound wandTag = wand.getTagCompound();
    	NBTTagCompound wandChargerTag;
    	if (!wandTag.hasKey("DawnMachine")) {
    		return;
    	}
    	wandChargerTag = new NBTTagCompound();
    	wandChargerTag.setInteger("X", x);
    	wandChargerTag.setInteger("Y", y);
    	wandChargerTag.setInteger("Z", z);
    	wandTag.setTag("DawnCharger", wandChargerTag);
    	
    	NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
    	
    	NBTTagCompound dawnChargerTag = createChargerTag(dawnMachineTag);
    	DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world.getTileEntity(x, y, z);
    	
    	dawnCharger.pairDawnMachine(dawnChargerTag);
    	dawnCharger.setDawnMachinePaired(true);
    	return;
    }
    
    private NBTTagCompound createChargerTag(NBTTagCompound dawnMachineTag) {
    	NBTTagCompound dawnChargerTag = new NBTTagCompound();
    	
    	dawnChargerTag.setInteger("Dimension",  dawnMachineTag.getInteger("Dimension"));
    	dawnChargerTag.setInteger("X", dawnMachineTag.getInteger("X"));
    	dawnChargerTag.setInteger("Y", dawnMachineTag.getInteger("Y"));
    	dawnChargerTag.setInteger("Z", dawnMachineTag.getInteger("Z"));
    	return dawnChargerTag;
    }

    private boolean createDawnMachine(ItemStack stack, EntityPlayer player,
                                      World world, int x, int y, int z) {
        if (world.isRemote)
            return false;

        ItemWandCasting wand = (ItemWandCasting) stack.getItem();

        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        Pair<MultiblockEntry, Integer> entry = BBBlock.dawnMachineMultiblock.getEntry(world, x, y, z, -1, block, meta);

        if (entry != null) {
            if (wand.consumeAllVisCrafting(stack, player, new AspectList().add(Aspect.ORDER, 20), true)) {
                if (!world.isRemote) {
                    BBBlock.dawnMachineMultiblock.convertMultiblockWithOrientationFromSideBlock(world, x, y, z, entry.getValue(), false, entry.getKey());
                    return true;
                }
                return false;
            }
        }

        return false;
    }
}
