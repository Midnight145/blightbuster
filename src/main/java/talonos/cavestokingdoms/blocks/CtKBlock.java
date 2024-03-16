package talonos.cavestokingdoms.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

/**
 * Thanks to Martijn Woudstra for the code samples.
 * 
 * @Author Talonos
 */

public class CtKBlock extends Block {

    /**
     * Constructor for when no material is passed on.
     * Default material: rock
     */
    public CtKBlock() {
        super(Material.rock);
    }

    /**
     * Constructor for defined material.
     * 
     * @param material
     */
    public CtKBlock(Material material) {
        super(material);
    }

    public static Block altarBlock;
    public static Block spiritStoneBlock;

    public static void init() {
        altarBlock = new AltarBlock();
        spiritStoneBlock = new SpiritStoneBlock();
    }

}
