package talonos.blightbuster.mixins.late;

import java.util.Random;

import com.google.common.collect.ImmutableList;
import net.minecraft.world.ChunkCoordIntPair;
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
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x / 16, z / 16);
        for (DawnMachineTileEntity tile : tiles) {
            int meta = world.getBlockMetadata(x, y, z);
            if (meta >= 6 && world.isAirBlock(x, y + 1, z)) {
                if (tile.cleansedChunks.contains(chunk)) {
                    ci.cancel();
                    return;
                }
            }
        }
    }
}
