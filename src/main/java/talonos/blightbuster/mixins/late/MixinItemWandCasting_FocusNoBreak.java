package talonos.blightbuster.mixins.late;

import net.minecraft.entity.EntityLivingBase;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;

import talonos.blightbuster.items.ItemStabilizerFocus;
import thaumcraft.api.wands.ItemFocusBasic;
import thaumcraft.common.items.wands.ItemWandCasting;

@Mixin(ItemWandCasting.class)
public class MixinItemWandCasting_FocusNoBreak {

    @WrapOperation(
        method = "onBlockStartBreak",
        at = @At(
            value = "INVOKE",
            target = "Lthaumcraft/common/items/wands/WandManager;isOnCooldown(Lnet/minecraft/entity/EntityLivingBase;)Z",
            remap = false),
        remap = false)
    private boolean preventBreakIfFocusActive(EntityLivingBase entityLiving, Operation<Boolean> original,
        @Local(name = "focus") ItemFocusBasic focus) {
        if (focus instanceof ItemStabilizerFocus) {
            return entityLiving.isSneaking(); // gets inverted in the original method, so will prevent breaking when not
                                              // sneaking
        }
        return original.call(entityLiving);
    }
}
