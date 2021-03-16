package talonos.blightbuster.multiblock.entries;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class NullMultiblockState implements IMultiblockEntryState {
    @Override
    public boolean isBlockState(World world, int x, int y, int z) {
        return true;
    }

    @Override
    public boolean isBlockState(Block block, int meta) {
        return true;
    }

    @Override
    public void replaceWithState(World world, int x, int y, int z, int orientation) {
        //This doesn't do anything
    }
}
