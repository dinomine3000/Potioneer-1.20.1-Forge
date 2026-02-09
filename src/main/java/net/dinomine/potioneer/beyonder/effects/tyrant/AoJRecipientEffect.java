package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class AoJRecipientEffect extends AbstractSourceRecipientEffect {

    public static AoJRecipientEffect getInstance(UUID enforcerId){
        AoJRecipientEffect eff = (AoJRecipientEffect) BeyonderEffects.TYRANT_AOJ_RECIPIENT.createInstance(0, 0, -1, true);
        eff.setEnforcer(enforcerId);
        return eff;
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof AoJRecipientEffect aojEffect)) return;
        this.sources.putAll(aojEffect.sources);
    }

    private void setEnforcer(UUID enforcer){
        this.sources.put(enforcer, 40);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.AOJ_INFLUENCE.get(), 20, 0, false, false, true));
        tickDownTime(target);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    public boolean isEntityEnforcer(UUID id) {
        return sources.containsKey(id);
    }
}
