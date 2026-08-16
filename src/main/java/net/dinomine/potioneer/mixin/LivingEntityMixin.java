package net.dinomine.potioneer.mixin;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

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
                value = "STORE"
            ),
            ordinal = 0
    )
    private float modifyF4Friction(float originalF2) {
        if (AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_FRICTIONLESS.getEffectId(), potioneer$self())) return 1.05F;
        return originalF2;
    }
}
