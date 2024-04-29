package talonos.blightbuster.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.DawnTotemTileEntity;

public class BlockDawnTotem extends BBBlock {

    public BlockDawnTotem(Material m) {
        super(m);
        this.setBlockName(BlightBuster.MODID + "_" + BBStrings.dawnTotemBlockName);
        this.setStepSound(soundTypeWood);
        this.setBlockTextureName("dawnTotem");
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setLightLevel(.875f);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new DawnTotemTileEntity();
    }

    // Icon stuff:

    @SideOnly(Side.CLIENT)
    private static IIcon[] icons;

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        icons = new IIcon[2];
        for (int x = 0; x < 2; x++) {
            icons[x] = iconRegister.registerIcon(BlightBuster.MODID + ":" + BBStrings.dawnTotemBlockName + x);
        }
        this.blockIcon = icons[0];
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess world, int x, int y, int z, int side) {
        if (side == ForgeDirection.UP.ordinal() || side == ForgeDirection.DOWN.ordinal()) {
            return icons[1];
        }
        return icons[0];
    }
}
