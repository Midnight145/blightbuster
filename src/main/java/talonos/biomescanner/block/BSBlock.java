package talonos.biomescanner.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import cpw.mods.fml.common.registry.GameRegistry;

public class BSBlock extends Block {

    /**
     * Constructor for when no material is passed on.
     * Default material: rock
     */
    public BSBlock() {
        super(Material.rock);
    }

    /**
     * Constructor for defined material.
     * 
     * @param material
     */
    public BSBlock(Material material) {
        super(material);
    }

    public static Block islandMapper;
    public static Block scannerController;
    public static Block gaugeBot;
    public static Block gaugeMid;
    public static Block gaugeTop;
    public static Block bedrockBrick;
    public static Block scannerGauge;

    public static void init() {
        islandMapper = new BlockIslandMapper();
        scannerController = new BlockScannerController();
        gaugeBot = new GaugeBlock().setPos(0);
        gaugeMid = new GaugeBlock().setPos(1);
        gaugeTop = new GaugeBlock().setPos(2);
        bedrockBrick = new BedrockBrick();
        scannerGauge = new BlockScannerGauge();

        GameRegistry.registerBlock(gaugeBot, gaugeBot.getUnlocalizedName());
        GameRegistry.registerBlock(gaugeMid, gaugeMid.getUnlocalizedName());
        GameRegistry.registerBlock(gaugeTop, gaugeTop.getUnlocalizedName());
    }

}
