package talonos.blightbuster.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MovingObjectPosition;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.lib.INodeIsolated;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

public class ItemIsolationFocus extends ItemFocusBasic {

    public ItemIsolationFocus() {
        setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.isolationFocusName);
        GameRegistry.registerItem(this, BlightBuster.MODID + ":" + BBStrings.isolationFocusName);
        setCreativeTab(CreativeTabs.tabMaterials);
        setTextureName(BlightBuster.MODID + ":" + BBStrings.isolationFocusName);
    }

    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if ((stack.getItem() instanceof ItemWandCasting wand && entity instanceof EntityPlayer player)) {
            MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, player, true);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (!(player.worldObj
                    .getTileEntity(mop.blockX, mop.blockY, mop.blockZ) instanceof INodeIsolated node)) {
                    return super.onEntitySwing(player, stack);
                }
                if (!wand.consumeAllVis(stack, player, this.getVisCost(stack), true, false)) {
                    return super.onEntitySwing(player, stack);
                }
                node.setIsolated(!node.isIsolated());
            }
        }
        return super.onEntitySwing(entity, stack);
    }

    @Override
    public int getActivationCooldown(ItemStack focusstack) {
        return 500;
    }

    @Override
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return player.getEntityWorld()
            .getTileEntity(x, y, z) instanceof INodeIsolated && !player.isSneaking();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
        this.icon = register.registerIcon(this.getIconString());
    }

    @Override
    public int getFocusColor(ItemStack arg0) {
        return 0x5a00cf;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {

        return new FocusUpgradeType[0];
    }

    @Override
    public AspectList getVisCost(ItemStack itemstack) {
        AspectList cost = new AspectList();
        cost.add(Aspect.ORDER, 20);
        return cost;
    }
}
