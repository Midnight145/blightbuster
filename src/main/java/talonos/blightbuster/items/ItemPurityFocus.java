package talonos.blightbuster.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import noppes.npcs.entity.EntityCustomNpc;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import talonos.blightbuster.lib.CleansingHelper;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.utils.BlockUtils;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileNode;

public class ItemPurityFocus extends ItemFocusBasic {

    static HashMap<String, Object> beam = new HashMap();
    static HashMap<String, Integer> lastX = new HashMap();
    static HashMap<String, Integer> lastY = new HashMap();
    static HashMap<String, Integer> lastZ = new HashMap();
    boolean healedMob = false;

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
    private static final AspectList nodeCost = new AspectList().add(Aspect.EARTH, 10000)
        .add(Aspect.ORDER, 15000);

    @Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        if (!wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), false, false)) {
            return itemstack;
        }
        if (isUpgradedWith(wand.getFocusItem(itemstack), ItemPurityFocus.beamUpgrade)) {
            p.setItemInUse(itemstack, Integer.MAX_VALUE);
            return itemstack;
        }
        int radius = wand.getFocusEnlarge(itemstack) + 1;

        Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.0D, 32.0D, 32.0F);
        if (pointedEntity != null && !world.isRemote
            && CleansingHelper.cleanseMobFromMapping(pointedEntity, pointedEntity.worldObj)) {
            wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
        }
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (int xOffset = -radius; xOffset < 1 + radius; xOffset++) {
                for (int zOffset = -radius; zOffset < 1 + radius; zOffset++) {
                    this.cleanUpLand(mop.blockX + xOffset, mop.blockZ + zOffset, world, p, itemstack);
                }
            }
            if (isUpgradedWith(wand.getFocusItem(itemstack), ItemPurityFocus.curative)) {
                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                    mop.blockX - radius,
                    0,
                    mop.blockZ - radius,
                    mop.blockX + radius,
                    5,
                    mop.blockZ + radius);
                List<EntityLivingBase> entities = new ArrayList<>(
                    p.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box));
                entities.remove(p);
                cureEntities(itemstack, p, entities);
            }
        }
        return itemstack;
    }

    private void cureEntities(ItemStack itemstack, EntityPlayer p, List<EntityLivingBase> entities) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        ItemStack focus = wand.getFocusItem(itemstack);
        for (EntityLivingBase entity : entities) {
            if (!wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), false, false)) {
                break;
            }
            if (isUpgradedWith(focus, curative) && entity.getHealth() < entity.getMaxHealth()
                && (entity.isCreatureType(EnumCreatureType.creature, false)
                    || entity.isCreatureType(EnumCreatureType.ambient, false)
                    || entity.isCreatureType(EnumCreatureType.waterCreature, false)
                    || entity instanceof EntityPlayer && !entity.equals(p))) {
                entity.heal(1 + (float) wand.getFocusPotency(itemstack) / 2);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
                WandManager.setCooldown(p, 5);
            }
            if (isUpgradedWith(focus, ItemPurityFocus.vacuum) && entity instanceof EntityThaumicSlime) {
                entity.setDead();
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
                return;
            }
            if (!isUpgradedWith(focus, blightBuster) && CleansingHelper.cleanseMobFromMapping(entity, p.worldObj)) {
                return;
            } else if (entity instanceof ITaintedMob || (BlightbusterConfig.customNpcSupport && entity instanceof EntityCustomNpc npc && npc.linkedName.toLowerCase().contains("taint"))) {
                entity.setLastAttacker(p);
                entity.attackEntityFrom(DamageSource.magic, 2 * wand.getFocusPotency(itemstack) + 20 * getUpgradeLevel(focus, blightBuster));
            }
        }
    }

    @Override
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return cleanNode(x, y, z, player, itemstack);
    }

    private boolean cleanNode(int x, int y, int z, EntityPlayer player, ItemStack itemstack) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        TileEntity tile = player.getEntityWorld()
            .getTileEntity(x, y, z);
        if (tile instanceof TileNode node) {
            boolean hasNodeUpgrade = isUpgradedWith(wand.getFocusItem(itemstack), ItemPurityFocus.node);
            if (node.getNodeType() == NodeType.TAINTED
                && wand.consumeAllVis(itemstack, player, getNodeVisCost(), true, false)) {
                if (hasNodeUpgrade) {
                    node.setNodeType(NodeType.PURE);
                } else {
                    node.setNodeType(NodeType.NORMAL);
                }
                node.markDirty();
                player.getEntityWorld()
                    .markBlockForUpdate(x, y, z);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onUsingFocusTick(ItemStack stack, EntityPlayer p, int count) {
        ItemWandCasting wand = (ItemWandCasting) stack.getItem();
        if (!wand.consumeAllVis(stack, p, this.getVisCost(stack), false, false)) {
            p.stopUsingItem();
            return;
        }
        String pp = "R" + p.getCommandSenderName();
        if (!p.worldObj.isRemote) {
            pp = "S" + p.getCommandSenderName();
        }

        lastX.computeIfAbsent(pp, k -> 0);
        lastY.computeIfAbsent(pp, k -> 0);
        lastZ.computeIfAbsent(pp, k -> 0);
        double range = 16.0D + 2 * wand.getFocusEnlarge(stack);
        Vec3 v = p.getLookVec();

        double tx = p.posX + v.xCoord * range;
        double ty = p.posY + v.yCoord * range;
        double tz = p.posZ + v.zCoord * range;
        int impact = 0;
        boolean spectralUpgrade = this.isUpgradedWith(wand.getFocusItem(stack), spectral);
        if (!spectralUpgrade) {
            Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0, range, 0);
            if (pointedEntity != null) {
                tx = pointedEntity.posX;
                ty = pointedEntity.posY + (double) (pointedEntity.height / 2.0F);
                tz = pointedEntity.posZ;
                impact = 5;
                CleansingHelper.cleanseMobFromMapping(pointedEntity, p.worldObj);
            } else {
                MovingObjectPosition mop = BlockUtils.getTargetBlock(p.worldObj, p, false);
                if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    tx = mop.hitVec.xCoord;
                    ty = mop.hitVec.yCoord;
                    tz = mop.hitVec.zCoord;
                    impact = 5;
                }
            }
        }

        if (p.worldObj.isRemote) {
            beam.put(
                pp,
                Thaumcraft.proxy.beamCont(
                    p.worldObj,
                    p,
                    tx,
                    ty,
                    tz,
                    spectralUpgrade ? 4 : 3,
                    getFocusColor(stack),
                    false,
                    impact > 0 ? 2.0F : 0.0F,
                    beam.get(pp),
                    impact));
        }

        cleanBeam(stack, p, Math.round(tx), Math.round(ty), Math.round(tz), spectralUpgrade);
    }

    private void cleanBeam(ItemStack stack, EntityPlayer p, long tx, long ty, long tz, boolean spectralUpgrade) {
        long px = Math.round(p.posX);
        long py = Math.round(p.posY);
        long pz = Math.round(p.posZ);
        for (long x = px; x < tx; x++) {
            for (long y = py; y < ty; y++) {
                for (long z = pz; z < tz; z++) {
                    //
                }
            }
        }
    }

    private void cleanUpLand(int x, int z, World world, EntityPlayer p, ItemStack itemstack) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        boolean cleanFlux = this.isUpgradedWith(wand.getFocusItem(itemstack), vacuum);
        boolean cleanAllNodes = this.isUpgradedWith(wand.getFocusItem(itemstack), vacuum);
        if (!p.worldObj.isRemote) {
            for (int y = 0; y < 256; y++) {
                if (!wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), false, false)) {
                    break;
                }
                if (CleansingHelper.cleanBlock(x, y, z, world)) {
                    wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
                }
                if (cleanFlux && CleansingHelper.cleanFlux(x, y, z, world)) {
                    wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
                }
                if (cleanAllNodes) {
                    cleanNode(x, y, z, p, itemstack);
                }
            }
            if (wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), false, false)
                && CleansingHelper.cleanseBiome(x, z, world)) {
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }
        }
    }

    public ItemFocusBasic.WandFocusAnimation getAnimation(ItemStack itemstack) {
        return WandFocusAnimation.CHARGE;
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
        ArrayList<FocusUpgradeType> output = new ArrayList<>();
        if (this.isUpgradedWith(itemstack, beamUpgrade)) {
            output.add(FocusUpgradeType.potency);
            if (rank == 5) {
                output.add(spectral);
            }
        } else {
            if (rank == 5) {
                output.add(node);
            }
        }
        output.add(FocusUpgradeType.enlarge);
        output.add(FocusUpgradeType.frugal);
        if (rank == 1) {
            output.add(beamUpgrade);
        } else if (rank == 3) {
            output.add(vacuum);
        } else if (rank == 5) {
            output.add(curative);
            output.add(blightBuster);
        }
        return output.toArray(new FocusUpgradeType[0]);
    }

    @Override
    public AspectList getVisCost(ItemStack arg0) {
        return cost;
    }

    public AspectList getNodeVisCost() {
        return nodeCost;
    }

    private static FocusUpgradeType beamUpgrade;
    private static FocusUpgradeType vacuum;
    private static FocusUpgradeType node;
    private static FocusUpgradeType curative;
    private static FocusUpgradeType blightBuster;
    private static FocusUpgradeType spectral;

    static {
        ItemPurityFocus.beamUpgrade = new FocusUpgradeType( // TODO: ADD FUNCTIONALITY
            FocusUpgradeType.types.length,
            Aspect.LIGHT.getImage(),
            "focus.upgrade.bb_beam.name",
            "focus.upgrade.bb_beam.text",
            new AspectList().add(Aspect.LIGHT, 1));
        ItemPurityFocus.vacuum = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            Aspect.VOID.getImage(),
            "focus.upgrade.bb_vacuum.name",
            "focus.upgrade.bb_vacuum.text",
            new AspectList().add(Aspect.VOID, 2));
        ItemPurityFocus.node = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            Aspect.AURA.getImage(),
            "focus.upgrade.bb_node.name",
            "focus.upgrade.bb_node.text",
            new AspectList().add(Aspect.AURA, 4));
        ItemPurityFocus.curative = new FocusUpgradeType( // TODO: ADD FUNCTIONALITY
            FocusUpgradeType.types.length,
            Aspect.HEAL.getImage(),
            "focus.upgrade.bb_massHeal.name",
            "focus.upgrade.bb_massHeal.text",
            new AspectList().add(Aspect.HEAL, 4));
        ItemPurityFocus.blightBuster = new FocusUpgradeType( // TODO: ADD FUNCTIONALITY
            FocusUpgradeType.types.length,
            Aspect.WEAPON.getImage(),
            "focus.upgrade.bb_blightBuster.name",
            "focus.upgrade.bb_blightBuster.text",
            new AspectList().add(Aspect.WEAPON, 1));
        ItemPurityFocus.spectral = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            Aspect.SOUL.getImage(),
            "focus.upgrade.bb_spectral.name",
            "focus.upgrade.bb_spectral.text",
            new AspectList().add(Aspect.SOUL, 2));
    }
}
