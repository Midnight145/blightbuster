package talonos.blightbuster.mixins.late;

import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.lib.utils.Utils;

@Mixin(Utils.class)
public class MixinUtils {

    @WrapMethod(method = "setBiomeAt", remap = false)
    private static void setBiomeAt(World world, int x, int z, BiomeGenBase biome, Operation<Void> original) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x >> 4, z >> 4);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return;
            }
        }
        original.call(world, x, z, biome);
    }
}
