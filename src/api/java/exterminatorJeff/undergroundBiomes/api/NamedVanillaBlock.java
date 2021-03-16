package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;

public class NamedVanillaBlock extends NamedBlock
{
    public static final NamedBlock cobblestone = new NamedVanillaBlock(Blocks.cobblestone);
    public static final NamedBlock cobblestone_wall = new NamedVanillaBlock(Blocks.cobblestone_wall);
    public static final NamedBlock dispenser = new NamedVanillaBlock(Blocks.dispenser);
    public static final NamedBlock furnace = new NamedVanillaBlock(Blocks.furnace);

    public static final NamedBlock lever = new NamedVanillaBlock(Blocks.lever);
    public static final NamedBlock piston = new NamedVanillaBlock(Blocks.piston);
    public static final NamedBlock planks = new NamedVanillaBlock(Blocks.planks);
    public static final NamedBlock stone_pressure_plate = new NamedVanillaBlock(Blocks.stone_pressure_plate);

    public static final NamedBlock sand = new NamedVanillaBlock(Blocks.sand);
    public static final NamedBlock sandstone = new NamedVanillaBlock(Blocks.sandstone);
    public static final NamedBlock smoothSandstone = new NamedVanillaBlock(Blocks.sandstone);
    public static final NamedBlock stairsCobblestone = new NamedVanillaBlock(Blocks.stone_stairs);
    public static final NamedBlock stairsStoneBrick = new NamedVanillaBlock(Blocks.stone_brick_stairs);
    public static final NamedBlock stone = new NamedVanillaBlock(Blocks.stone);
    public static final NamedBlock stoneBrick = new NamedVanillaBlock(Blocks.stonebrick);
    public static final NamedBlock stoneButton = new NamedVanillaBlock(Blocks.stone_button);
    public static final NamedBlock stoneSingleSlab = new NamedVanillaBlock(Blocks.stone_slab);
    public static final NamedBlock torchRedstoneActive = new NamedVanillaBlock(Blocks.redstone_torch);

    public NamedVanillaBlock(String name) {
        super(name);
        this.id = UBIDs.blockID(name);
        this.block = UBIDs.blockNamed(name);
    }

    public NamedVanillaBlock(Block _block) {
        super(_block.getUnlocalizedName());
        this.id = Block.getIdFromBlock(_block);
        this.block = _block;
    }

    public Block block() {
        if (this.block == null) {
            this.block = UBIDs.blockNamed(internal());
        }

        return this.block;
    }
}