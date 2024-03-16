package talonos.biomescanner.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BSStrings;
import talonos.biomescanner.BiomeScanner;

public class BedrockBrick extends BSBlock {

    public BedrockBrick() {
        this.setBlockName(BiomeScanner.MODID + "_" + BSStrings.bedrockBrickName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    /**
     * Overrides the registerBlockIcon method.
     * This method handles all the textures.
     * Call registerIcon() and pass it a
     * Format: [modid]:[blockname]
     * 
     * @param iconRegister
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        this.blockIcon = iconRegister.registerIcon(BiomeScanner.MODID + ":" + BSStrings.bedrockBrickName);
    }
}
