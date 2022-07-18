package talonos.blightbuster.items;

import java.util.Arrays;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import talonos.blightbuster.BlightBuster;

public class ItemBiomeHelper extends Item {

	ItemBiomeHelper() {

		this.setUnlocalizedName(BlightBuster.MODID + "_" + "biomeHelper");
		GameRegistry.registerItem(this, "biomeHelper");
		this.setCreativeTab(CreativeTabs.tabMaterials);
		this.setTextureName(BlightBuster.MODID + ":" + "biomeHelper");
		MinecraftForge.EVENT_BUS.register(this);

	}

	@Override
	public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player) {
		// TODO Auto-generated method stub
		if (!world.isRemote) {
			WorldServer server = (WorldServer) world;
			int x = (int) player.posX;
			int y = (int) player.posY;
			int z = (int) player.posZ;

			BiomeGenBase biome1 = world.getBiomeGenForCoords(x, z);
			System.out.println(biome1.getClass().getName());
			System.out.println("world.getBiomeGenForCoords: " + biome1.biomeName);
			System.out.println("Biome ID: " + biome1.biomeID);
			EnumCreatureType types[] = EnumCreatureType.values();
			for (EnumCreatureType type : types) {
				System.out
						.println("spawnableList for type " + type.name() + ": " + Arrays.asList(biome1.getSpawnableList(type).toString()));
			}

			return super.onItemRightClick(item, world, player);
		}
		return item;
	}
}
