package talonos.blightbuster.mixins.late;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import talonos.blightbuster.lib.INodeIsolated;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false, priority = 1001)
public abstract class MixinTileNode_Isolate extends TileThaumcraft implements INodeIsolated {

    @Unique
    public boolean bb$isolated = false;

    @Override
    public void setIsolated(boolean bb$isolated) {
        this.bb$isolated = bb$isolated;
        this.markDirty();
    }

    @Override
    public boolean isIsolated() {
        return this.bb$isolated;
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readFromNBTMixin(NBTTagCompound nbt, CallbackInfo c) {
        this.bb$isolated = nbt.getBoolean("bb:isolated");
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeToNBTMixin(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setBoolean("bb:isolated", this.bb$isolated);
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"))
    private void writeCustomNBTMixin(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setBoolean("bb:isolated", this.bb$isolated);
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void readCustomNBTMixin(NBTTagCompound nbt, CallbackInfo c) {
        this.bb$isolated = nbt.getBoolean("bb:isolated");
    }

    @WrapMethod(
        method = "onWandRightClick(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;")
    private ItemStack wrapOnRightClick(World world, ItemStack wandstack, EntityPlayer player,
        Operation<ItemStack> original) {
        if (this.bb$isolated) {
            return wandstack;
        }
        return original.call(world, wandstack, player);
    }

    @WrapMethod(
        method = { "handleDarkNode", "handleTaintNode", "handleRecharge", "handlePureNode", "handleDischarge",
            "handleHungryNodeFirst", "handleHungryNodeSecond", "handleNodeStability" })
    private boolean killNodeUpdates(boolean change, Operation<Boolean> original) {
        if (this.bb$isolated) {
            return change;
        }
        return original.call(change);
    }
}
