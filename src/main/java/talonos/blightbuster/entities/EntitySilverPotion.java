package talonos.blightbuster.entities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

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
    protected float getGravityVelocity() {
        return 0.05F;
    }

    protected float func_70182_d() {
        return 0.5F;
    }

    protected float func_70183_g() {
        return -20.0F;
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition par1MovingObjectPosition) {
        if (!this.worldObj.isRemote) {
            for (int x = -5; x < 5; x++) {
                for (int z = -5; z < 5; z++) {
                    if (((this.worldObj.getBiomeGenForCoords(x + (int) this.posX, z + (int) this.posZ).biomeID
                        == Config.biomeTaintID)
                        || (this.worldObj.getBiomeGenForCoords(x + (int) this.posX, z + (int) this.posZ).biomeID
                            == Config.biomeEerieID)
                        || (this.worldObj.getBiomeGenForCoords(x + (int) this.posX, z + (int) this.posZ).biomeID
                            == Config.biomeMagicalForestID))
                        && (getDistanceFrom(x, z) <= 5.0D)) {
                        BiomeGenBase[] biomesForGeneration = null;
                        biomesForGeneration = this.worldObj.getWorldChunkManager()
                            .loadBlockGeneratorData(
                                biomesForGeneration,
                                x + (int) this.posX,
                                z + (int) this.posZ,
                                1,
                                1);
                        if ((biomesForGeneration != null) && (biomesForGeneration[0] != null)) {
                            BlightbusterNetwork.setBiomeAt(
                                this.worldObj,
                                x + (int) this.posX,
                                z + (int) this.posZ,
                                biomesForGeneration[0]);
                        }
                    }

                    for (int y = -3; y < 3; y++) {
                        if (getDistanceFrom(x, y, z) <= 3.0D && this.posY + y < 256 && this.posY + y >= 0) {
                            if (this.worldObj.getBlock(x + (int) this.posX, y + (int) this.posY, z + (int) this.posZ)
                                == ConfigBlocks.blockTaintFibres) {
                                worldObj.setBlock(
                                    x + (int) this.posX,
                                    y + (int) this.posY,
                                    z + (int) this.posZ,
                                    Blocks.air);
                            }
                            if (worldObj.getBlock(x + (int) this.posX, y + (int) this.posY, z + (int) this.posZ)
                                == ConfigBlocks.blockTaint) {
                                if (worldObj
                                    .getBlockMetadata(x + (int) this.posX, y + (int) this.posY, z + (int) this.posZ)
                                    == 0) {
                                    worldObj.setBlock(
                                        x + (int) this.posX,
                                        y + (int) this.posY,
                                        z + (int) this.posZ,
                                        ConfigBlocks.blockFluxGoo,
                                        ((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta(),
                                        3);
                                } else if (worldObj
                                    .getBlockMetadata(x + (int) this.posX, y + (int) this.posY, z + (int) this.posZ)
                                    == 1) {
                                        worldObj.setBlock(
                                            x + (int) this.posX,
                                            y + (int) this.posY,
                                            z + (int) this.posZ,
                                            Blocks.dirt);
                                    } else {
                                        worldObj.setBlock(
                                            x + (int) this.posX,
                                            y + (int) this.posY,
                                            z + (int) this.posZ,
                                            Blocks.air);
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

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {
        super.readEntityFromNBT(par1NBTTagCompound);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {
        super.writeEntityToNBT(par1NBTTagCompound);
    }
}
