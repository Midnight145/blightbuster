package talonos.blightbuster.mixins.late;

import com.google.common.collect.ImmutableList;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.ChunkCoordIntPair;
import org.spongepowered.asm.mixin.Mixin;

import org.spongepowered.asm.mixin.injection.At;
import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false)
public abstract class MixinTileNode extends TileThaumcraft {

    @WrapMethod(method = "handleTaintNode")
    private boolean handleTaintNodeMixin(boolean change, Operation<Boolean> original) {
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(this.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(this.xCoord / 16, this.zCoord / 16);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return change;
            }
        }
        return original.call(change);
    }

    @WrapOperation(method = "handleTaintNode", at = @At(value = "INVOKE", target="Lthaumcraft/common/lib/utils/Utils;setBiomeAt(Lnet/minecraft/world/World;IILnet/minecraft/world/biome/BiomeGenBase;)V"))
    private void stopTaintNodeInChunk(net.minecraft.world.World world, int x, int z, net.minecraft.world.biome.BiomeGenBase biome, Operation<Void> original) {
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(world);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x / 16, z / 16);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return;
            }
        }
        original.call(world, x, z, biome);
    }
}
