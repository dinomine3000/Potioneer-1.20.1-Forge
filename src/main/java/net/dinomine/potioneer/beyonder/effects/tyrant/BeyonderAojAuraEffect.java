package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class BeyonderAojAuraEffect extends BeyonderEffect {
    private static final int auraRadius = 16;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || !(target instanceof Player player)) return;
        cap.requestPassiveSpiritualityCost(cost);
        if(target.tickCount%20 != target.getId()%20) return;
        target.level().getEntities(target, target.getBoundingBox().inflate(auraRadius)).forEach(ent -> applyAuraEffects(ent, cap, target));
    }

    private static void applyAuraEffects(Entity entity, LivingEntityBeyonderCapability cap, LivingEntity enforcer){
        if(!(entity instanceof LivingEntity livingEntity) || !(enforcer instanceof Player enforcerPlayer)) return;
        if(!AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(livingEntity, enforcer)) return;
        enforcer.level().playSound(enforcer, livingEntity.getOnPos(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 1, 1);
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20*10, 0, false, false, true));
        ParticleMaker.createAuraParticles(enforcerPlayer, livingEntity);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        if(attacker == null || victim.level().isClientSide() || !calledOnVictim) return false;
        if(attackerCap.isBeyonder() && victimCap.getSequenceLevel() > attackerCap.getSequenceLevel()) return false;
        event.setAmount(event.getAmount()/2f);
        if(attacker instanceof Mob mob && (mob.getLastAttacker() == null || !mob.getLastAttacker().is(victim)) && mob.getTarget() != null && mob.getTarget().is(victim) && mob.getMaxHealth() < victim.getHealth()){
            mob.setTarget(null);
            mob.setLastHurtMob(null);
            mob.setLastHurtByMob(null);
            mob.setLastHurtByPlayer(null);
        }
        return false;
    }
}
