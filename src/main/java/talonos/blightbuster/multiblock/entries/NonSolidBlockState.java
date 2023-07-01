package talonos.blightbuster.multiblock.entries;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class NonSolidBlockState implements IMultiblockEntryState {
	
	public NonSolidBlockState() {
		
	}
	
	@Override
	public boolean isBlockState(World world, int x, int y, int z) { return !world.getBlock(x, y, z).isOpaqueCube(); }
	
	@Override
	public boolean isBlockState(Block block, int meta) { return !block.isOpaqueCube(); }
	
	@Override
	public void replaceWithState(World world, int x, int y, int z, int orientation) {
		// Don't do anything, because these blocks aren't converted
	}
}
