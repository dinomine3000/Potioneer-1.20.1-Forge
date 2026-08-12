package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.Optional;
import java.util.function.Supplier;

public class AuraSourceEffect extends BeyonderEffect {
    private static final Supplier<Integer> AURA_RADIUS = PotioneerAbilityConfig.AURA_RADIUS;
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || !(target instanceof Player player)) return;
        cap.requestPassiveSpiritualityCost(cost);
        if(target.tickCount%20 != target.getId()%20) return;
        target.level().getEntities(target, target.getBoundingBox().inflate(AURA_RADIUS.get())).forEach(ent -> applyAuraEffects(ent, target));
        //applyAuraEffects(target, target);
    }

    private void applyAuraEffects(Entity entity, LivingEntity enforcer){
        if(!(entity instanceof LivingEntity livingEntity)) return;
        if(AbilityFunctionHelper.areEntitiesAllies(livingEntity, enforcer)) return;
        Optional<BeyonderCapability> optCap = livingEntity.getCapability(CapProvider.BEYONDER_STATS).resolve();
        Optional<BeyonderCapability> optCapEnforcer = enforcer.getCapability(CapProvider.BEYONDER_STATS).resolve();
        if(optCap.isEmpty() || optCapEnforcer.isEmpty()) return;
        if(!AreaOfJurisdictionAbility.isEntityInAOJ(livingEntity, enforcer)) return;
        BeyonderCapability cap = optCap.get();
        AuraRecipientEffect eff = (AuraRecipientEffect) BeyonderEffects.TYRANT_AURA_RECIPIENT.createInstance(getSequenceLevel(), 0, -1, true);
        eff.addSourceSilent(enforcer.getUUID());
        cap.getEffectsManager().addOrRefreshEffect(eff, cap, livingEntity);
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(attacker == null || victim.level().isClientSide() || !calledOnVictim) return false;
        if(attackerCap.isEmpty()) return false;
        if(attackerCap.get().isBeyonder() && victimCap.getSequenceLevel() > attackerCap.get().getSequenceLevel()) return false;
        if(!AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(attacker, victim)) return false;
        event.setAmount(event.getAmount()/2f);
        if(attacker instanceof Mob mob && (mob.getLastAttacker() == null || !mob.getLastAttacker().is(victim)) && mob.getTarget() != null && mob.getTarget().is(victim) && mob.getMaxHealth() < victim.getHealth()){
            mob.setTarget(null);
            mob.setLastHurtByMob(null);
            mob.setLastHurtByPlayer(null);
        }
        return false;
    }
}
