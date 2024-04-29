package talonos.blightbuster.multiblock.entries;

import net.minecraft.block.Block;
import net.minecraft.world.World;

public class BasicMultiblockState implements IMultiblockEntryState {

    private final Block block;
    private final int meta;
    private final int metaFlags;

    public BasicMultiblockState(Block block) {
        this(block, 0, 0);
    }

    public BasicMultiblockState(Block block, int meta) {
        this(block, meta, 0xF);
    }

    public Block getBlock() {
        return this.block;
    }

    public int getMeta() {
        return this.meta;
    }

    public int getMetaFlags() {
        return this.metaFlags;
    }

    public BasicMultiblockState(Block block, int meta, int metaFlags) {
        this.block = block;
        this.meta = meta;
        this.metaFlags = metaFlags;
    }

    @Override
    public boolean isBlockState(World world, int x, int y, int z) {
        Block targetBlock = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);

        return isBlockState(targetBlock, meta);
    }

    @Override
    public boolean isBlockState(Block block, int meta) {
        return this.block == block && (this.meta & this.metaFlags) == (meta & this.metaFlags);
    }

    protected int getReplacementMeta(int orientation) {
        return this.meta & this.metaFlags;
    }

    @Override
    public void replaceWithState(World world, int x, int y, int z, int orientation) {
        int meta = getReplacementMeta(orientation);

        world.setBlock(x, y, z, this.block, meta, 3);
    }
}
