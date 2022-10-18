package talonos.blightbuster.blocks;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.multiblock.BlockMultiblock;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.ConfigBlocks;

public class BlockDawnMachineDummy extends BlockMultiblock {

	private IIcon backgroundTop;
	private IIcon backgroundSide;
	private IIcon[] leftBufferLayer = new IIcon[6];
	private IIcon[] rightBufferLayer = new IIcon[6];

	protected BlockDawnMachineDummy() {
		super(Material.wood, BBBlock.dawnMachineMultiblock);

		this.setBlockName(BlightBuster.MODID + "_" + BBStrings.dawnMachineBufferName);
		this.setStepSound(soundTypeWood);
		this.setLightLevel(.875f);
		this.setBlockTextureName("dawnMachineBuffer");
		this.setHardness(10.0F);
		this.setResistance(500.0F);
		GameRegistry.registerBlock(this, this.getUnlocalizedName());
	}

	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister registry) {
		backgroundTop = registry.registerIcon("thaumcraft:silverwoodtop");
		backgroundSide = registry.registerIcon("thaumcraft:silverwoodside");
		IIcon frontLeftC = registry.registerIcon("blightbuster:limb-front-left-c");
		IIcon frontRightC = registry.registerIcon("blightbuster:limb-front-right-c");
		IIcon outsideLeftC = registry.registerIcon("blightbuster:limb-left-outside-c");
		IIcon outsideRightC = registry.registerIcon("blightbuster:limb-right-outside-c");
		IIcon blankDummy = registry.registerIcon("blightbuster:blankDummy");

		leftBufferLayer[0] = blankDummy;
		leftBufferLayer[1] = blankDummy;
		leftBufferLayer[2] = frontRightC;
		leftBufferLayer[3] = frontLeftC;
		leftBufferLayer[4] = outsideLeftC;
		leftBufferLayer[5] = blankDummy;

		rightBufferLayer[0] = blankDummy;
		rightBufferLayer[1] = blankDummy;
		rightBufferLayer[2] = frontLeftC;
		rightBufferLayer[3] = frontRightC;
		rightBufferLayer[4] = blankDummy;
		rightBufferLayer[5] = outsideRightC;
	}

	public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
		if (par5 == 1) {
			if (par1World.isRemote) { Thaumcraft.proxy.blockSparkle(par1World, par2, par3, par4, 16736256, 5); }
			return true;
		}
		return super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public int getRenderBlockPass() { return 1; }

	@Override
	public boolean canRenderInPass(int pass) {
		return (pass == 0 || pass == 1);
	}

	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {
		if (ForgeHooksClient.getWorldRenderPass() == 1)
			return getInputIcon(transformSide(side, meta), meta);

		if (side == 0 || side == 1)
			return backgroundTop;
		return backgroundSide;
	}

	private IIcon getInputIcon(int side, int meta) {
		int block = meta / 4;

		switch (block) {
		case 1:
			return rightBufferLayer[side];
		default:
			return leftBufferLayer[side];
		}
	}

	@Override
	public Item getItemDropped(int meta, Random par2Random, int par3) {
		return Item.getItemFromBlock(ConfigBlocks.blockMagicalLog);
	}

	@Override
	public int damageDropped(int p_149692_1_) {
		return 1;
	}
}
