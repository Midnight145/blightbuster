package talonos.blightbuster.blocks;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.DawnChargerTileEntity;

public class DawnCharger extends BBBlock {
    public DawnCharger() {
        super(Material.wood);
        this.setHardness(5.0f);
        this.setBlockName(BlightBuster.MODID + "_" + BBStrings.dawnChargerName);
        this.setStepSound(soundTypeMetal);
        this.setBlockTextureName("blightbuster:dawnCharger");
        GameRegistry.registerBlock(this, BBStrings.dawnChargerName);
    }
    
    @Override
    public boolean hasTileEntity(int meta)
    {
        return true;
    }
 
    @Override
    public TileEntity createTileEntity(World world, int meta)
    {
        return new DawnChargerTileEntity();
    }
}
