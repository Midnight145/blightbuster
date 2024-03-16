package talonos.blightbuster.items;

import java.util.HashSet;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.gen.layer.IntCache;

import cpw.mods.fml.common.registry.GameRegistry;
import exterminatorJeff.undergroundBiomes.api.UBAPIHook;
import exterminatorJeff.undergroundBiomes.api.UBStrataColumn;
import exterminatorJeff.undergroundBiomes.api.UBStrataColumnProvider;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import thaumcraft.common.config.ConfigBlocks;

public class ItemWorldOreKiller extends Item {

    static private Set<Block> listOfOres;

    public ItemWorldOreKiller() {
        setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.oreKillerName);
        GameRegistry.registerItem(this, BBStrings.oreKillerName);
        setCreativeTab(CreativeTabs.tabMaterials);
        setTextureName(BlightBuster.MODID + ":" + BBStrings.oreKillerName);
    }

    UBStrataColumnProvider p;

    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World theWorld, EntityPlayer thePlayer) {

        if (!theWorld.isRemote) {
            int x = ((int) thePlayer.posX / 16) * 16;
            int z = ((int) thePlayer.posZ / 16) * 16;

            thePlayer.addChatMessage(new ChatComponentText("Killing ores in world based on coords: "));
            thePlayer.addChatMessage(new ChatComponentText("  xSection: " + x + ", zSection: " + z));

            if (p == null) {
                p = UBAPIHook.ubAPIHook.dimensionalStrataColumnProvider.ubStrataColumnProvider(0);
            }

            if (listOfOres == null) {
                listOfOres = new HashSet<Block>();
                listOfOres.add(GameRegistry.findBlock("minecraft", "gold_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "iron_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "coal_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "lapis_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "diamond_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "redstone_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "glowstone"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "emerald_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "quartz_ore"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "netherrack"));
                listOfOres.add(GameRegistry.findBlock("minecraft", "end_stone"));
                listOfOres.add(GameRegistry.findBlock("appliedenergistics2", "tile.OreQuartz"));
                listOfOres.add(GameRegistry.findBlock("appliedenergistics2", "tile.OreQuartzCharged"));
                listOfOres.add(GameRegistry.findBlock("Thaumcraft", "blockCustomOre"));
                listOfOres.add(GameRegistry.findBlock("ThermalFoundation", "Ore"));
                listOfOres.add(GameRegistry.findBlock("BigReactors", "YelloriteOre"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "base.ore"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "ender.ore"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "fantasy.ore"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "nether.ore"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "precious.ore"));
                listOfOres.add(GameRegistry.findBlock("Metallurgy", "utility.ore"));
                listOfOres.add(GameRegistry.findBlock("NetherOres", "tile.netherores.ore.0"));
                listOfOres.add(GameRegistry.findBlock("NetherOres", "tile.netherores.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreRedstone"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreRedstone"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreRedstone"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreCoal"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreCoal"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreCoal"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreDiamond"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreDiamond"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreDiamond"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreLapis"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreLapis"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreLapis"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreEmerald"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreEmerald"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreEmerald"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreGold"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreGold"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreGold"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_oreIron"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_oreIron"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_oreIron"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore"));
                listOfOres
                    .add(GameRegistry.findBlock("UndergroundBiomes", "igneous_appliedenergistics2.OreQuartzCharged.7"));
                listOfOres.add(
                    GameRegistry.findBlock("UndergroundBiomes", "metamorphic_appliedenergistics2.OreQuartzCharged.7"));
                listOfOres.add(
                    GameRegistry.findBlock("UndergroundBiomes", "sedimentary_appliedenergistics2.OreQuartzCharged.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.14"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.14"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.14"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_thermalfoundation.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_thermalfoundation.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_thermalfoundation.ore.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.6"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.13"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.13"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.13"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.base.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.base.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.base.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_thermalfoundation.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_thermalfoundation.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_thermalfoundation.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.precious.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.precious.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.precious.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.precious.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.precious.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.precious.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.11"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.11"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.11"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore.5"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.precious.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.precious.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.precious.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore.2"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.utility.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.utility.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.utility.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_appliedenergistics2.OreQuartz.7"));
                listOfOres
                    .add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_appliedenergistics2.OreQuartz.7"));
                listOfOres
                    .add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_appliedenergistics2.OreQuartz.7"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.8"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.8"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.8"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.fantasy.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.fantasy.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.fantasy.ore.4"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_netherquartz"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_netherquartz"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_netherquartz"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.base.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.base.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.base.ore.1"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_blockCustomOre.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_blockCustomOre.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_blockCustomOre.3"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "igneous_metal.block.base.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "metamorphic_metal.block.base.ore"));
                listOfOres.add(GameRegistry.findBlock("UndergroundBiomes", "sedimentary_metal.block.base.ore"));
                listOfOres.add(GameRegistry.findBlock("TConstruct", "BattleSignBlock"));
            }

            for (int xLoc = x - 64; xLoc < x + 80; xLoc++) {
                for (int zLoc = z - 64; zLoc < z + 80; zLoc++) {
                    UBStrataColumn s = p.strataColumn(xLoc, zLoc);
                    for (int yLoc = 254; yLoc > 1; yLoc--) {
                        if (theWorld.getBlock(xLoc, yLoc, zLoc)
                            .equals(Blocks.sand)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.air)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.water)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.flowing_water)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.sand)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.cactus)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.deadbush)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.glass)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.sandstone)
                            && !theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(ConfigBlocks.blockTaintFibres)) {
                            theWorld.setBlock(xLoc, yLoc, zLoc, Blocks.stone);
                        }
                        if (theWorld.getBlock(xLoc, yLoc, zLoc)
                            .equals(Blocks.sandstone)
                            && theWorld.getBlock(xLoc, yLoc + 1, zLoc)
                                .equals(Blocks.stone)) {
                            theWorld.setBlock(xLoc, yLoc, zLoc, Blocks.stone);
                        }
                        if (listOfOres.contains(theWorld.getBlock(xLoc, yLoc, zLoc))) {
                            if (theWorld.getTileEntity(xLoc, yLoc, zLoc) != null)
                                theWorld.removeTileEntity(xLoc, yLoc, zLoc);
                            theWorld.setBlock(xLoc, yLoc, zLoc, s.stone(yLoc).block, s.stone(yLoc).metadata, 2);
                        }
                    }
                }
            }

            thePlayer.addChatMessage(new ChatComponentText("  ores should be dead."));

            theWorld.getChunkProvider()
                .unloadQueuedChunks();

            IntCache.resetIntCache();

        }

        return itemStack;
    }
}
