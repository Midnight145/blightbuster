package talonos.blightbuster.items;

import java.util.Set;

import cpw.mods.fml.common.registry.GameRegistry;
import exterminatorJeff.undergroundBiomes.api.UBStrataColumnProvider;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.lib.network.PacketHandler;
import thaumcraft.common.lib.network.playerdata.PacketAspectPool;
import thaumcraft.common.lib.research.ResearchManager;

public class ItemAlienTome extends Item {
	static private Set<Block> listOfOres;

	public ItemAlienTome() {
		setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.researchnoteName);
		GameRegistry.registerItem(this, BBStrings.researchnoteName);
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName(BlightBuster.MODID + ":" + BBStrings.researchnoteName);
	}

	UBStrataColumnProvider p;

	@Override
	public ItemStack onItemRightClick(ItemStack itemStack, World theWorld, EntityPlayer thePlayer) {

		if (!theWorld.isRemote) {
			grantResearch(thePlayer, 4);
		}
		else {
			thePlayer.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("blightbuster.researchnote")));
		}

		itemStack.stackSize -= 1;

		return itemStack;
	}

	// Code copied from WarpEvents. Seriously should have been public and in an API.

	private static void grantResearch(EntityPlayer player, int times) {
		for (int a = 0; a < times; a++) {
			Aspect aspect = (Aspect) Aspect.getPrimalAspects().get(player.worldObj.rand.nextInt(6));
			Thaumcraft.proxy.playerKnowledge.addAspectPool(player.getCommandSenderName(), aspect, (short) 10);
			PacketHandler.INSTANCE.sendTo(
					new PacketAspectPool(aspect.getTag(), Short.valueOf((short) 10),
							Short.valueOf(Thaumcraft.proxy.playerKnowledge.getAspectPoolFor(player.getCommandSenderName(), aspect))),
					(EntityPlayerMP) player);
		}
		ResearchManager.scheduleSave(player);
	}
}
