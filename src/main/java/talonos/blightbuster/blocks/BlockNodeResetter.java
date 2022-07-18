package talonos.blightbuster.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.NodeResetterTileEntity;

public class BlockNodeResetter extends Block {
	public BlockNodeResetter() {
		super(Material.wood);
		this.setHardness(5.0f);
		this.setBlockName(BlightBuster.MODID + "_" + "nodeResetter");
		this.setStepSound(soundTypeMetal);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setBlockTextureName("blightbuster:nodeResetter");
		GameRegistry.registerBlock(this, "nodeResetter");
	}

	@Override
	public boolean hasTileEntity(int meta) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(World world, int meta) {
		return new NodeResetterTileEntity();
	}
}
