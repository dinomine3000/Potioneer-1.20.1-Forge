package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.CooldownAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;

public class CooldownSourceEffect extends BeyonderEffect {
    private static final int minDefensiveCooldown = 5*20;
    private static final int maxDefensiveCooldown = 60*20;
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(cost);
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker,
                                BeyonderCapability victimCap, Optional<BeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(attacker == null || optAttackerCap.isEmpty() || !calledOnVictim) return false;
        if(victim.level().isClientSide()) return false;

        BeyonderCapability attackerCap = optAttackerCap.get();

        if(!attackerCap.getLuckManager().passesLuckCheck(1/2f, 0, 0, attacker.getRandom())){
            attackerCap.getEffectsManager().addOrRefreshEffect(CooldownAbility.createCooldownEffectInstance(
                            getSequenceLevel(), minDefensiveCooldown, maxDefensiveCooldown,20*5),
                    attackerCap, attacker);
            return false;
        }

        return false;
    }
}
