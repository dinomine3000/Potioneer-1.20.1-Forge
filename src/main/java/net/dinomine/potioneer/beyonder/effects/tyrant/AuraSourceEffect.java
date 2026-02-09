package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Optional;

public class AuraSourceEffect extends BeyonderEffect {
    private static final int auraRadius = 16;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || !(target instanceof Player player)) return;
        cap.requestPassiveSpiritualityCost(cost);
        if(target.tickCount%20 != target.getId()%20) return;
        target.level().getEntities(target, target.getBoundingBox().inflate(auraRadius)).forEach(ent -> applyAuraEffects(ent, target));
    }

    private void applyAuraEffects(Entity entity, LivingEntity enforcer){
        if(!(entity instanceof LivingEntity livingEntity)) return;
        if(AllySystemSaveData.isAllies(livingEntity, enforcer)) return;
        Optional<LivingEntityBeyonderCapability> optCap = livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty()) return;
        LivingEntityBeyonderCapability cap = optCap.get();
        AuraRecipientEffect eff = (AuraRecipientEffect) BeyonderEffects.TYRANT_AURA_RECIPIENT.createInstance(getSequenceLevel(), 0, -1, true);
        eff.addSourceSilent(enforcer.getUUID());
        cap.getEffectsManager().addOrReplaceEffect(eff, cap, livingEntity);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        if(attacker == null || victim.level().isClientSide() || !calledOnVictim) return false;
        if(attackerCap.isBeyonder() && victimCap.getSequenceLevel() > attackerCap.getSequenceLevel()) return false;
        if(!AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(attacker, victim)) return false;
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
