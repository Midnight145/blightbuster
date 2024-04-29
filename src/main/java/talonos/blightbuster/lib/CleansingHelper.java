package talonos.blightbuster.lib;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

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
        return false;
    }

    private static void cleanseSingleMob(Entity tainted, EntityLivingBase cleansed) {
        // new entity copies original entity location
        cleansed.copyLocationAndAnglesFrom(tainted);
        // original entity spawns new entity into the world
        tainted.worldObj.spawnEntityInWorld(cleansed);
        // new entity removes the old entity
        cleansed.worldObj.removeEntity(tainted);
    }

    public static void cleanseBiome(int x, int z, World world) {
        BiomeGenBase[] genBiomes = world.getWorldChunkManager()
            .loadBlockGeneratorData(null, x, z, 1, 1);
        final BiomeGenBase biome = world.getBiomeGenForCoords(x, z);
        if (biome.biomeID == Config.biomeTaintID || biome.biomeID == Config.biomeEerieID
            || biome.biomeID == Config.biomeMagicalForestID) {

            if (genBiomes != null && genBiomes.length > 0 && genBiomes[0] != null) {
                if (genBiomes[0].biomeID == Config.biomeTaintID) {
                    BlightbusterNetwork.setBiomeAt(world, x, z, BlightbusterConfig.defaultBiome);
                    return;
                } else if (genBiomes[0].biomeID == Config.biomeEerieID
                    || genBiomes[0].biomeID == Config.biomeMagicalForestID) {
                        return;
                    }
                BlightbusterNetwork.setBiomeAt(world, x, z, genBiomes[0]);
            }
        }
    }

    public static void cleanBlock(int x, int y, int z, World world) {
        Block block = world.getBlock(x, y, z);
        int meta = world.getBlockMetadata(x, y, z);
        if (block == ConfigBlocks.blockTaintFibres) {
            world.setBlockToAir(x, y, z);
        }
        if (world.getBlock(x, y, z) == ConfigBlocks.blockTaint) {
            if (meta == 2) {
                return;
            }
            if (meta == 0) {
                world.setBlock(
                    x,
                    y,
                    z,
                    ConfigBlocks.blockFluxGoo,
                    ((BlockFluxGoo) ConfigBlocks.blockFluxGoo).getQuanta(),
                    3);
            } else if (meta == 1) {
                world.setBlock(x, y, z, Blocks.dirt);
            } else {
                world.setBlock(x, y, z, Blocks.air);
            }
        }
    }
}
