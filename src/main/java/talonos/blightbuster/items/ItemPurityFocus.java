package talonos.blightbuster.items;

import static thaumcraft.api.wands.FocusUpgradeType.architect;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.FakePlayer;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
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
import thaumcraft.common.entities.monster.EntityThaumicSlime;
import thaumcraft.common.items.wands.ItemWandCasting;
import thaumcraft.common.items.wands.WandManager;
import thaumcraft.common.lib.utils.EntityUtils;
import thaumcraft.common.tiles.TileNode;

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

    public static final AspectList blockCost = new AspectList();
    public static final AspectList nodeCost = new AspectList();
    public static final AspectList attackCost = new AspectList();
    public static final AspectList healCost = new AspectList();
    public static final AspectList vacuumCost = new AspectList();

    @Override
    public ItemStack onFocusRightClick(ItemStack itemstack, World world, EntityPlayer p, MovingObjectPosition mop) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();

        Entity pointedEntity = EntityUtils.getPointedEntity(p.worldObj, p, 0.0D, 32.0D, 1F);
        boolean cleaned = false;
        ItemStack focus = wand.getFocusItem(itemstack);
        if (pointedEntity instanceof EntityLivingBase e) {
            cleaned = cleanEntity(itemstack, p, e, wand, focus);
        }
        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            if (this.isUpgradedWith(focus, architect)) {
                for (BlockCoordinates block : getArchitectBlocks(
                    itemstack,
                    p.worldObj,
                    mop.blockX,
                    mop.blockY,
                    mop.blockZ,
                    mop.sideHit,
                    p)) {
                    cleanUpLand(block.x, block.z, world, p, itemstack);
                }
            } else {
                int range = 1 + wand.getFocusEnlarge(itemstack);
                for (int x = mop.blockX - range; x < 1 + mop.blockX + range; x++) {
                    for (int z = mop.blockZ - range; z < 1 + mop.blockZ + range; z++) {
                        cleanUpLand(x, z, world, p, itemstack);
                    }
                }
            }
            if (!cleaned && (isUpgradedWith(focus, curative) || isUpgradedWith(focus, blightBuster))) {
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
        boolean vacuumUpgrade = isUpgradedWith(focus, vacuum);
        boolean blightBusterUpgrade = isUpgradedWith(focus, blightBuster);
        for (EntityLivingBase entity : entities) {
            if (!(wand.consumeAllVis(itemstack, p, this.getHealVisCost(), false, false)
                || vacuumUpgrade && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false)
                || (blightBusterUpgrade && wand.consumeAllVis(itemstack, p, this.getAttackVisCost(), false, false)))) {
                break;
            }
            cleanEntity(itemstack, p, entity, wand, focus);
        }
    }

    private boolean cleanEntity(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand,
        ItemStack focus) {
        if (doVacuum(itemstack, p, entity, wand, focus)) {
            if (p.worldObj.isRemote) {} else {
                entity.setDead();
                wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), true, false);
            }
            return true;
        } else if (doBlightBuster(itemstack, p, entity, wand, focus)) {
            if (p.worldObj.isRemote) {
                PurityFocusFX.fire(entity);
            } else {
                entity.attackEntityFrom(
                    new EntityDamageSource("magic", p).setDamageBypassesArmor(),
                    BlightbusterConfig.attackStrength);
                wand.consumeAllVis(itemstack, p, this.getAttackVisCost(), true, false);
            }
            return true;
        } else if (doBasicClean(itemstack, p, entity, wand, focus)) {
            if (p.worldObj.isRemote) {
                PurityFocusFX.clean(entity);
            } else {
                wand.consumeAllVis(itemstack, p, this.getHealVisCost(), true, false);
            }
            return true;
        } else if (doCurative(itemstack, p, entity, wand, focus)) {
            if (p.worldObj.isRemote) {
                PurityFocusFX.heal(entity);
            } else {
                entity.heal(BlightbusterConfig.healStrength);
                wand.consumeAllVis(itemstack, p, this.getHealVisCost(), true, false);
            }
            return true;
        }
        return false;
    }

    private boolean doVacuum(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand,
        ItemStack focus) {
        return isUpgradedWith(focus, vacuum) && entity instanceof EntityThaumicSlime
            && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false);
    }

    private boolean doBlightBuster(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand,
        ItemStack focus) {
        return isUpgradedWith(focus, blightBuster) && entity.hurtTime <= 0
            && entity.getHealth() > 0
            && (entity instanceof ITaintedMob
                || (BlightbusterConfig.customNpcSupport && entity instanceof EntityCustomNpc npc
                    && npc.linkedName.toLowerCase()
                        .contains("taint")))
            && wand.consumeAllVis(itemstack, p, this.getAttackVisCost(), false, false);
    }

    private boolean doBasicClean(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand,
        ItemStack focus) {
        return !isUpgradedWith(focus, blightBuster)
            && wand.consumeAllVis(itemstack, p, this.getHealVisCost(), false, false)
            && CleansingHelper.cleanseMobFromMapping(entity, p.worldObj);
    }

    private boolean doCurative(ItemStack itemstack, EntityPlayer p, EntityLivingBase entity, ItemWandCasting wand,
        ItemStack focus) {
        return isUpgradedWith(focus, curative) && entity.getHealth() < entity.getMaxHealth()
            && wand.consumeAllVis(itemstack, p, this.getHealVisCost(), false, false)
            && (!entity.isCreatureType(EnumCreatureType.monster, false)
                || entity instanceof EntityPlayer && !entity.equals(p))
            && !(p instanceof FakePlayer);
    }

    /**
     * Cancel all block breaking while holding a wand equipped with this focus to allow for node and
     * single-column cleaning.
     */
    @Override
    public boolean onFocusBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player) {
        return true;
    }

    private void cleanNode(int x, int y, int z, EntityPlayer player, ItemStack itemstack) {
        TileEntity tile = player.getEntityWorld()
            .getTileEntity(x, y, z);
        if (tile instanceof TileNode node) {
            ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
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
                PurityFocusFX.node(player, x, y, z);
            }
        }
    }

    private void cleanUpLand(int x, int z, World world, EntityPlayer p, ItemStack itemstack) {
        ItemWandCasting wand = (ItemWandCasting) itemstack.getItem();
        boolean blockCost = false;
        boolean cleanBlocks = wand.consumeAllVis(itemstack, p, this.getBlockVisCost(), false, false);
        boolean cleanFlux = this.isUpgradedWith(wand.getFocusItem(itemstack), vacuum)
            && wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false);
        boolean cleanNodes = this.isUpgradedWith(wand.getFocusItem(itemstack), node)
            && wand.consumeAllVis(itemstack, p, this.getNodeVisCost(), false, false);
        if (!p.worldObj.isRemote) {
            for (int y = 0; y < 256; y++) {
                if (cleanBlocks && CleansingHelper.cleanBlock(x, y, z, world)) {
                    blockCost = true;
                }
                if (cleanFlux && CleansingHelper.cleanFlux(x, y, z, world)) {
                    wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), true, false);
                    cleanFlux = wand.consumeAllVis(itemstack, p, this.getVacuumVisCost(), false, false);
                }
                if (cleanNodes) {
                    cleanNode(x, y, z, p, itemstack);
                    cleanNodes = wand.consumeAllVis(itemstack, p, this.getNodeVisCost(), false, false);
                }
            }
            if ((cleanBlocks && CleansingHelper.cleanseBiome(x, z, world)) || blockCost) {
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
        if (rank == 1) {
            output.add(architect);
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
        return 2 + getUpgradeLevel(focusstack, FocusUpgradeType.enlarge);
    }

    public boolean onEntitySwing(EntityLivingBase player, ItemStack stack) {
        if (!(stack.getItem() instanceof ItemWandCasting)) {
            return super.onEntitySwing(player, stack);
        }
        if (!player.worldObj.isRemote && player instanceof EntityPlayer p) {
            MovingObjectPosition mop = this.getMovingObjectPositionFromPlayer(player.worldObj, p, true);
            if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                cleanNode(mop.blockX, mop.blockY, mop.blockZ, p, stack);
                cleanUpLand(mop.blockX, mop.blockZ, player.worldObj, p, stack);
            }
        }
        return super.onEntitySwing(player, stack);
    }

    public ArrayList<BlockCoordinates> getArchitectBlocks(ItemStack itemstack, World world, int x, int y, int z,
        int side, EntityPlayer player) {
        ArrayList<BlockCoordinates> out = new ArrayList<>();
        int sizeX = WandManager.getAreaX(itemstack);
        int sizeZ = WandManager.getAreaZ(itemstack);
        int yy;
        for (int xx = x - sizeX; xx < 1 + x + sizeX; xx++) {
            for (int zz = z - sizeZ; zz < 1 + z + sizeZ; zz++) {
                yy = y;
                if (!world.getBlock(xx, yy, zz)
                    .isOpaqueCube()) {
                    do {
                        yy--;
                    } while (!world.getBlock(xx, yy, zz)
                        .isOpaqueCube() && yy > 0);
                } else if (world.getBlock(xx, yy + 1, zz)
                    .isOpaqueCube()) {
                        do {
                            yy++;
                        } while (world.getBlock(xx, yy + 1, zz)
                            .isOpaqueCube() && yy < 255);
                    }
                out.add(new BlockCoordinates(xx, yy, zz));
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
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean par4) {
        AspectList al = this.getVisCost(stack);
        if (al != null && al.size() > 0) {
            list.add(StatCollector.translateToLocal("item.blightbuster_purityFocus.cost1"));
            addVis(list, getBlockVisCost());
            list.add(
                StatCollector.translateToLocal(
                    this.isUpgradedWith(stack, curative) ? "item.blightbuster_purityFocus.cost3"
                        : "item.blightbuster_purityFocus.cost2"));
            addVis(list, getHealVisCost());
            list.add(StatCollector.translateToLocal("item.blightbuster_purityFocus.cost4"));
            addVis(list, getNodeVisCost());
            if (this.isUpgradedWith(stack, vacuum)) {
                list.add(StatCollector.translateToLocal("item.blightbuster_purityFocus.cost5"));
                addVis(list, getVacuumVisCost());
            }
            if (this.isUpgradedWith(stack, blightBuster)) {
                list.add(StatCollector.translateToLocal("item.blightbuster_purityFocus.cost6"));
                addVis(list, getAttackVisCost());
            }
        }
        this.addFocusInformation(stack, player, list, par4);
    }

    private static void addVis(List list, AspectList al) {
        for (Aspect aspect : al.getAspectsSorted()) {
            DecimalFormat formatter = new DecimalFormat("#####.##");
            String amount = formatter.format(al.getAmount(aspect) / 100.0F);
            list.add(" §" + aspect.getChatcolor() + aspect.getName() + "§r x " + amount);
        }
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
