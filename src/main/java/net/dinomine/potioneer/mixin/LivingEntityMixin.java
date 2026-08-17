package net.dinomine.potioneer.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    @Shadow public abstract void remove(Entity.RemovalReason pReason);

    @Unique
    private LivingEntity potioneer$self(){
        return (LivingEntity) (Object) this;
    }


    /**
     * @return
     * @reason only way i could find to get frictionless effect to work
     */
    @ModifyVariable(
            method = "travel",
            at = @At(
                value = "STORE",
                target = "Lnet/minecraft/world/entity/LivingEntity;handleRelativeFrictionAndCalculateMovement(Lnet/minecraft/world/phys/Vec3;F)Lnet/minecraft/world/phys/Vec3;"
            ),
            ordinal = 0
    )
    private float modifyF4Friction(float originalF2) {
        if (AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_FRICTIONLESS.getEffectId(), potioneer$self())) return 1.05F;
        return originalF2;
    }

    @ModifyExpressionValue(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;canElytraFly(Lnet/minecraft/world/entity/LivingEntity;)Z",
                    remap = false
            )
    )
    public boolean elytraOverride(boolean originalFlag) {
        return originalFlag || AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_ELYTRA.getEffectId(), potioneer$self());
    }

    @ModifyExpressionValue(
            method = "updateFallFlying",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/ItemStack;elytraFlightTick(Lnet/minecraft/world/entity/LivingEntity;I)Z",
                    remap = false
            )
    )
    public boolean eytraValidOverride(boolean originalFlag) {
        return originalFlag || AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_ELYTRA.getEffectId(), potioneer$self());
    }

}
