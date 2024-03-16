package talonos.cavestokingdoms.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.cavestokingdoms.blocks.entities.AltarEntity;
import talonos.cavestokingdoms.lib.DEFS;

public class AltarBlock extends CtKBlock {

    public AltarBlock() {
        this.setBlockName(DEFS.MODID + "_" + DEFS.AltarBlockName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setBlockTextureName("bedrock");
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new AltarEntity();
    }

    // Icon stuff:

    /* ICONS */
    public static final int PLAIN = 0;
    public static final int LEFTSIDE = 1;
    public static final int RIGHTSIDE = 2;
    public static final int TOPLEFT = 3;
    public static final int TOPRIGHT = 4;
    public static final int BOTTOMLEFT = 5;
    public static final int BOTTOMRIGHT = 6;

    @SideOnly(Side.CLIENT)
    private static IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[7];
        for (int x = 0; x < 7; x++) {
            icons[x] = iconRegister.registerIcon(DEFS.MODID + ":" + DEFS.AltarBlockName + x);
        }
        this.blockIcon = icons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side == 0) {
            return icons[PLAIN];
        }
        TileEntity east = world.getTileEntity(x + 1, y, z);
        TileEntity west = world.getTileEntity(x - 1, y, z);
        TileEntity north = world.getTileEntity(x, y, z + 1);
        TileEntity south = world.getTileEntity(x, y, z - 1);
        boolean eastCovered = true;
        boolean westCovered = true;
        boolean northCovered = true;
        boolean southCovered = true;
        if (east == null || !(east instanceof AltarEntity)) {
            eastCovered = false;
        }
        if (west == null || !(west instanceof AltarEntity)) {
            westCovered = false;
        }
        if (north == null || !(north instanceof AltarEntity)) {
            northCovered = false;
        }
        if (south == null || !(south instanceof AltarEntity)) {
            southCovered = false;
        }

        if (side == 1) {
            // We are on top.
            if (northCovered) {
                if (eastCovered) {
                    return icons[BOTTOMLEFT];
                }
                if (westCovered) {
                    return icons[BOTTOMRIGHT];
                }
            }
            if (southCovered) {
                if (eastCovered) {
                    return icons[TOPLEFT];
                }
                if (westCovered) {
                    return icons[TOPRIGHT];
                }
            }
        }

        if (side == 4 || side == 5) {
            if (southCovered) {
                return icons[LEFTSIDE];
            }
            if (northCovered) {
                return icons[RIGHTSIDE];
            }
        }
        if (side == 2 || side == 3) {
            if (eastCovered) {
                return icons[RIGHTSIDE];
            }
            if (westCovered) {
                return icons[LEFTSIDE];
            }
        }
        return icons[PLAIN];
    }
}
