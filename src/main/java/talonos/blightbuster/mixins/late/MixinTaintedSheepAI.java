package talonos.blightbuster.mixins.late;

import net.minecraft.entity.EntityLiving;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.entities.ai.misc.AIConvertGrass;

@Mixin(AIConvertGrass.class)
public abstract class MixinTaintedSheepAI {

    @Shadow(remap = false)
    private EntityLiving entity;

    @Inject(method = "shouldExecute", at = @At(value = "HEAD"), cancellable = true)
    private void updateTaskMixin(CallbackInfoReturnable<Boolean> cir) {
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) entity.worldObj
                .getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                    int[] chunkCoords = tile.cleansedChunks.get(i);
                    if (chunkCoords[0] == entity.chunkCoordX && chunkCoords[1] == entity.chunkCoordZ) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
