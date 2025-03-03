package talonos.blightbuster.items;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

import thaumcraft.common.Thaumcraft;

public class PurityFocusFX {

    public static long lastFireTime = 0;
    public static long lastCleanTime = 0;
    public static long lastHealTime = 0;
    public static long lastNodeTime = 0;

    public static void fire(EntityLivingBase entity) {
        double w;
        double d;
        double h;
        // Only play this sound once per cast, even if multiple entities are hit
        if (lastFireTime < entity.worldObj.getTotalWorldTime() - 4) {
            entity.worldObj.playSoundAtEntity(entity, "thaumcraft:fireloop", 0.66F, 2.0F);
            lastFireTime = entity.worldObj.getTotalWorldTime();
        }
        double count = 3 + entity.worldObj.rand.nextInt(8) * entity.height;
        for (int i = (int) count; i >= 0; i--) {
            w = entity.worldObj.rand.nextGaussian() * entity.width * 0.6;
            d = entity.worldObj.rand.nextGaussian() * entity.width * 0.6;
            h = entity.worldObj.rand.nextFloat() * entity.height * 0.05F + entity.height * (i / count);
            Thaumcraft.proxy.drawGenericParticles(
                entity.worldObj,
                entity.posX + w,
                entity.posY + h,
                entity.posZ + d,
                0.0D,
                0.03D,
                0.0D,
                0.9F + entity.worldObj.rand.nextFloat() * 0.1F,
                1.0F,
                1.0F,
                0.7F,
                false,
                160,
                10,
                1,
                8 + entity.worldObj.rand.nextInt(4),
                (int) (h * 2),
                2F + entity.worldObj.rand.nextFloat() * 0.2F);
        }
    }

    public static void clean(EntityLivingBase entity) {
        double w;
        double d;
        double h;
        // Only play this sound once per cast, even if multiple entities are hit
        if (lastCleanTime < entity.worldObj.getTotalWorldTime() - 4) {
            entity.worldObj.playSoundAtEntity(entity, "thaumcraft:gore", 0.5F, 1);
            lastCleanTime = entity.worldObj.getTotalWorldTime();
        }
        for (int i = 20; i > 0; i--) {
            for (int j = 20; j > 0; j--) {
                w = entity.worldObj.rand.nextFloat() * entity.width;
                d = entity.worldObj.rand.nextFloat() * entity.width;
                h = entity.worldObj.rand.nextFloat() * entity.height;

            }
        }

    }

    public static void heal(EntityLivingBase entity) {
        double w;
        double d;
        double h;
        // Only play this sound once per cast, even if multiple entities are hit
        if (lastHealTime < entity.worldObj.getTotalWorldTime() - 4) {
            entity.worldObj.playSoundAtEntity(entity, "thaumcraft:wand", 0.5F, 1);
            lastHealTime = entity.worldObj.getTotalWorldTime();
        }
        for (int i = (2 + entity.worldObj.rand.nextInt(3)); i > 0; i--) {
            w = (entity.worldObj.rand.nextGaussian() - 0.5) * entity.width;
            d = (entity.worldObj.rand.nextGaussian() - 0.5) * entity.width;
            h = (entity.worldObj.rand.nextGaussian() - 0.2) * entity.height * 0.8F;

            double d0 = entity.worldObj.rand.nextGaussian() * 0.02D;
            double d1 = entity.worldObj.rand.nextGaussian() * 0.02D;
            double d2 = entity.worldObj.rand.nextGaussian() * 0.02D;
            entity.worldObj.spawnParticle("heart", entity.posX + w, entity.posY + h, entity.posZ + d, d0, d1, d2);
        }
    }

    public static void node(EntityPlayer player, int x, int y, int z) {
        float w;
        float d;
        float h;
        // Only play this sound once at a time, even if multiple nodes are hit.
        // It also only plays if the node is within 10 blocks.
        if (lastNodeTime < player.worldObj.getTotalWorldTime() - 4 && player.getDistanceSq(x, y, z) < 100) {
            player.playSound("thaumcraft:wand", 0.5F, 1F);
            lastNodeTime = player.worldObj.getTotalWorldTime();
        }

        for (int i = 12; i >= 0; i--) {
            w = (float) (player.worldObj.rand.nextGaussian() + 1) / 2F;
            d = (float) (player.worldObj.rand.nextGaussian() + 1) / 2F;
            h = (float) (player.worldObj.rand.nextGaussian() + 1) / 2F;
            Thaumcraft.proxy.sparkle(x + w, y + d, z + h, 2, 0, 0);
        }
    }
}
