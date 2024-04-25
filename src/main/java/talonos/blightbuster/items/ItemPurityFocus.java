package talonos.blightbuster.items;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.BBStrings;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.api.aspects.Aspect;
import thaumcraft.api.aspects.AspectList;
import thaumcraft.api.nodes.NodeType;
import thaumcraft.api.wands.FocusUpgradeType;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.Thaumcraft;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintVillager;
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
            // if target is a tainted sheep, convert to sheep
            if (pointedEntity instanceof EntityTaintSheep) {
                EntityTaintSheep entityTaintSheep = (EntityTaintSheep) pointedEntity;
                EntitySheep entitysheep = new EntitySheep(entityTaintSheep.worldObj);
                this.convertFromTo(entityTaintSheep, entitysheep);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }

            // if target is a tainted cow, convert to cow
            else if (pointedEntity instanceof EntityTaintCow) {
                EntityTaintCow entityTaintCow = (EntityTaintCow) pointedEntity;
                EntityCow entityCow = new EntityCow(entityTaintCow.worldObj);
                this.convertFromTo(entityTaintCow, entityCow);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }

            // if target is a tainted chicken, convert to chicken
            else if (pointedEntity instanceof EntityTaintChicken) {
                EntityTaintChicken entityTaintChicken = (EntityTaintChicken) pointedEntity;
                EntityChicken chicken = new EntityChicken(entityTaintChicken.worldObj);
                this.convertFromTo(entityTaintChicken, chicken);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }

            // if target is a tainted villager, convert to villager
            else if (pointedEntity instanceof EntityTaintVillager) {
                EntityTaintVillager entityTaintVillager = (EntityTaintVillager) pointedEntity;
                EntityVillager villager = new EntityVillager(entityTaintVillager.worldObj);
                this.convertFromTo(entityTaintVillager, villager);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }

            // if target is a tainted pig, convert to pig
            else if (pointedEntity instanceof EntityTaintPig) {
                EntityTaintPig entityTaintPig = (EntityTaintPig) pointedEntity;
                EntityPig pig = new EntityPig(entityTaintPig.worldObj);
                this.convertFromTo(entityTaintPig, pig);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }
            // if target is a tainted creeper, convert to creeper
            else if (pointedEntity instanceof EntityTaintCreeper) {
                EntityTaintCreeper entityTaintCreeper = (EntityTaintCreeper) pointedEntity;
                EntityCreeper creeper = new EntityCreeper(entityTaintCreeper.worldObj);
                this.convertFromTo(entityTaintCreeper, creeper);
                wand.consumeAllVis(itemstack, p, this.getVisCost(itemstack), true, false);
            }
        }

        if (mop != null && mop.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
            for (int xOffset = -potency; xOffset < 1 + potency; xOffset++) {
                for (int zOffset = -potency; zOffset < 1 + potency; zOffset++) {
                    this.cleanUpLand(mop.blockX + xOffset, mop.blockZ + zOffset, world, itemstack, p);
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
        if (tile instanceof TileNode) {
            TileNode node = (TileNode) tile;
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

    /*
     * converts original entity to convertTo entity and subtracts one from player
     * held item stack requires the following lines before calling (replace
     * EntityConvertTo with the convertTo entity and EntityOriginal with original
     * entity): EntityOriginal original = (EntityOriginal)pointedEntity;
     * EntityConvertTo convertTo = new EntityConvertTo(original.worldObj);
     */
    public void convertFromTo(Entity original, EntityLivingBase convertTo) {
        // new entity copies original entity location
        convertTo.copyLocationAndAnglesFrom(original);
        // original entity spawns new entity into the world
        if (!original.worldObj.isRemote) {
            original.worldObj.spawnEntityInWorld(convertTo);
        }
        // new entity removes the old entity
        convertTo.worldObj.removeEntity(original);
    }

    private AspectList getBigVisCost(int potency) {
        return new AspectList().add(Aspect.EARTH, 10 * (potency * 2 + 1) * (potency * 2 + 1))
            .add(Aspect.ORDER, 15 * (potency * 2 + 1) * (potency * 2 + 1));
    }

    private void cleanUpLand(int x, int z, World world, ItemStack itemStack, EntityPlayer p) {
        boolean flag = false; // this is used to check if there *is* any block needing to be removed in the
                              // check to see if the original biome is also magicalforest or eerie
        if (!p.worldObj.isRemote) {
            for (int y = 0; y < 256; y++) {
                if (world.getBlock(x, y, z) == ConfigBlocks.blockTaintFibres) {
                    world.setBlock(x, y, z, Blocks.air);
                    flag = true;
                }
                if (world.getBlock(x, y, z) == ConfigBlocks.blockTaint) {
                    if (world.getBlockMetadata(x, y, z) == 2) {
                        return;
                    } // Flesh blocks. WHAT THE FUCK THAUMCRAFT
                    if (world.getBlockMetadata(x, y, z) == 0) {
                        world.setBlock(
                            x,
                            y,
                            z,
                            ConfigBlocks.blockFluxGoo,
                            ((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta(),
                            3);
                    } else if (world.getBlockMetadata(x, y, z) == 1) {
                        world.setBlock(x, y, z, Blocks.dirt);
                    } else {
                        world.setBlock(x, y, z, Blocks.air);
                    }
                    flag = true;
                }
            }
            if (((world.getBiomeGenForCoords(x, z).biomeID == Config.biomeTaintID)
                || (world.getBiomeGenForCoords(x, z).biomeID == Config.biomeEerieID)
                || (world.getBiomeGenForCoords(x, z).biomeID == Config.biomeMagicalForestID))) {
                ItemWandCasting wand = (ItemWandCasting) itemStack.getItem();
                BiomeGenBase[] biomesForGeneration = null;
                biomesForGeneration = world.getWorldChunkManager()
                    .loadBlockGeneratorData(biomesForGeneration, x, z, 1, 1);

                if ((biomesForGeneration != null) && (biomesForGeneration[0] != null)) {
                    if (!flag) {
                        if ((biomesForGeneration[0].biomeID == Config.biomeEerieID
                            && world.getBiomeGenForCoords(x, z).biomeID == Config.biomeEerieID)
                            || (biomesForGeneration[0].biomeID == Config.biomeMagicalForestID
                                && world.getBiomeGenForCoords(x, z).biomeID == Config.biomeMagicalForestID)) {
                            return;
                        }
                    }
                    wand.consumeAllVis(itemStack, p, this.getVisCost(itemStack), true, false);
                    if (biomesForGeneration[0].biomeID == Config.biomeTaintID) {
                        BlightbusterNetwork.setBiomeAt(world, x, z, BiomeGenBase.getBiome(Config.biomeMagicalForestID));
                    } else {
                        BlightbusterNetwork.setBiomeAt(world, x, z, biomesForGeneration[0]);
                    }

                }
            }
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
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return new FocusUpgradeType[] { FocusUpgradeType.frugal, FocusUpgradeType.enlarge };
            default:
                return null;
        }
    }

    @Override
    public AspectList getVisCost(ItemStack arg0) {
        return cost;
    }

    public AspectList getNodeVisCost(ItemStack arg0) {
        return auraCost;
    }
}
