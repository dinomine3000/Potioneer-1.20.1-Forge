package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class CleanseAbility extends Ability {
    @Override
    public void init() {
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        for(MobEffectInstance eff: new ArrayList<>(target.getActiveEffects())){
            if(eff.getEffect().isBeneficial()) continue;
            target.removeEffect(eff.getEffect());
        }
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "cleanse";
    }
}
