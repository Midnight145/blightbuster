package talonos.blightbuster.mixins.late;

import net.minecraft.entity.monster.EntityMob;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.google.common.collect.ImmutableList;

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
        ImmutableList<DawnMachineTileEntity> tiles = DawnMachineTileEntity.getDawnMachines(this.worldObj);
        ChunkCoordIntPair chunk = new ChunkCoordIntPair(this.chunkCoordX, this.chunkCoordZ);
        for (DawnMachineTileEntity tile : tiles) {
            if (tile.cleansedChunks.contains(chunk)) {
                this.timeSinceIgnited = 0;
                return;
            }
        }
    }

}
