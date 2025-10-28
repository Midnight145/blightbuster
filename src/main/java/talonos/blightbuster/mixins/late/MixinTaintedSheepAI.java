package talonos.blightbuster.mixins.late;

import java.util.List;

import net.minecraft.entity.EntityLiving;
import net.minecraft.world.ChunkCoordIntPair;

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
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(entity.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(entity.chunkCoordX, entity.chunkCoordZ);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                cir.setReturnValue(false);
                return;
            }
        }
    }
}
