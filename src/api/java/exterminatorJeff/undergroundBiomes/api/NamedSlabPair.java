package exterminatorJeff.undergroundBiomes.api;

import java.util.logging.Logger;

import net.minecraft.block.Block;

import Zeno410Utils.Zeno410Logger;

public class NamedSlabPair {

    public static final Logger logger = new Zeno410Logger("NamedSlabPair").logger();
    public final NamedBlock half;
    public final NamedBlock full;

    public NamedSlabPair(NamedBlock material) {
        this.half = new NamedSlab(material.internal() + "HalfSlab");
        this.full = new NamedSlab(material.internal() + "FullSlab");
    }

    public static class NamedSlab extends NamedBlock {

        public NamedSlab(String name) {
            super(name);
        }

        public Block block() {
            Block result = Block.getBlockFromName(internal());
            if (result == null) {
                result = Block.getBlockFromName(external());
            }
            return result;
        }
    }
}
