package talonos.blightbuster.mixins.late;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false)
public abstract class MixinTileNode {

    @Inject(method = "handleTaintNode", at = @At(value = "HEAD", remap = false), cancellable = true)
    private void handleTaintNodeMixin(boolean change, CallbackInfoReturnable<Boolean> cir) {
        // BlightBuster.logger.info("Mixed into handleTaintNode");
        if (DawnMachineTileEntity.instance != null) {
            TileNode tmp = (TileNode) (Object) this;
            for (int i = 0; i < DawnMachineTileEntity.cleansedChunks.size(); i++) {
                int[] coords = DawnMachineTileEntity.cleansedChunks.get(i);
                if (coords[0] == tmp.xCoord / 16 && coords[1] == tmp.zCoord / 16) {
                    cir.setReturnValue(false);
                    return;
                }
            }
        }
    }
}
