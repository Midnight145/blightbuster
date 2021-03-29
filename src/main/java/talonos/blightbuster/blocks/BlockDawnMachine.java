package talonos.blightbuster.blocks;

import java.util.Random;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.multiblock.BlockMultiblock;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.Thaumcraft;

public class BlockDawnMachine extends BlockMultiblock {

    private IIcon top;
    private IIcon bottom;
    private IIcon sideLive;
    private IIcon sideDead;
    private IIcon glow;

    public BlockDawnMachine() {
        super(Material.wood, BBBlock.dawnMachineMultiblock);
        this.setLightLevel(.875f);
        this.setHardness(10.0F);
        this.setResistance(500.0F);
        this.setBlockName(BlightBuster.MODID + "_" + BBStrings.dawnMachineName);
        this.setStepSound(soundTypeWood);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
    }

    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
        if (par5 == 1) {
            if (par1World.isRemote) {
                Thaumcraft.proxy.blockSparkle(par1World, par2, par3, par4, 16736256, 5);
            }
            return true;
        }
        return super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister registry) {
        top = registry.registerIcon("blightbuster:dawnMachineTop");
        sideLive = registry.registerIcon("blightbuster:dawnMachineSide");
        sideDead = registry.registerIcon("blightbuster:dawnMachineDeactivated");
        bottom = registry.registerIcon("blightbuster:dawnMachineBottom");
        glow = registry.registerIcon("blightbuster:dawnMachineGlow");
    }

    @SideOnly(Side.CLIENT)
    @Override
    public int getRenderType() { return -1; }

    public IIcon getTopIcon() { return top; }
    public IIcon getBottomIcon() { return bottom; }
    public IIcon getLiveSideIcon() { return sideLive; }
    public IIcon getDeadSideIcon() { return sideDead; }
    public IIcon getGlowIcon() { return glow; }

    @Override
    public Item getItemDropped(int meta, Random par2Random, int par3) {
        return Item.getItemFromBlock(BBBlock.cyberTotem);
    }

    @Override
    public boolean hasTileEntity(int meta) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, int meta) {
        return new DawnMachineTileEntity();
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world, x, y, z, block, meta);

        BlightBuster.instance.chunkLoader.eliminateDawnMachine(world, x, y, z);
    }
}
