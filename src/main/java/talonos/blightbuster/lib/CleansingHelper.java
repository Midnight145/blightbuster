package talonos.blightbuster.lib;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.common.registry.VillagerRegistry;
import noppes.npcs.entity.EntityCustomNpc;
import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.BlightbusterConfig;
import talonos.blightbuster.network.BlightbusterNetwork;
import thaumcraft.common.blocks.BlockFluxGoo;
import thaumcraft.common.config.Config;
import thaumcraft.common.config.ConfigBlocks;

public class CleansingHelper {

    public static boolean cleanseMobFromMapping(Entity entity, World world) {
        Class<?> clazz = entity.getClass();
        if (BlightbusterConfig.purifiedMappings.containsKey(clazz)) {
            try {
                cleanseSingleMob(
                    entity,
                    (EntityLivingBase) BlightbusterConfig.purifiedMappings.get(clazz)
                        .newInstance(world));
                return true;
            } catch (final Exception e) {
                BlightBuster.logger.error("Failed to cleanse entity from mapping: " + clazz.getName(), e);
            }
        }
        if (BlightbusterConfig.customNpcSupport && entity instanceof EntityCustomNpc npc) {
            if (BlightbusterConfig.customNpcMappings.containsKey(npc.linkedName)) {
                try {
                    cleanseSingleMob(
                        entity,
                        (EntityLivingBase) BlightbusterConfig.customNpcMappings.get(npc.linkedName)
                            .newInstance(world));
                    return true;
                } catch (final Exception e) {
                    BlightBuster.logger.error("Failed to cleanse entity from mapping: " + npc.linkedName, e);
                }
            }
        }
        if (entity instanceof EntityLivingBase e && e.isPotionActive(Config.potionTaintPoisonID)) {
            e.removePotionEffect(Config.potionTaintPoisonID);
            return true;
        }
        return false;
    }

    private static void cleanseSingleMob(Entity tainted, EntityLivingBase cleansed) {
        // new entity copies original entity location
        cleansed.copyLocationAndAnglesFrom(tainted);
        // original entity spawns new entity into the world
        tainted.worldObj.spawnEntityInWorld(cleansed);
        // new entity removes the old entity
        cleansed.worldObj.removeEntity(tainted);

        if (cleansed instanceof EntityVillager villager) {
            VillagerRegistry.applyRandomTrade(villager, villager.worldObj.rand);
        }
    }

    public static boolean cleanseBiome(int x, int z, World world) {
        BiomeGenBase[] genBiomes = world.getWorldChunkManager()
            .loadBlockGeneratorData(null, x, z, 1, 1);
        final BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (biome.biomeID == Config.biomeTaintID || biome.biomeID == Config.biomeEerieID
            || biome.biomeID == Config.biomeMagicalForestID) {

            if (genBiomes != null && genBiomes.length > 0 && genBiomes[0] != null) {
                if (genBiomes[0].biomeID == Config.biomeTaintID) {
                    BlightbusterNetwork.setBiomeAt(world, x, z, BlightbusterConfig.defaultBiome);
                    return true;
                } else if ((genBiomes[0].biomeID == Config.biomeEerieID
                    || genBiomes[0].biomeID == Config.biomeMagicalForestID) && genBiomes[0].biomeID == biome.biomeID) {
                        return false;
                    }
                BlightbusterNetwork.setBiomeAt(world, x, z, genBiomes[0]);
                return true;
            }
        }
        return false;
    }

    public static boolean cleanBlock(int x, int y, int z, World world) {
        boolean didSomething = false;
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block == ConfigBlocks.blockTaintFibres) {
            world.setBlockToAir(x, y, z);
            didSomething = true;
        } else if (block == ConfigBlocks.blockTaint) {
            if (meta == 2) {
                return didSomething;
            }
            if (meta == 0) {
                world.setBlock(
                    x,
                    y,
                    z,
                    ConfigBlocks.blockFluxGoo,
                    ((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta(),
                    3);
                didSomething = true;
            } else if (meta == 1) {
                world.setBlock(x, y, z, Blocks.dirt, 0, 3);
                didSomething = true;
            } else {
                world.setBlockToAir(x, y, z);
                didSomething = true;
            }
        }
        if (didSomething) {
            world.markBlockForUpdate(x, y, z);
        }
        return didSomething;
    }

    public static boolean cleanFlux(int x, int y, int z, World world) {
        boolean didSomething = false;
        Block block = world.getBlock(x, y, z);
        if (block == ConfigBlocks.blockFluxGoo) {
            world.setBlockToAir(x, y, z);
            didSomething = true;
        } else if (block == ConfigBlocks.blockFluxGas) {
            world.setBlockToAir(x, y, z);
            didSomething = true;
        }
        if (didSomething) {
            world.markBlockForUpdate(x, y, z);
        }
        return didSomething;
    }
}
