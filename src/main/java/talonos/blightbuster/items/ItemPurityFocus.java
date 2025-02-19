package talonos.blightbuster.items;

import java.util.ArrayList;
import java.util.List;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraftforge.event.entity.living.LivingEvent;
import noppes.npcs.entity.EntityCustomNpc;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import talonos.blightbuster.lib.CleansingHelper;
import thaumcraft.api.BlockCoordinates;
import thaumcraft.api.IArchitect;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.entities.ITaintedMob;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.config.Config;
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileNode;
import thaumcraft.common.tiles.TileWarded;

public class ItemPurityFocus extends ItemFocusBasic implements IArchitect {

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

    private static final AspectList blockCost = new AspectList().add(Aspect.EARTH, 10)
        .add(Aspect.ORDER, 15);
    private static final AspectList nodeCost = new AspectList().add(Aspect.EARTH, 10000)
        .add(Aspect.ORDER, 15000);
    private static final AspectList attackCost = new AspectList().add(Aspect.FIRE, 500)
        .add(Aspect.ENTROPY, 500);
    private static final AspectList healCost = new AspectList().add(Aspect.EARTH, 200)
        .add(Aspect.WATER, 200)
        .add(Aspect.ORDER, 100);
    private static final AspectList vacuumCost = new AspectList().add(Aspect.AIR, 50)
        .add(Aspect.ENTROPY, 50);

    @Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();

        Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.0D, 32.0D, 32.0F);
        if (pointedEntity != null && !world.isRemote && pointedEntity instanceof EntityLivingBase e) {
            cleanEntity(itemstack, p, e, wand, wand.getFocusItem(itemstack));
        }
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (BlockCoordinates block : getArchitectBlocks(itemstack, p.worldObj, mop.blockX, mop.blockY, mop.blockZ, mop.sideHit, p)) {
                this.cleanUpLand(block.x, block.z, world, p, itemstack);
            }
            if (isUpgradedWith(wand.getFocusItem(itemstack), ItemPurityFocus.curative) || isUpgradedWith(wand.getFocusItem(itemstack), ItemPurityFocus.blightBuster)) {
                int sizeX = WandManager.getAreaX(itemstack);
                int sizeZ = WandManager.getAreaZ(itemstack);
                AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                    mop.blockX - sizeX,
                    0,
                    mop.blockZ - sizeZ,
                    mop.blockX + sizeX,
                    255,
                    mop.blockZ + sizeZ);
                List<EntityLivingBase> entities = new ArrayList<>(
                    p.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box));
                entities.remove(p);
                cleanEntities(itemstack, p, entities);
            }
        }
        return itemstack;
    }

    private void cleanEntities(ItemStack itemstack, EntityPlayer p, List<EntityLivingBase> entities) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        ItemStack focus = wand.getFocusItem(itemstack);
        for (EntityLivingBase entity : entities) {
            if (!cleanEntity(itemstack, p, entity, wand, focus)) {
                break;
            }
        }
    }

    private boolean cleanEntity(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand, ItemStack focus) {
        boolean curativeUpgrade = isUpgradedWith(focus, curative);
        boolean vacuumUpgrade = isUpgradedWith(focus, ItemPurityFocus.vacuum);
        boolean blightBusterUpgrade = isUpgradedWith(focus, blightBuster);
        if (!wand.consumeAllVis(itemstack, p, this.getHealVisCost(), false, false) && !(vacuumUpgrade && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false)) && !(blightBusterUpgrade && wand.consumeAllVis(itemstack, p, this.getAttackVisCost(), false, false))) {
            return false;
        }
        if (curativeUpgrade && entity.getHealth() < entity.getMaxHealth()
            && wand.consumeAllVis(itemstack, p, this.getHealVisCost(), false, false)
            && (entity.isCreatureType(EnumCreatureType.creature, false)
                || entity.isCreatureType(EnumCreatureType.ambient, false)
                || entity.isCreatureType(EnumCreatureType.waterCreature, false)
                || entity instanceof EntityPlayer && !entity.equals(p))) {
            entity.heal(4);
            wand.consumeAllVis(itemstack, p, this.getHealVisCost(), true, false);
        }
        if (vacuumUpgrade && entity instanceof EntityThaumicSlime && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false)) {
            entity.setDead();
            wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), true, false);
            return true;
        }
        if (!blightBusterUpgrade && CleansingHelper.cleanseMobFromMapping(entity, p.worldObj)) {
            wand.consumeAllVis(itemstack, p, this.getHealVisCost(), true, false);
            return true;
        } else if (blightBusterUpgrade && entity.hurtTime <= 0 && entity instanceof ITaintedMob || (BlightbusterConfig.customNpcSupport && entity instanceof EntityCustomNpc npc && npc.linkedName.toLowerCase().contains("taint"))) {
            entity.setLastAttacker(p);
            entity.attackEntityFrom(DamageSource.magic, 20);
            wand.consumeAllVis(itemstack, p, this.getAttackVisCost(), true, false);
        }
        return true;
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

    private void cleanUpLand(int x, int z, World world, EntityPlayer p, ItemStack itemstack) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        boolean cleanBlocks = true;
        boolean cleanFlux = this.isUpgradedWith(wand.getFocusItem(itemstack), vacuum) && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false);
        boolean cleanNodes = this.isUpgradedWith(wand.getFocusItem(itemstack), node) && wand.consumeAllVis(itemstack, p, this.getNodeVisCost(), false, false);
        if (!p.worldObj.isRemote) {
            for (int y = 0; y < 256; y++) {
                cleanBlocks = cleanBlocks && wand.consumeAllVis(itemstack, p, this.getBlockVisCost(), false, false);
                cleanFlux = cleanFlux && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false);
                cleanNodes = cleanNodes && wand.consumeAllVis(itemstack, p, this.getNodeVisCost(), false, false);
                if (!(cleanBlocks || cleanFlux || cleanNodes)) {
                    break;
                }
                if (cleanBlocks && CleansingHelper.cleanBlock(x, y, z, world)) {
                    wand.consumeAllVis(itemstack, p, this.getBlockVisCost(), true, false);
                }
                if (cleanFlux && CleansingHelper.cleanFlux(x, y, z, world)) {
                    wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), true, false);
                }
                if (cleanNodes) {
                    cleanNode(x, y, z, p, itemstack);
                }
            }
            if (wand.consumeAllVis(itemstack, p, this.getBlockVisCost(), false, false)
                && CleansingHelper.cleanseBiome(x, z, world)) {
                wand.consumeAllVis(itemstack, p, this.getBlockVisCost(), true, false);
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
        output.add(FocusUpgradeType.enlarge);
        output.add(FocusUpgradeType.frugal);
        if (rank == 2) {
            output.add(FocusUpgradeType.architect);
        } else if (rank == 3) {
            output.add(vacuum);
        } else if (rank == 5) {
            output.add(curative);
            output.add(blightBuster);
            output.add(node);
        }
        return output.toArray(new FocusUpgradeType[0]);
    }

    public int getMaxAreaSize(ItemStack focusstack) {
        return 1 + getUpgradeLevel(focusstack, FocusUpgradeType.enlarge);
    }

    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack itemstack, World world, int x, int y, int z, int side, EntityPlayer player) {
        ArrayList<BlockCoordinates> out = new ArrayList<>();
        int sizeX = WandManager.getAreaX(itemstack);
        int sizeZ = WandManager.getAreaZ(itemstack);
        for (int xOffset = -sizeX; xOffset < 1 + sizeX; xOffset++) {
            for (int zOffset = -sizeZ; zOffset < 1 + sizeZ; zOffset++) {
                out.add(new BlockCoordinates(x + xOffset, y, z + zOffset));
            }
        }
        return out;
    }

    public boolean showAxis(ItemStack stack, World world, EntityPlayer player, int side, IArchitect.EnumAxis axis) {
        int dim = WandManager.getAreaDim(stack);
        if (axis == IArchitect.EnumAxis.X && (dim == 0 || dim == 1)) {
            return true;
        } else if (axis == IArchitect.EnumAxis.Z && (dim == 0 || dim == 2)) {
            return true;
        }
        return false;
    }

    @Override
    public AspectList getVisCost(ItemStack itemstack) {
        AspectList cost = new AspectList();
        cost.add(blockCost);
        if (this.isUpgradedWith(itemstack, vacuum)) {
            cost.add(getVacuumVisCost());
        }
        if (this.isUpgradedWith(itemstack, blightBuster)) {
            cost.add(getAttackVisCost());
        }
        if (this.isUpgradedWith(itemstack, curative)) {
            cost.add(getHealVisCost());
        }
        return cost;
    }

    public AspectList getBlockVisCost() {
        return blockCost;
    }

    public AspectList getNodeVisCost() {
        return nodeCost;
    }

    public AspectList getAttackVisCost() {
        return attackCost;
    }

    public AspectList getHealVisCost() {
        return healCost;
    }

    public AspectList getVacuumVisCost() {
        return vacuumCost;
    }

    public static FocusUpgradeType vacuum;
    public static FocusUpgradeType node;
    public static FocusUpgradeType curative;
    public static FocusUpgradeType blightBuster;

    static {
        ItemPurityFocus.vacuum = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            new ResourceLocation("blightbuster", "textures/foci/fluxVacuum.png"),
            "focus.upgrade.bb_vacuum.name",
            "focus.upgrade.bb_vacuum.text",
            new AspectList().add(Aspect.VOID, 3));
        ItemPurityFocus.node = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            new ResourceLocation("blightbuster", "textures/foci/nodePurifier.png"),
            "focus.upgrade.bb_node.name",
            "focus.upgrade.bb_node.text",
            new AspectList().add(Aspect.AURA, 4));
        ItemPurityFocus.curative = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            new ResourceLocation("blightbuster", "textures/foci/curative.png"),
            "focus.upgrade.bb_curative.name",
            "focus.upgrade.bb_curative.text",
            new AspectList().add(Aspect.HEAL, 4));
        ItemPurityFocus.blightBuster = new FocusUpgradeType(
            FocusUpgradeType.types.length,
            new ResourceLocation("blightbuster", "textures/foci/blightBuster.png"),
            "focus.upgrade.bb_blightBuster.name",
            "focus.upgrade.bb_blightBuster.text",
            new AspectList().add(Aspect.WEAPON, 1));
    }
}
