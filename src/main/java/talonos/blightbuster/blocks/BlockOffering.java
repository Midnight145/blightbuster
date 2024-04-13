package talonos.blightbuster.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;

public class BlockOffering extends Block {

    public BlockOffering() {
        super(Material.iron);
        this.setHardness(10.0f);
        this.setBlockName(BlightBuster.MODID + "_" + BBStrings.offeringName);
        this.setStepSound(soundTypeMetal);
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockTextureName("blightbuster:spiritStone");
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }
}
