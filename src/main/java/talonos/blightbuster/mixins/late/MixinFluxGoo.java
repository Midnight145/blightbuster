package talonos.blightbuster.mixins.late;

import java.util.Random;

import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.blocks.BlockFluxGoo;

@Mixin(value = BlockFluxGoo.class, remap = false)
public abstract class MixinFluxGoo {

    @Inject(method = "updateTick", at = @At(value = "HEAD"), cancellable = true)
    private void updateTickMixin(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) world.getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                int meta = world.getBlockMetadata(x, y, z);
                if (meta >= 6 && world.isAirBlock(x, y + 1, z)) {
                    for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                        int[] chunkCoords = tile.cleansedChunks.get(i);
                        if (chunkCoords[0] == x / 16 && chunkCoords[1] == z / 16) {
                            ci.cancel();
                            return;
                        }
                    }
                }
            }
        }
    }
}
