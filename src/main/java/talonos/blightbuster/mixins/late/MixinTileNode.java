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
        if (DawnMachineTileEntity.coords != null) {
            int[] coords = DawnMachineTileEntity.coords;
            TileNode tmp = (TileNode) (Object) this;
            DawnMachineTileEntity tile = (DawnMachineTileEntity) tmp.getWorldObj()
                .getTileEntity(coords[0], coords[1], coords[2]);
            if (tile != null) {
                for (int i = 0; i < tile.cleansedChunks.size(); i++) {
                    int[] chunkCoords = tile.cleansedChunks.get(i);
                    if (chunkCoords[0] == tmp.xCoord / 16 && chunkCoords[1] == tmp.zCoord / 16) {
                        cir.setReturnValue(false);
                        return;
                    }
                }
            }
        }
    }
}
