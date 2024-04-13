package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.block.Block;

import Zeno410Utils.BlockState;

public class BlockCodes extends BlockState {

    public final NamedBlock name;
    public final BlockCodes onDrop;
    private final int metadataHashcode;

    public BlockCodes(Block block, int metadata) {
        super(block, metadata);
        this.name = null;
        this.onDrop = this;
        this.metadataHashcode = new Integer(metadata).hashCode();
    }

    public BlockCodes(NamedBlock namer, int metadata) {
        super(namer.block(), metadata);
        this.name = namer;
        if (this.block == null) {
            throw new RuntimeException("couldn't find block for " + namer.internal());
        }
        this.onDrop = this;
        this.metadataHashcode = new Integer(metadata).hashCode();
    }

    public BlockCodes(NamedBlock namer, int metadata, BlockCodes onDrop) {
        super(namer.block(), metadata);
        this.name = namer;
        this.onDrop = onDrop;
        this.metadataHashcode = new Integer(metadata).hashCode();
    }

    public int hashcode() {
        return this.block.hashCode() + this.metadataHashcode;
    }

    public boolean equals(Object compared) {
        if ((compared instanceof BlockCodes)) {
            BlockCodes comparedCodes = (BlockCodes) compared;
            if ((this.block == comparedCodes.block) && (this.metadata == comparedCodes.metadata)) return true;
        }
        return false;
    }
}
