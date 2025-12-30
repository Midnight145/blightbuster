package talonos.blightbuster.mixins.late;

import net.minecraft.tileentity.TileEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import talonos.blightbuster.client.StabilizedNodeRenderer;
import talonos.blightbuster.lib.INodeIsolated;
import thaumcraft.client.renderers.tile.TileNodeRenderer;

@Mixin(TileNodeRenderer.class)
public class MixinTileNodeRenderer {

    @Inject(
        method = "renderTileEntityAt",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/client/renderers/tile/TileNodeRenderer;renderNode(Lnet/minecraft/entity/EntityLivingBase;DZZFIIIFLthaumcraft/api/aspects/AspectList;Lthaumcraft/api/nodes/NodeType;Lthaumcraft/api/nodes/NodeModifier;)V",
            shift = At.Shift.AFTER),
        remap = false)
    public void renderWardedNode(TileEntity tile, double x, double y, double z, float partialTicks, CallbackInfo ci) {
        if (tile instanceof INodeIsolated node && node.isIsolated()) {
            StabilizedNodeRenderer.render(tile.xCoord, tile.yCoord, tile.zCoord, partialTicks);
        }
    }
}
