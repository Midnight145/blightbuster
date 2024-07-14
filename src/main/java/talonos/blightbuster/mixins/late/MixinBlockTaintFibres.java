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
        if (DawnMachineTileEntity.instance != null) {
            int chunkX = x / 16;
            int chunkZ = z / 16;
            for (int[] coords : DawnMachineTileEntity.cleansedChunks) {
                if (coords[0] == chunkX && coords[1] == chunkZ) {
                    ci.cancel();
                    return;
                }
            }
        }
    }

    @Inject(method = "spreadFibres", at = @At(value = "HEAD", remap = false), cancellable = true)
    private static void spreadFibresMixin(World world, int x, int y, int z, CallbackInfoReturnable<Boolean> cir) {
        if (DawnMachineTileEntity.instance != null) {
            int chunkX = x / 16;
            int chunkZ = z / 16;
            for (int[] coords : DawnMachineTileEntity.cleansedChunks) {
                if (coords[0] == chunkX && coords[1] == chunkZ) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
