package talonos.blightbuster.mixins.late;

import java.util.List;

import net.minecraft.world.ChunkCoordIntPair;

import org.spongepowered.asm.mixin.Mixin;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import talonos.blightbuster.lib.INodeStabilizer;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false)
public abstract class MixinTileNode_Taint extends TileThaumcraft implements INodeStabilizer {

    @WrapMethod(method = "handleTaintNode")
    private boolean handleTaintNodeMixin(boolean change, Operation<Boolean> original) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(this.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(this.xCoord >> 4, this.zCoord >> 4);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return change;
            }
        }
        return original.call(change);
    }
}
