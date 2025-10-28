package talonos.blightbuster.mixins.late;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.blocks.BlockTaintFibres;

@Mixin(value = BlockTaintFibres.class, remap = false)
public abstract class MixinBlockTaintFibres {

    @Inject(method = "taintBiomeSpread", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void taintBiomeSpreadMixin(World world, int x, int y, int z, Random rand, Block block,
        CallbackInfo ci) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x >> 4, z >> 4);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                ci.cancel();
                return;
            }
        }
    }

    @Inject(method = "spreadFibres", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void spreadFibresMixin(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x >> 4, z >> 4);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                cir.setReturnValue(true);
                return;
            }
        }
    }
}
