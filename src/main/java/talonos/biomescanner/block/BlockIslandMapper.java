package talonos.biomescanner.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BSStrings;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.tileentity.TileEntityIslandMapper;

public class BlockIslandMapper extends BSBlock implements ITileEntityProvider {

    public BlockIslandMapper() {
        this.setBlockName(BiomeScanner.MODID + "_" + BSStrings.blockIslandMapperName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setLightLevel(1.0f);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.blockIcon = par1IconRegister.registerIcon(BiomeScanner.MODID + ":" + BSStrings.bedrockBrickName);
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or
     * not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone
     * wire, etc to this block.
     */
    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int iDunno) {
        return new TileEntityIslandMapper();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        if (world.getTileEntity(x, y, z) != null) MapScanner.instance.bus()
            .unregister(world.getTileEntity(x, y, z));
    }
}
