package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class InvisibilityEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        AbilityFunctionHelper.getLivingEntitiesAround(target, 32).forEach(ent -> {
            if(ent instanceof Monster monster){
                LivingEntity monsterTarget = monster.getTarget();
                if(monsterTarget != null && monsterTarget.is(target)) monster.setTarget(null);
            }
        });
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.INVISIBILITY)){
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, (this.maxLife-lifetime), 1, false, false));
        }

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.INVISIBILITY)){
            target.removeEffect(MobEffects.INVISIBILITY);
        }
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        endEffectWhenPossible();
        return false;
    }
}
