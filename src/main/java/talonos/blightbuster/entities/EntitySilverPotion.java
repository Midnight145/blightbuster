package talonos.blightbuster.entities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.lib.CleansingHelper;
import thaumcraft.common.entities.monster.EntityThaumicSlime;

public class EntitySilverPotion extends EntityThrowable {

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
        return 0.6F;
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
                    CleansingHelper.cleanseBiome(newX, newZ, worldObj);

                    for (int y = -3; y < 3; y++) {
                        int newY = y + (int) this.posY;
                        if (this.getDistanceFrom(x, y, z) <= 3.0D && this.posY + y < 256 && this.posY + y >= 0) {
                            CleansingHelper.cleanBlock(newX, newY, newZ, worldObj);
                            CleansingHelper.cleanFlux(newX, newY, newZ, worldObj);
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

    private double getDistanceFrom(double x, double y, double z) {
        return Math.sqrt(x * x + y * y + z * z);
    }

    private void cleanseMobs(int x, int y, int z) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(x - 5, y - 3, z - 5, x + 5, y + 3, z + 5);
        List<EntityLivingBase> entities = new ArrayList<>(
            this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, box));
        for (EntityLivingBase entity : entities) {
            if (entity instanceof EntityThaumicSlime) {
                entity.setDead();
            } else {
                CleansingHelper.cleanseMobFromMapping(entity, worldObj);
            }
        }
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
