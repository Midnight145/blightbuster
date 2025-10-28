package talonos.blightbuster.mixins.late;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.blocks.BlockTaintFibres;

@Mixin(value = BlockTaintFibres.class, remap = false)
public abstract class MixinBlockTaintFibres {

    @Inject(method = "taintBiomeSpread", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void taintBiomeSpreadMixin(World world, int x, int y, int z, Random rand, Block block,
        CallbackInfo ci) {
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x / 16, z / 16);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                ci.cancel();
                return;
            }
        }
    }

    @WrapOperation(
        method = "taintBiomeSpread",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/common/lib/utils/Utils;setBiomeAt(Lnet/minecraft/world/World;IILnet/minecraft/world/biome/BiomeGenBase;)V"))
    private static void stopTaintInChunk(World world, int x, int z, BiomeGenBase biome, Operation<Void> original) {
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x / 16, z / 16);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return;
            }
        }
        original.call(world, x, z, biome);
    }

    @Inject(method = "spreadFibres", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void spreadFibresMixin(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x / 16, z / 16);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
