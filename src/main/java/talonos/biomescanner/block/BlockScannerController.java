package talonos.biomescanner.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.biomescanner.BSStrings;
import talonos.biomescanner.BiomeScanner;
import talonos.biomescanner.map.MapScanner;
import talonos.biomescanner.tileentity.TileEntityIslandMapper;
import talonos.biomescanner.tileentity.TileEntityIslandScanner;

public class BlockScannerController extends BSBlock implements ITileEntityProvider {

    private IIcon back;
    private IIcon front;
    private IIcon top;
    private IIcon bottom;
    private IIcon sides;

    public BlockScannerController() {
        this.setBlockName(BiomeScanner.MODID + "_" + BSStrings.blockScannerControllerName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setCreativeTab(CreativeTabs.tabBlock);
        this.setLightLevel(0.16f);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister) {
        this.back = par1IconRegister.registerIcon("biomescanner:controller-back");
        this.front = par1IconRegister.registerIcon("biomescanner:controller-front");
        this.sides = par1IconRegister.registerIcon("biomescanner:controller-sides");
        this.bottom = par1IconRegister.registerIcon("biomescanner:controller-bottom");
        this.top = par1IconRegister.registerIcon("biomescanner:controller-top");
        this.blockIcon = this.top;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
        if (side == 0) {
            return this.bottom;
        } else if (side == 1) {
            return this.top;
        } else if (side == 2) {
            return this.front;
        } else if (side == 3) {
            return this.back;
        } else {
            return this.sides;
        }
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
        return new TileEntityIslandScanner();
    }

    @Override
    public int onBlockPlaced(World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ,
        int metadata) {
        MapScanner.instance.chunkX = Math.floorDiv(x, 16);
        MapScanner.instance.chunkZ = Math.floorDiv(z, 16);
        MapScanner.instance.world = world;
        for (int iterX = 0; iterX <= 4; iterX++) {
            for (int iterY = 0; iterY <= 6; iterY++) {
                TileEntity hopefullyAMap = world.getTileEntity(x + iterX - 2, y + iterY, z + 7);

                if (hopefullyAMap instanceof TileEntityIslandMapper) {
                    TileEntityIslandMapper map = (TileEntityIslandMapper) hopefullyAMap;

                    map.setMapCoords(iterX * MapScanner.blockWidth, (5 - iterY) * MapScanner.blockHeight);

                    world.markBlockForUpdate(map.xCoord, map.yCoord, map.zCoord);
                }
            }
        }

        return metadata;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
        float hitY, float hitZ) {
        if (ForgeDirection.VALID_DIRECTIONS[side] == ForgeDirection.UP) {
            if (!world.isRemote) {
                if (MapScanner.instance.isActive()) {
                    player.addChatComponentMessage(new ChatComponentTranslation("scanner.gui.alreadyactive"));
                } else {
                    MapScanner.instance.activate();
                    TileEntityIslandScanner scanner = ((TileEntityIslandScanner) world.getTileEntity(x, y, z));
                    if (scanner != null) {
                        scanner.scanOrdered();
                    }
                }
            }
            return true;
        } else {
            if (world.getTileEntity(x, y, z) != null && !player.isSneaking()) {
                player.openGui(BiomeScanner.instance, 0, world, x, y, z);
                return true;
            }
        }

        return false;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int side) {
        if (world.getTileEntity(x, y, z) != null) {
            ((TileEntityIslandScanner) world.getTileEntity(x, y, z)).unregister();
        }
    }
}
