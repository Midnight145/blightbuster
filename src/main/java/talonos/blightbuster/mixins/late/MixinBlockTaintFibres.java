package talonos.blightbuster.mixins.late;

import java.util.Random;

import net.minecraft.block.Block;
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
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) world.getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                int chunkX = x / 16;
                int chunkZ = z / 16;
                for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                    int[] chunkCoords = tile.cleansedChunks.get(i);
                    if (chunkCoords[0] == chunkX && chunkCoords[1] == chunkZ) {
                        ci.cancel();
                        return;
                    }
                }
            }
        }
    }

    @Inject(method = "spreadFibres", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void spreadFibresMixin(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) world.getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                int chunkX = x / 16;
                int chunkZ = z / 16;
                for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                    int[] chunkCoords = tile.cleansedChunks.get(i);
                    if (chunkCoords[0] == chunkX && chunkCoords[1] == chunkZ) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
