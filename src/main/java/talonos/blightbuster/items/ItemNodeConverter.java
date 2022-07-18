package talonos.blightbuster.items;

import java.util.List;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import talonos.blightbuster.BlightBuster;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.common.tiles.TileNode;

public class ItemNodeConverter extends Item {

	private final static String[] ITEM_NAMES = { "converter.normal", "converter.unstable", "converter.dark", "converter.tainted",
			"converter.hungry", "converter.pure" };

	ItemNodeConverter() {
		setUnlocalizedName(BlightBuster.MODID + "_nodeconverter");
		GameRegistry.registerItem(this, "nodeConverter");
		setCreativeTab(CreativeTabs.tabMaterials);
		setTextureName(BlightBuster.MODID + ":nodeconverter");
		setHasSubtypes(true);
	}

	@Override
	public String getUnlocalizedName(ItemStack item) {
		// TODO Auto-generated method stub
		return BlightBuster.MODID + "_" + ItemNodeConverter.ITEM_NAMES[item.getItemDamage()];

	}

	@Override
	public boolean isDamageable() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item parItem, CreativeTabs parTab, List parListSubItems) {
		for (int i = 0; i < 6; i++) {
			parListSubItems.add(new ItemStack(this, 1, i));
		}
	}

	@Override
	public boolean onItemUse(ItemStack item, EntityPlayer player, World world, int x, int y, int z, int side, float px, float py,
			float pz) {
		TileEntity tile = player.getEntityWorld().getTileEntity(x, y, z);
		if (tile != null && tile instanceof TileNode) {
			TileNode node = (TileNode) tile;
			int metadata = item.getItemDamage();
			switch (metadata) {
			case 0:
				node.setNodeType(NodeType.NORMAL);
				break;
			case 1:
				node.setNodeType(NodeType.UNSTABLE);
				break;
			case 2:
				node.setNodeType(NodeType.DARK);
				break;
			case 3:
				node.setNodeType(NodeType.TAINTED);
				break;
			case 4:
				node.setNodeType(NodeType.HUNGRY);
				break;
			case 5:
				node.setNodeType(NodeType.PURE);
				break;
			default:
				node.setNodeType(NodeType.NORMAL);
				break;
			}

			node.markDirty();
			player.getEntityWorld().markBlockForUpdate(x, y, z);

			return true;
		}

		return false;
	}
}
