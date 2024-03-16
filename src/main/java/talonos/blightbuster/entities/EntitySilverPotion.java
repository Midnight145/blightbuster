package talonos.blightbuster.entities;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import noppes.npcs.entity.EntityCustomNpc;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;
import thaumcraft.common.entities.monster.EntityTaintChicken;
import thaumcraft.common.entities.monster.EntityTaintCow;
import thaumcraft.common.entities.monster.EntityTaintCreeper;
import thaumcraft.common.entities.monster.EntityTaintPig;
import thaumcraft.common.entities.monster.EntityTaintSheep;
import thaumcraft.common.entities.monster.EntityTaintVillager;

public class EntitySilverPotion extends EntityThrowable {

    // Used in cleanseSingleMob for nicer purifying of animals
    // Class<?> is tainted mob, constructor is the purified version
    static private HashMap<Class<?>, Constructor<?>> taint_purified_constructors = new HashMap<Class<?>, Constructor<?>>();
    static {
        final Class<?>[] taintedEntities = { EntityTaintSheep.class, EntityTaintChicken.class, EntityTaintCow.class,
            EntityTaintPig.class, EntityTaintVillager.class, EntityTaintCreeper.class };

        final Class<?>[] purifiedEntities = { EntitySheep.class, EntityChicken.class, EntityCow.class, EntityPig.class,
            EntityVillager.class, EntityCreeper.class };

        for (int i = 0; i < taintedEntities.length; i++) {
            try {
                taint_purified_constructors.put(taintedEntities[i], purifiedEntities[i].getConstructor(World.class));
            } catch (final Exception e) {
                e.printStackTrace();
            }
        }
    }

    public EntitySilverPotion(World par1World) {
        super(par1World);
    }

    public EntitySilverPotion(World par1World, EntityLivingBase par2EntityLivingBase) {
        super(par1World, par2EntityLivingBase);
    }

    @SideOnly(Side.CLIENT)
    public EntitySilverPotion(World par1World, double par2, double par4, double par6, int par8) {
        this(par1World, par2, par4, par6);
    }

    public EntitySilverPotion(World par1World, double par2, double par4, double par6) {
        super(par1World, par2, par4, par6);
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    @Override
    protected float getGravityVelocity() {
        return 0.05F;
    }

    @Override
    protected float func_70182_d() {
        return 0.5F;
    }

    @Override
    protected float func_70183_g() {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    @Override
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
        if (!this.worldObj.isRemote) {
            this.cleanseMobs((int) this.posX, (int) this.posY, (int) this.posZ);
            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    int newX = x + (int) this.posX;
                    int newZ = z + (int) this.posZ;
                    int biomeid = this.worldObj.getBiomeGenForCoords(newX, newZ).biomeID;
                    if ((biomeid == Config.biomeTaintID || biomeid == Config.biomeEerieID
                        || biomeid == Config.biomeMagicalForestID) && this.getDistanceFrom(x, z) <= 5.0D) {
                        BiomeGenBase[] biomesForGeneration = null;
                        biomesForGeneration = this.worldObj.getWorldChunkManager()
                            .loadBlockGeneratorData(biomesForGeneration, newX, newZ, 1, 1);
                        if (biomesForGeneration != null && biomesForGeneration[0] != null) {
                            BlightbusterNetwork.setBiomeAt(this.worldObj, newX, newZ, biomesForGeneration[0]);
                        }
                    }

                    for (int y = -3; y < 3; y++) {
                        int newY = y + (int) this.posY;
                        if (this.getDistanceFrom(x, y, z) <= 3.0D && this.posY + y < 256 && this.posY + y >= 0) {
                            Block block = this.worldObj.getBlock(newX, newY, newZ);
                            int meta = this.worldObj.getBlockMetadata(newX, newY, newZ);
                            if (block == ConfigBlocks.blockTaintFibres) {
                                this.worldObj.setBlockToAir(newX, newY, newZ);
                            }
                            if (block == ConfigBlocks.blockTaint) {
                                if (meta != 1) {
                                    this.worldObj.setBlockToAir(newX, newY, newZ);
                                } else {
                                    this.worldObj.setBlock(
                                        x + (int) this.posX,
                                        y + (int) this.posY,
                                        z + (int) this.posZ,
                                        Blocks.dirt);
                                }
                            }
                        }
                    }
                }
            }

            this.worldObj.playAuxSFX(
                2002,
                (int) Math.round(this.posX),
                (int) Math.round(this.posY),
                (int) Math.round(this.posZ),
                22);
            this.setDead();
        }
    }

    private double getDistanceFrom(double x, double z) {
        return Math.sqrt(x * x + z * z);
    }

    private double getDistanceFrom(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    private void cleanseMobs(int x, int y, int z) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 5, y - 3, z - 5, x + 5, y + 3, z + 5);
        List<EntityLivingBase> entities = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box);
        for (int i = 0; i < entities.size(); i++) {
            EntityLivingBase entity = entities.get(i);
            if (EntitySilverPotion.taint_purified_constructors.containsKey(entity.getClass())) {
                try {
                    this.cleanseSingleMob(
                        entity,
                        (EntityLivingBase) taint_purified_constructors.get(entity.getClass())
                            .newInstance(this.worldObj));
                } catch (final Exception e) {}
            }
            if (entity instanceof EntityCustomNpc) {
                final EntityCustomNpc npc = (EntityCustomNpc) entity;

                if ("TaintedOcelot".equals(npc.linkedName)) {
                    this.cleanseSingleMob(entity, new EntityOcelot(entity.worldObj));
                } else if ("TaintedWolf".equals(npc.linkedName)) {
                    this.cleanseSingleMob(entity, new EntityWolf(entity.worldObj));
                } else if ("TaintedTownsfolk".equals(npc.linkedName)) {
                    this.cleanseSingleMob(entity, new EntityVillager(entity.worldObj));
                }
            }
        }
    }

    private void cleanseSingleMob(Entity tainted, EntityLivingBase cleansed) {
        // new entity copies original entity location
        cleansed.copyLocationAndAnglesFrom(tainted);
        // original entity spawns new entity into the world
        tainted.worldObj.spawnEntityInWorld(cleansed);
        // new entity removes the old entity
        cleansed.worldObj.removeEntity(tainted);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    @Override
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    @Override
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }
}
