package talonos.blightbuster.multiblock.entries;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public interface IMultiblockEntryState {
	boolean isBlockState(World world, int x, int y, int z);
	
	boolean isBlockState(Block block, int meta);
	
	void replaceWithState(World world, int x, int y, int z, int orientation);
}
