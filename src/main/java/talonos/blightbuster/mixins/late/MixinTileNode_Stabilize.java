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

import talonos.blightbuster.lib.INodeStabilized;
import thaumcraft.api.TileThaumcraft;
import thaumcraft.common.tiles.TileNode;

@Mixin(value = TileNode.class, remap = false, priority = 1001)
public abstract class MixinTileNode_Stabilize extends TileThaumcraft implements INodeStabilized {

    @Unique
    public boolean bb$stabilized = false;

    @Override
    public void setStabilized(boolean bb$stabilized) {
        this.bb$stabilized = bb$stabilized;
        this.markDirty();
    }

    @Override
    public boolean isStabilized() {
        return this.bb$stabilized;
    }

    @Inject(method = "readFromNBT", at = @At("TAIL"))
    private void readFromNBTMixin(NBTTagCompound nbt, CallbackInfo c) {
        this.bb$stabilized = nbt.getBoolean("bb:stabilized");
    }

    @Inject(method = "writeToNBT", at = @At("TAIL"))
    private void writeToNBTMixin(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setBoolean("bb:stabilized", this.bb$stabilized);
    }

    @Inject(method = "writeCustomNBT", at = @At("TAIL"))
    private void writeCustomNBTMixin(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setBoolean("bb:stabilized", this.bb$stabilized);
    }

    @Inject(method = "readCustomNBT", at = @At("TAIL"))
    private void readCustomNBTMixin(NBTTagCompound nbt, CallbackInfo c) {
        this.bb$stabilized = nbt.getBoolean("bb:stabilized");
    }

    @WrapMethod(
        method = "onWandRightClick(Lnet/minecraft/world/World;Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/player/EntityPlayer;)Lnet/minecraft/item/ItemStack;")
    private ItemStack wrapOnRightClick(World world, ItemStack wandstack, EntityPlayer player,
        Operation<ItemStack> original) {
        if (this.bb$stabilized) {
            return wandstack;
        }
        return original.call(world, wandstack, player);
    }

    @WrapMethod(
        method = { "handleDarkNode", "handleTaintNode", "handleRecharge", "handlePureNode", "handleDischarge",
            "handleHungryNodeFirst", "handleHungryNodeSecond", "handleNodeStability" })
    private boolean killNodeUpdates(boolean change, Operation<Boolean> original) {
        if (this.bb$stabilized) {
            return change;
        }
        return original.call(change);
    }
}
