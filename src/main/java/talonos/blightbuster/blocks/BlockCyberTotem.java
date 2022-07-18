package talonos.blightbuster.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;

public class BlockCyberTotem extends BBBlock {

	private IIcon topIcon;
	private IIcon sideIcon;
	private IIcon bottomIcon;

	public BlockCyberTotem() {
		super(Material.wood);
		this.setLightLevel(.875f);
		this.setHardness(10.0F);
		this.setResistance(500.0F);
		this.setBlockName(BlightBuster.MODID + "_" + BBStrings.cyberTotemName);
		this.setStepSound(soundTypeWood);
		this.setCreativeTab(CreativeTabs.tabBlock);
		GameRegistry.registerBlock(this, this.getUnlocalizedName());
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister registry) {
		topIcon = registry.registerIcon("blightbuster:dawnMachineTop");
		sideIcon = registry.registerIcon("blightbuster:dawnMachineDeactivated");
		bottomIcon = registry.registerIcon("blightbuster:dawnMachineBottom");
	}

	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(int side, int meta) {
		if (side == 1)
			return topIcon;
		if (side == 0)
			return bottomIcon;
		return this.sideIcon;
	}
}
