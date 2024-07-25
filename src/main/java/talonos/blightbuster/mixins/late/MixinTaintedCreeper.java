package talonos.blightbuster.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import talonos.blightbuster.BlightBuster;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

@Mixin(value = EntityTaintCreeper.class)
public abstract class MixinTaintedCreeper {

    // @Inject(
    // method = "onUpdate",
    // at = @At(
    // value = "INVOKE_ASSIGN",
    // target = "Lthaumcraft/common/entities/monster/EntityTaintCreeper;getCreeperState()I"),
    // cancellable = true,
    // locals = LocalCapture.CAPTURE_FAILSOFT)
    // private void onUpdateMixin(CallbackInfo ci, int var1) {
    // BlightBuster.logger.info("Mixing in onUpdate");
    // if (DawnMachineTileEntity.instance != null) {
    // EntityTaintCreeper tmp = (EntityTaintCreeper) (Object) this;
    // for (int[] coords : DawnMachineTileEntity.cleansedChunks) {
    // if (coords[0] == (int) tmp.posX / 16 && coords[1] == (int) tmp.posZ / 16) {
    // var1 = 0;
    // return;
    // }
    // }
    // }
    // }

    @Shadow(remap = false)
    private int timeSinceIgnited;

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    private void onUpdateMixin(CallbackInfo c) {
        if (DawnMachineTileEntity.instance != null) {
            EntityTaintCreeper tmp = (EntityTaintCreeper) (Object) this;
            for (int i = 0; i < DawnMachineTileEntity.cleansedChunks.size(); i++) {
                int[] coords = DawnMachineTileEntity.cleansedChunks.get(i);
                if (coords[0] == (int) tmp.posX / 16 && coords[1] == (int) tmp.posZ / 16) {
                    this.timeSinceIgnited = 0;
                    return;
                }
            }
        }
    }
}
