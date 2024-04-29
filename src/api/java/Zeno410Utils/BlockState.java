package Zeno410Utils;

import net.minecraft.block.Block;

public class BlockState {

    public final Block block;
    public final int metadata;

    public BlockState(Block block, int metadata) {
        this.block = block;
        this.metadata = metadata;
    }
}
