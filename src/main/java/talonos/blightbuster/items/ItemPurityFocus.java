package talonos.blightbuster.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.lib.CleansingHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileNode;

public class ItemPurityFocus extends ItemFocusBasic {

    public ItemPurityFocus() {
        this.setUnlocalizedName(BlightBuster.MODID + "_" + BBStrings.purityFocusName);
        GameRegistry.registerItem(this, BBStrings.purityFocusName);
        this.setCreativeTab(Thaumcraft.tabTC);
        this.setTextureName(BlightBuster.MODID + ":" + BBStrings.purityFocusName);
    }

    @Override
    public String getSortingHelper(ItemStack itemstack) {
        return "PU" + super.getSortingHelper(itemstack);
    }

    private static final AspectList cost = new AspectList().add(Aspect.EARTH, 10)
        .add(Aspect.ORDER, 15);
    private static final AspectList auraCost = new AspectList().add(Aspect.EARTH, 10000)
        .add(Aspect.ORDER, 15000);

    public boolean isVisCostPerTick() {
        return false;
    }

    @Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        int potency = wand.getFocusEnlarge(itemstack) + 1;

        if (!wand.consumeAllVis(itemstack, p, this.getBigVisCost(potency), false, false)) {
            return itemstack;
        }

        Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.0D, 32.0D, 32.0F);
        if (pointedEntity != null && !world.isRemote) {
            CleansingHelper.cleanseMobFromMapping(pointedEntity, pointedEntity.worldObj);
        }

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (int xOffset = -potency; xOffset < 1 + potency; xOffset++) {
                for (int zOffset = -potency; zOffset < 1 + potency; zOffset++) {
                    this.cleanUpLand(mop.blockX + xOffset, mop.blockZ + zOffset, world, p);
                }
            }
        }
        return itemstack;
    }

    @Override
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        TileEntity tile = player.getEntityWorld()
            .getTileEntity(x, y, z);
        if (tile instanceof TileNode node) {
            if (node.getNodeType() == NodeType.TAINTED
                && wand.consumeAllVis(itemstack, player, this.getNodeVisCost(itemstack), true, false)) {
                node.setNodeType(NodeType.NORMAL);
                node.markDirty();
                player.getEntityWorld()
                    .markBlockForUpdate(x, y, z);
            }
            return true;
        }
        return false;
    }

    private AspectList getBigVisCost(int potency) {
        return new AspectList().add(Aspect.EARTH, 10 * (potency * 2 + 1) * (potency * 2 + 1))
            .add(Aspect.ORDER, 15 * (potency * 2 + 1) * (potency * 2 + 1));
    }

    private void cleanUpLand(int x, int z, World world, EntityPlayer p) {
        if (!p.worldObj.isRemote) {
            for (int y = 0; y < 256; y++) {
                CleansingHelper.cleanBlock(x, y, z, world);
            }
            CleansingHelper.cleanseBiome(x, z, world);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon(this.getIconString());
        this.icon = register.registerIcon(this.getIconString());
    }

    @Override
    public int getFocusColor(ItemStack arg0) {
        return 0x32f8c5;
    }

    @Override
    public FocusUpgradeType[] getPossibleUpgradesByRank(ItemStack itemstack, int rank) {
        switch (rank) {
            case 1, 2, 3, 4, 5:
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
        }
        return null;
    }

    @Override
    public AspectList getVisCost(ItemStack arg0) {
        return cost;
    }

    public AspectList getNodeVisCost(ItemStack arg0) {
        return auraCost;
    }
}
