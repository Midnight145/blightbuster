package talonos.blightbuster.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

@Mixin(value = EntityTaintCreeper.class)
public abstract class MixinTaintedCreeper {

    @Shadow(remap = false)
    private int timeSinceIgnited;

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    private void onUpdateMixin(CallbackInfo c) {
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            EntityTaintCreeper tmp = (EntityTaintCreeper) (Object) this;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) tmp.worldObj
                .getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                    int[] chunkCoords = tile.cleansedChunks.get(i);
                    if (chunkCoords[0] == (int) tmp.posX / 16 && chunkCoords[1] == (int) tmp.posZ / 16) {
                        this.timeSinceIgnited = 0;
                        return;
                    }
                }
            }
        }
    }
}
