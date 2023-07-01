package talonos.blightbuster.items;

import java.util.HashSet;
import java.util.Set;

import cpw.mods.fml.common.registry.GameRegistry;
import exterminatorJeff.undergroundBiomes.api.UBAPIHook;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.world.World;
import net.minecraft.world.gen.layer.IntCache;
import net.minecraftforge.common.util.ForgeDirection;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.lib.world.ThaumcraftWorldGenerator;

public class ItemSuperTestWorldTainter extends Item {
	
	// All items in this set have a 50% chance of being replaced with Fibrous Taint
	// when the world-tainter is used.
	private static Set<Block> toRandomlyReplace;
	
	// Boilerplate setup stuff.
	public ItemSuperTestWorldTainter() {
		setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.worldTainterName + "super");
		GameRegistry.registerItem(this, BBStrings.worldTainterName + "super");
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName(BlightBuster.MODID + ":" + BBStrings.worldTainterName);
	}
	
	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World theWorld, EntityPlayer thePlayer) {
		// Lazily initialize the list of stuff to randomly replace.
		if (toRandomlyReplace == null) {
			toRandomlyReplace = new HashSet<Block>();
			toRandomlyReplace.add(Blocks.snow_layer);
			toRandomlyReplace.add(GameRegistry.findBlock("Natura", "N Crops"));
		}
		
		// Only do server-side
		if (!theWorld.isRemote) {
			// Get the chunk the player is in (round down to the nearest multiple of 16)
			int x = ((int) thePlayer.posX / 16) * 16;
			int z = ((int) thePlayer.posZ / 16) * 16;
			
			// Inform the player.
			thePlayer.addChatMessage(new ChatComponentText("Tainting and UBifying world based on coords: "));
			thePlayer.addChatMessage(new ChatComponentText("  xSection: " + x + ", zSection: " + z));
			
			// Taint an 11 chunk diameter area.
			for (int xLoc = 0; xLoc < 1800; xLoc++) {
				for (int zLoc = z - 32; zLoc < z + 48; zLoc++) {
					// Dunno how this works. Thaumcraft's "Utils" package does all the real work. :/
					BlightbusterNetwork.setBiomeAt(theWorld, xLoc, zLoc, ThaumcraftWorldGenerator.biomeTaint);
					
					// For each Y level...
					for (int yLoc = 1; yLoc < 254; yLoc++) {
						// Replace Air blocks on solid surfaces with Taint blocks
						if (theWorld.isSideSolid(xLoc, yLoc - 1, zLoc, ForgeDirection.UP, false)
								&& theWorld.getBlock(xLoc, yLoc, zLoc) == Blocks.air) {
							theWorld.setBlock(xLoc, yLoc, zLoc, ConfigBlocks.blockTaintFibres);
						}
						// Maybe replace other blocks with taint blocks.
						if (toRandomlyReplace.contains(theWorld.getBlock(xLoc, yLoc, zLoc))) {
							if (theWorld.rand.nextBoolean()) {
								theWorld.setBlock(xLoc, yLoc, zLoc, ConfigBlocks.blockTaintFibres);
							}
						}
					}
				}
			}
			
			thePlayer.addChatMessage(new ChatComponentText("  World should now be tainted.")); // Well it should. :/
			
			// While we're at it, UBify all ores in the area around us as well.
			for (int xLoc = 0; xLoc < 1800; xLoc += 16) {
				for (int zLoc = z - 32; zLoc < z + 48; zLoc += 16) {
					UBAPIHook.ubAPIHook.ubOreTexturizer.redoOres(xLoc, zLoc, theWorld);
				}
			}
			
			// While we're at *that*, also kill any gravel ore.
			
			for (int xLoc = 0; xLoc < 1800; xLoc++) {
				for (int zLoc = z - 32; zLoc < z + 48; zLoc++) {
					for (int yLoc = 254; yLoc > 1; yLoc--) {
						Block gravelOre = GameRegistry.findBlock("TConstruct", "GravelOre");
						if (theWorld.getBlock(xLoc, yLoc, zLoc).equals(gravelOre)) {
							theWorld.setBlock(xLoc, yLoc, zLoc, Blocks.gravel);
						}
					}
				}
			}
			
			thePlayer.addChatMessage(new ChatComponentText("  Ores should now be fixed, with gravel ore deleted."));
			
			// I was told by Zeno112 that this should prevent memory leaks.
			theWorld.getChunkProvider().unloadQueuedChunks();
			IntCache.resetIntCache();
		}
		
		return itemStack;
	}
}
