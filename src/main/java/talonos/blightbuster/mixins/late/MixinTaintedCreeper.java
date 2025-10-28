package talonos.blightbuster.mixins.late;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;

import talonos.blightbuster.tileentity.DawnMachineTileEntity;
import thaumcraft.common.entities.monster.EntityTaintCreeper;

@Mixin(value = EntityTaintCreeper.class)
public abstract class MixinTaintedCreeper extends EntityMob {

    @Shadow(remap = false)
    private int timeSinceIgnited;

    public MixinTaintedCreeper(World world) {
        super(world);
    }

    @Inject(method = "onUpdate", at = @At(value = "HEAD"))
    private void onUpdateMixin(CallbackInfo c) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(this.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                this.timeSinceIgnited = 0;
                return;
            }
        }
    }

    @WrapOperation(
        method = "onUpdate",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;setBlock(IIILnet/minecraft/block/Block;II)Z"))
    private boolean wrapSetBlock(World world, int x, int y, int z, Block block, int meta, int flags,
        Operation<Boolean> original) {
        List<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(this.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(x >> 4, z >> 4);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                return false;
            }
        }
        return original.call(world, x, y, z, block, meta, flags);
    }
}
