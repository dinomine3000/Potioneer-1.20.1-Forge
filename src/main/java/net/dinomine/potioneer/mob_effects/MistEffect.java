package net.dinomine.potioneer.mob_effects;

import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class MistEffect extends MobEffect {
    protected MistEffect(MobEffectCategory pCategory, int pColor) {
        super(pCategory, pColor);
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
    }

    @Override
    public boolean isDurationEffectTick(int duration, int pAmplifier) {
        return false;
    }
}
