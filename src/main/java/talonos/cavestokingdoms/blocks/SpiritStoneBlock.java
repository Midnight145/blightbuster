package talonos.cavestokingdoms.blocks;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.cavestokingdoms.extendedproperties.PlayerOrbsGotten;
import talonos.cavestokingdoms.lib.DEFS;

public class SpiritStoneBlock extends CtKBlock {

    public SpiritStoneBlock() {
        this.setBlockName(DEFS.MODID + "_" + DEFS.SpiritStoneBlockName);
        this.setBlockUnbreakable();
        this.setResistance(6000000.0F);
        this.setStepSound(soundTypePiston);
        this.disableStats();
        this.setBlockTextureName("bedrock");
        this.setCreativeTab(CreativeTabs.tabBlock);
        GameRegistry.registerBlock(this, this.getUnlocalizedName());
        this.setLightLevel(0.75F);
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
        this.blockIcon = iconRegister.registerIcon(DEFS.MODID + ":" + DEFS.SpiritStoneBlockName);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int huh, float i, float dont,
        float even) {
        if (!world.isRemote) {
            if (PlayerOrbsGotten.get(player)
                .hasOrb()) {
                return false;
            }
            world.setBlock(x, y, z, Blocks.obsidian);
            PlayerOrbsGotten.get(player)
                .giveOrb();
        }
        return true;
    }
}
