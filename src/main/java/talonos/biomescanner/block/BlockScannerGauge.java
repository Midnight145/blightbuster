package talonos.biomescanner.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import talonos.biomescanner.BSStrings;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.tileentity.TileEntityGauge;

public class BlockScannerGauge extends BSBlock implements ITileEntityProvider {

    public BlockScannerGauge() {
        super(Material.iron);
        this.setBlockName(BiomeScanner.MODID + "_" + BSStrings.scannerGaugeBlockName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGauge();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (player.capabilities.isCreativeMode) {
            if (!world.isRemote) {
                TileEntityGauge gauge = (TileEntityGauge) world.getTileEntity(x, y, z);
                if (gauge != null) {
                    gauge.cycleZone();
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        if (world.getTileEntity(x, y, z) != null) ((TileEntityGauge) world.getTileEntity(x, y, z)).unregister();
    }

    /**
     * Is this block (a) opaque and (b) a full 1m cube? This determines whether or
     * not to render the shared face of two
     * adjacent blocks and also whether the player can attach torches, redstone
     * wire, etc to this block.
     */
    public boolean isOpaqueCube() {
        return false;
    }

    /**
     * If this block doesn't render as an ordinary block it will return False
     * (examples: signs, buttons, stairs, etc)
     */
    public boolean renderAsNormalBlock() {
        return false;
    }

    /**
     * The type of render function that is called for this block
     */
    public int getRenderType() {
        return 22;
    }
}
