package exterminatorJeff.undergroundBiomes.api;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

import cpw.mods.fml.common.registry.GameRegistry;

public class NamedBlock extends Names {

    protected int id;
    public Block block;
    public static final String modid = "undergroundBiomes";

    public NamedBlock(String internalName) {
        super(internalName);
    }

    public void gameRegister(Block _block, Class itemClass) {
        this.block = _block;
        GameRegistry.registerBlock(this.block, itemClass, internal());
    }

    public void register(int _id, Block _block) {
        if (this.block != null) {
            throw duplicateRegistry();
        }

        reRegister(_id, _block);
    }

    public void reRegister(int _id, Block _block) {
        this.id = _id;
        this.block = _block;
        this.block.setBlockName(external());
        Block current = Block.getBlockById(_id);
        if (current != this.block) {
            if (current != null) {
                throw new RuntimeException(internal() + " has been replaced by " + current.toString());
            }
            Block.blockRegistry.addObject(this.id, internal(), _block);
        }
    }

    public Block block() {
        return this.block;
    }

    public boolean matches(Item compared) {
        if ((compared instanceof ItemBlock)) {
            return ((ItemBlock) compared).field_150939_a.equals(this.block);
        }
        return false;
    }

    public boolean matches(Block compared) {
        return compared.equals(block());
    }

    public int ID() {
        return Block.getIdFromBlock(block());
    }

    public Item matchingItem(Block block) {
        return Item.getItemById(Block.getIdFromBlock(block));
    }
}
