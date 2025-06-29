package net.dinomine.potioneer.mob_effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.Vec3;

public class WaterPrisonEffect extends MobEffect {
    protected WaterPrisonEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if(pLivingEntity instanceof Player player && (player.isCreative() || player.isSpectator())) return;
        if(pLivingEntity.isInWater()){
            float mult = pAmplifier;
            if(!pLivingEntity.isEyeInFluidType(Fluids.WATER.getFluidType())){
                mult *= 40f;
            }
            pLivingEntity.addDeltaMovement(new Vec3(0, -mult/20f, 0));
        }
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }
}
