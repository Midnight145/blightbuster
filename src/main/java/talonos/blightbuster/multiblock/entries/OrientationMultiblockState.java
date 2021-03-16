package talonos.blightbuster.multiblock.entries;

import net.minecraft.block.Block;

public class OrientationMultiblockState extends BasicMultiblockState {

    public OrientationMultiblockState(Block block) {
        super(block);
    }

    public OrientationMultiblockState(Block block, int meta) {
        super(block, meta);
    }

    public OrientationMultiblockState(Block block, int meta, int metaFlags) {
        super(block, meta, metaFlags);
    }

    @Override
    public int getReplacementMeta(int orientation) {
        return (this.getMeta() & this.getMetaFlags()) | (orientation & 0x3);
    }
}
