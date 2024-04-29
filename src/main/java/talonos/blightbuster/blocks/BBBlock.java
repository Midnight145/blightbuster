package talonos.blightbuster.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;

import talonos.blightbuster.BlightbusterConfig;
import talonos.blightbuster.multiblock.DawnMachineMultiblock;
import talonos.blightbuster.multiblock.Multiblock;

/**
 * Thanks to Martijn Woudstra for the code samples.
 *
 * @Author Talonos
 */

public class BBBlock extends Block {

    /**
     * Constructor for when no material is passed on. Default material: rock
     */
    public BBBlock() {
        super(Material.rock);
    }

    public BBBlock(Material material) {
        super(material);
    }

    public static Block dawnTotem;
    public static BlockDawnMachineInput dawnMachineInput;
    public static Block dawnMachineBuffer;
    public static BlockDawnMachine dawnMachine;
    public static Block cyberTotem;
    public static Multiblock dawnMachineMultiblock;
    public static Block offering;
    public static Block dawnCharger;

    public static void init() {
        if (BlightbusterConfig.enableDawnMachine) {
            dawnMachineMultiblock = new DawnMachineMultiblock();
            dawnMachineInput = new BlockDawnMachineInput();
            dawnMachineBuffer = new BlockDawnMachineDummy();
            dawnMachine = new BlockDawnMachine();
            cyberTotem = new BlockCyberTotem();

            if (BlightbusterConfig.enableDawnOffering) {
                offering = new BlockOffering();
            }
            if (BlightbusterConfig.enableDawnCharger && BlightbusterConfig.enableRf) {
                dawnCharger = new BlockDawnCharger();
            }
        }

        if (BlightbusterConfig.enableDawnTotem) {
            dawnTotem = new BlockDawnTotem(Material.wood);
        }

        dawnMachineMultiblock.init();
    }

}
