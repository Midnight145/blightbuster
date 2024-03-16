package talonos.blightbuster.handlers;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.World;

import org.apache.commons.lang3.tuple.Pair;

import talonos.blightbuster.blocks.BBBlock;
import talonos.blightbuster.multiblock.BlockMultiblock;
import talonos.blightbuster.multiblock.entries.MultiblockEntry;
import talonos.blightbuster.tileentity.DawnChargerTileEntity;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.IWandTriggerManager;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.research.ResearchManager;

public class TalonosWandTriggerManager implements IWandTriggerManager {

    private final BlockTransportManager blockTransportManager = new BlockTransportManager();

    @Override
    public boolean performTrigger(World world, ItemStack wand, EntityPlayer player, int x, int y, int z, int side,
        int event) {

        switch (event) {
            case 0:
                if (ResearchManager.isResearchComplete(player.getCommandSenderName(), "DAWNMACHINE")) {
                    final boolean success = this.createDawnMachine(wand, player, world, x, y, z);
                    if (success) {
                        final Block convertedBlock = world.getBlock(x, y, z);
                        if (convertedBlock instanceof BlockMultiblock) {
                            final TileEntity controller = ((BlockMultiblock) convertedBlock)
                                .getMultiblockController(world, x, y, z);
                            if (controller != null) {
                                this.pairDawnMachineToWand(
                                    world,
                                    wand,
                                    controller.getWorldObj().provider.dimensionId,
                                    controller.xCoord,
                                    controller.yCoord,
                                    controller.zCoord);
                                return true;
                            }
                        }

                        player.addChatMessage(new ChatComponentTranslation("gui.offering.pairFailed"));
                    }
                    return success;
                }
                break;
            case 1:
                final Block convertedBlock = world.getBlock(x, y, z);

                if (convertedBlock instanceof BlockMultiblock) {
                    final TileEntity controller = ((BlockMultiblock) convertedBlock)
                        .getMultiblockController(world, x, y, z);
                    if (controller != null) {
                        if (world.isRemote) {
                            return false;
                        }

                        if (!this.isWandPaired(
                            wand,
                            controller.getWorldObj().provider.dimensionId,
                            controller.xCoord,
                            controller.yCoord,
                            controller.zCoord)) {
                            this.pairDawnMachineToWand(
                                world,
                                wand,
                                controller.getWorldObj().provider.dimensionId,
                                controller.xCoord,
                                controller.yCoord,
                                controller.zCoord);
                            player.addChatMessage(new ChatComponentTranslation("gui.offering.pairSucceeded"));
                        } else {
                            player.addChatMessage(new ChatComponentTranslation("gui.offering.pairAlreadyExists"));
                        }
                        return true;
                    }
                }

                break;
            case 2:
                if (world.isRemote) {
                    return false;
                }

                final NBTTagCompound wandTag = wand.getTagCompound();
                if (wandTag == null || !wandTag.hasKey("DawnMachine", 10)) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.noPairing"));
                    return false;
                }

                final NBTTagCompound pairingTag = wandTag.getCompoundTag("DawnMachine");
                if (pairingTag.getInteger("Dimension") != world.provider.dimensionId) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.wrongDimension"));
                    return false;
                }

                final int dawnMachineX = pairingTag.getInteger("X");
                final int dawnMachineY = pairingTag.getInteger("Y");
                final int dawnMachineZ = pairingTag.getInteger("Z");

                if (world.getBlock(dawnMachineX, dawnMachineY, dawnMachineZ) != BBBlock.dawnMachine) {
                    player.addChatMessage(new ChatComponentTranslation("gui.offering.pairingDestroyed"));
                    return false;
                }
                for (int clearY = 0; clearY < 5; clearY++) {
                    for (int clearX = -2; clearX <= 2; clearX++) {
                        for (int clearZ = -2; clearZ <= 2; clearZ++) {
                            if (clearY == 0 && clearX == 0 && clearZ == 0) {
                                continue;
                            }
                            if (player.getPlayerCoordinates().posX == x + clearX
                                && player.getPlayerCoordinates().posY == y + clearY
                                && player.getPlayerCoordinates().posZ == z + clearZ) {
                                player.addChatMessage(new ChatComponentTranslation("gui.offering.movePlayer"));
                                return false;
                            }
                            if (!world.isAirBlock(x + clearX, y + clearY, z + clearZ)) {
                                final Block clearBlock = world.getBlock(x + clearX, y + clearY, z + clearZ);
                                if (!clearBlock.getMaterial()
                                    .isReplaceable()) {
                                    player.addChatMessage(new ChatComponentTranslation("gui.offering.clearArea"));
                                    return false;
                                }
                            }
                        }
                    }
                }

                final boolean result = this.blockTransportManager
                    .transport(world, dawnMachineX, dawnMachineY + 1, dawnMachineZ, x, y + 2, z);

                if (result) {
                    final TileEntity controller = world.getTileEntity(x, y + 1, z);
                    this.pairDawnMachineToWand(
                        world,
                        wand,
                        controller.getWorldObj().provider.dimensionId,
                        controller.xCoord,
                        controller.yCoord,
                        controller.zCoord);
                    this.moveDawnCharger(controller.getWorldObj(), wand);
                }

                break;
            case 3:
                if (world.isRemote
                    || !ResearchManager.isResearchComplete(player.getCommandSenderName(), "DAWNCHARGER")) {
                    return false;
                }
                final TileEntity charger = world.getTileEntity(x, y, z);
                this.pairDawnChargerToDawnMachine(
                    charger.getWorldObj(),
                    wand,
                    charger.getWorldObj().provider.dimensionId,
                    x,
                    y,
                    z);
                player.addChatMessage(new ChatComponentTranslation("gui.charger.pairSuccessful"));
        }
        return false;
    }

    private void moveDawnCharger(World world, ItemStack wand) {
        final NBTTagCompound wandTag = wand.getTagCompound();
        if (wandTag.getTag("DawnCharger") == null) {
            return;
        }
        final NBTTagCompound chargerTag = wandTag.getCompoundTag("DawnCharger");
        final DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world
            .getTileEntity(chargerTag.getInteger("X"), chargerTag.getInteger("Y"), chargerTag.getInteger("Z"));
        if (dawnCharger == null) {
            return;
        }
        final NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
        dawnCharger.pairDawnMachine(
            dawnMachineTag.getInteger("X"),
            dawnMachineTag.getInteger("Y"),
            dawnMachineTag.getInteger("Z"),
            dawnMachineTag.getInteger("Dimension"));
        dawnCharger.setDawnMachinePaired(true);

    }

    private boolean isWandPaired(ItemStack wand, int dimension, int x, int y, int z) {
        if (wand.getTagCompound() == null) {
            return false;
        }

        final NBTTagCompound wandTag = wand.getTagCompound();

        if (!wandTag.hasKey("DawnMachine", 10)) {
            return false;
        }

        final NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
        if (dawnMachineTag.getInteger("Dimension") != dimension || dawnMachineTag.getInteger("X") != x
            || dawnMachineTag.getInteger("Y") != y
            || dawnMachineTag.getInteger("Z") != z) {
            return false;
        }
        return true;
    }

    private void pairDawnMachineToWand(World world, ItemStack wand, int dimension, int x, int y, int z) {
        NBTTagCompound wandTag = wand.getTagCompound();
        if (wandTag == null) {
            wandTag = new NBTTagCompound();
            wand.setTagCompound(wandTag);
        }

        final NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");
        dawnMachineTag.setInteger("Dimension", dimension);
        dawnMachineTag.setInteger("X", x);
        dawnMachineTag.setInteger("Y", y);
        dawnMachineTag.setInteger("Z", z);
        wandTag.setTag("DawnMachine", dawnMachineTag);

        final NBTTagCompound wandChargerTag = wandTag.getCompoundTag("DawnCharger");
        final DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world.getTileEntity(
            wandChargerTag.getInteger("X"),
            wandChargerTag.getInteger("Y"),
            wandChargerTag.getInteger("Z"));

        if (dawnCharger == null) {
            return;
        }

        dawnCharger.pairDawnMachine(
            dawnMachineTag.getInteger("X"),
            dawnMachineTag.getInteger("Y"),
            dawnMachineTag.getInteger("Z"),
            dawnMachineTag.getInteger("Dimension"));

    }

    private void pairDawnChargerToDawnMachine(World world, ItemStack wand, int dimension, int x, int y, int z) {
        final NBTTagCompound wandTag = wand.getTagCompound();
        NBTTagCompound wandChargerTag;
        if (!wandTag.hasKey("DawnMachine")) {
            return;
        }
        wandChargerTag = new NBTTagCompound();
        wandChargerTag.setInteger("X", x);
        wandChargerTag.setInteger("Y", y);
        wandChargerTag.setInteger("Z", z);
        wandTag.setTag("DawnCharger", wandChargerTag);

        final NBTTagCompound dawnMachineTag = wandTag.getCompoundTag("DawnMachine");

        final DawnChargerTileEntity dawnCharger = (DawnChargerTileEntity) world.getTileEntity(x, y, z);

        dawnCharger.pairDawnMachine(
            dawnMachineTag.getInteger("X"),
            dawnMachineTag.getInteger("Y"),
            dawnMachineTag.getInteger("Z"),
            dawnMachineTag.getInteger("Dimension"));
        dawnCharger.setDawnMachinePaired(true);
    }

    private NBTTagCompound createChargerTag(NBTTagCompound dawnMachineTag) {
        final NBTTagCompound dawnChargerTag = new NBTTagCompound();

        dawnChargerTag.setInteger("Dimension", dawnMachineTag.getInteger("Dimension"));
        dawnChargerTag.setInteger("X", dawnMachineTag.getInteger("X"));
        dawnChargerTag.setInteger("Y", dawnMachineTag.getInteger("Y"));
        dawnChargerTag.setInteger("Z", dawnMachineTag.getInteger("Z"));
        return dawnChargerTag;
    }

    private boolean createDawnMachine(ItemStack stack, EntityPlayer player, World world, int x, int y, int z) {
        if (world.isRemote) {
            return false;
        }

        final ItemWandCasting wand = (ItemWandCasting) stack.getItem();

        final Block block = world.getBlock(x, y, z);
        final int meta = world.getBlockMetadata(x, y, z);
        final Pair<MultiblockEntry, Integer> entry = BBBlock.dawnMachineMultiblock
            .getEntry(world, x, y, z, -1, block, meta);

        if (entry != null && wand.consumeAllVisCrafting(stack, player, new AspectList().add(Aspect.ORDER, 20), true)
            && !world.isRemote) {
            BBBlock.dawnMachineMultiblock
                .convertMultiblockWithOrientationFromSideBlock(world, x, y, z, entry.getValue(), false, entry.getKey());
            return true;
        }
        return false;
    }
}
