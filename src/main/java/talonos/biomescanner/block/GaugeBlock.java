package talonos.biomescanner.block;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.util.IIcon;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BSStrings;
import talonos.biomescanner.BiomeScanner;

public class GaugeBlock extends BSBlock {

    int pos;

    public GaugeBlock() {
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setBlockUnbreakable();
        this.setLightLevel(1.0f);
    }

    public GaugeBlock setPos(int pos) {
        this.pos = pos;
        this.setBlockName(BiomeScanner.MODID + "_" + BSStrings.GaugeBlockName + pos);
        return this;
    }

    @SideOnly(Side.CLIENT)
    private IIcon[] icons;

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        int stateCount = (this.pos == 1) ? 16 : 14;
        icons = new IIcon[stateCount];
        for (int i = 0; i < stateCount; i++) {
            icons[i] = par1IconRegister.registerIcon(BiomeScanner.MODID + ":" + "statbar" + (this.pos * 16 + i));
        }
    }

    /**
     * From the specified side and block metadata retrieves the blocks texture.
     * Args: side, metadata
     */
    public IIcon getIcon(int par1, int par2) {
        if (par2 < icons.length) return icons[par2];
        else return icons[icons.length - 1];
    }
}
