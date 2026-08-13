package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AuraAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class AuraSourceEffect extends BeyonderEffect {
    private static final Supplier<Integer> AURA_RADIUS = PotioneerAbilityConfig.AURA_RADIUS;
    private int wordCooldown = 0;
    private UUID abilityInstance = null;
    public boolean getWordCooldown(int newCooldown, LivingEntity target, BeyonderCapability cap){
        boolean res = wordCooldown < 1;
        wordCooldown += newCooldown;
        if(res) return true;

        cap.getAbilitiesManager().getAbilities(Abilities.TYRANT_AURA.getAblId()).forEach(abl -> {
            abl.putOnCooldown(20*20, target);
            abl.setEnabled(cap, target, false);
        });
        return false;
    }

    public void setCooldown(int cooldown, UUID abilityId){this.wordCooldown = cooldown;abilityInstance = abilityId;}
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
        if(sequenceLevel > 5) return;
        if(wordCooldown > 0) wordCooldown--;
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
        Ability abl = cap.getAbilitiesManager().getAbilityInstance(abilityInstance);
        if(abl instanceof AuraAbility auraAbility){
            auraAbility.storeCooldown(wordCooldown, target);
        }
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

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("aura_cooldown", wordCooldown);
        nbt.putUUID("instanceId", abilityInstance);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.wordCooldown = nbt.getInt("aura_cooldown");
        this.abilityInstance = nbt.getUUID("instanceId");
    }

    public boolean validateMessage(String rawText) {
        if(sequenceLevel > 5) return false;
        return rawText.equalsIgnoreCase("execution")
                || rawText.equalsIgnoreCase("exorcism")
                || rawText.equalsIgnoreCase("stand back");
    }

    public void execute(String word, LivingEntity target, BeyonderCapability optCap){
        List<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(target, 8);
        switch(word.toLowerCase()){
            case "execution":
                for(LivingEntity hit: hits){
                    if(hit.getHealth() < hit.getMaxHealth()*0.2) hit.hurt(PotioneerDamage.execution((ServerLevel) target.level(), target), Integer.MAX_VALUE);
                    else {
                        hit.hurt(PotioneerDamage.execution((ServerLevel) target.level(), target), 15);
                    }
                }
                break;
            case "exorcism":
                List<LivingEntity> undeads = AbilityFunctionHelper.getLivingEntitiesAround(target, 8, ent -> ent.getMobType().equals(MobType.UNDEAD));
                for(LivingEntity hit: undeads){
                    if(hit.getHealth() < hit.getMaxHealth()*0.2) hit.hurt(PotioneerDamage.tyrant_purification((ServerLevel) target.level(), target), Integer.MAX_VALUE);
                    else {
                        hit.hurt(PotioneerDamage.tyrant_purification((ServerLevel) target.level(), target), 15*3);
                    }
                }
                break;
            case "stand back":
                for(LivingEntity hit: hits){
                    ArrestSourceEffect.applyArrestToRecipient(target, hit, sequenceLevel, true);
                }
                break;
        }
    }
}
