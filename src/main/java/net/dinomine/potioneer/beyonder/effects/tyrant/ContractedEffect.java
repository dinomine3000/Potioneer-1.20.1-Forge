package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility.ContractOption;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractViewAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public class ContractedEffect extends BeyonderEffect {
    private static final Supplier<Float> SPIRITUALITY_THRESHOLD =
            () -> PotioneerAbilityConfig.CONTRACT_SPIRITUALITY_THRESHOLD.get().floatValue();
    private static final Supplier<Integer> HEALTH_THRESHOLD =
            PotioneerAbilityConfig.CONTRACT_HEALTH_THRESHOLD;

    private static final Supplier<Integer> DAMAGE_BUFF =
            PotioneerAbilityConfig.CONTRACT_DAMAGE_BUFF;
    private static final Supplier<Float> REGENERATION_BUFF =
            () -> PotioneerAbilityConfig.CONTRACT_REGENERATION_BUFF.get().floatValue();
    private static final Supplier<Float> STAMINA_BUFF =
            () -> PotioneerAbilityConfig.CONTRACT_STAMINA_BUFF.get().floatValue();
    private static final Supplier<Integer> HEALTH_BUFF =
            PotioneerAbilityConfig.CONTRACT_HEALTH_BUFF;

    private static final UUID SEQUENCE_LEVEL_UPGRADE_ID = UUID.fromString("eb93c3f3-9d5b-4c61-9f36-507379ce9e41");
    private static final String VIEWER_GROUP = "contract_view";

    private ContractOption condition;
    private ContractOption reward;

    private AbilityKey generateKeyForViewerAbility(){
        return new AbilityKey(VIEWER_GROUP, Abilities.CONTRACT_VIEW.getAblId(), 0);
    }

    @Override
    public boolean canBeCleansed() {
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.onAcquire(cap, target);
        if(reward.getId().equalsIgnoreCase("ability")){
            manageAbilityBuffs(cap, target, true);
        }

        if(target.level().isClientSide()) return;
        ContractViewAbility abl = (ContractViewAbility) Abilities.CONTRACT_VIEW.create(0);
        abl.setConditions(condition, reward);
        if(!cap.getAbilitiesManager().addAbility(generateKeyForViewerAbility(), abl, cap, target, false, true)){
            Potioneer.LOGGER.warn("Important: On activating ContractedEffect, the ability to view it failed to be added! This might have happened because another ContractedEffect exists, or because the ability failed to be removed previously. Please report this bug if you find it.");
            System.out.println("Warning: Failed to add Contract Viewer Ability. Either another effect already exists or the ability failed to be removed before.-");
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(Objects.equals(condition, ContractOption.HP_COND)){
            if(target.getHealth() < HEALTH_THRESHOLD.get()) invalidate();
        }
        else if(Objects.equals(condition, ContractOption.NETHER_COND) && Objects.equals(target.level().dimension(), Level.NETHER)) invalidate();
        else if(Objects.equals(condition, ContractOption.SPIRITUALITY_COND) && cap.getSpirituality() < cap.getMaxSpirituality()*SPIRITUALITY_THRESHOLD.get()) invalidate();
        if(!condition.isValid()) return;

        BeyonderStats statsHolder = cap.getEffectsManager().statsHolder;
        if(Objects.equals(reward, ContractOption.DAMAGE_BUFF)) statsHolder.addDamage(DAMAGE_BUFF.get());
        else if(Objects.equals(reward, ContractOption.REGENERATION_BUFF)) statsHolder.addRegeneration(REGENERATION_BUFF.get());
        else if(Objects.equals(reward, ContractOption.HEALTH_BUFF)) statsHolder.addHealth(HEALTH_BUFF.get());
        else if(Objects.equals(reward, ContractOption.STAMINA_BUFF)) statsHolder.addStamina(STAMINA_BUFF.get());
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(reward.getId().equalsIgnoreCase("ability")) manageAbilityBuffs(cap, target, false);

        //we filter to server side to make sure nothing weird happens, since ability management is mainly handled by server.
        if(target.level().isClientSide()) return;
        cap.getAbilitiesManager().removeAbility(generateKeyForViewerAbility(), cap, target, true);
        //cap.getAbilitiesManager().removeFirstAbilityLike(Abilities.CONTRACT_VIEW.getAblId(), VIEWER_GROUP, cap, target, true);
    }

    private void manageAbilityBuffs(LivingEntityBeyonderCapability cap, LivingEntity target, boolean doBuff){
        if(!reward.getId().equalsIgnoreCase("ability")) return;

        String ablId = AbilityKey.fromString(reward.getArguments().get(0)).getAbilityId();
        cap.getAbilitiesManager().getAbilities(ablId).forEach(abl ->  {
            if(doBuff)
                abl.temporarilyUpgradeToLevel(SEQUENCE_LEVEL_UPGRADE_ID, -1, cap, target);
            else
                abl.removeTemporaryUpgrade(SEQUENCE_LEVEL_UPGRADE_ID, cap, target);
        });
    }

    public static ContractedEffect getInstance(ContractOption condition, ContractOption reward){
        ContractedEffect eff = (ContractedEffect) BeyonderEffects.TYRANT_CONTRACT.createInstance(0, 0, -1, true);
        eff.setConditions(condition, reward);
        return eff;
    }

    public void setConditions(ContractOption condition, ContractOption reward){
        this.condition = condition;
        this.reward = reward;
    }

    public void testAbilityCast(Ability abl, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(condition.getId().equalsIgnoreCase("ability_cond")){
            if(!condition.isValid()) return;
            for(String ablArg: condition.getArguments()){
                AbilityKey key = AbilityKey.fromString(ablArg);
                if(key.isSameAbility(abl.getAbilityId())){
                    endEffectWhenPossible();
                    return;
                }
            }
        }
        if(!condition.isValid() || !reward.getId().equalsIgnoreCase("ability")) return;

        if(abl.is(reward.getArguments().get(0))){
            abl.temporarilyUpgradeToLevel(SEQUENCE_LEVEL_UPGRADE_ID, -1, cap, target);
        }
    }

    private void invalidate(){
        condition.markInvalid();
        endEffectWhenPossible();
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim) return false;
        if(Objects.equals(victim.getMobType(), MobType.UNDEAD) && Objects.equals(condition, ContractOption.UNDEAD_COND)) invalidate();
        return false;
    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim) return false;
        if(!condition.isValid()) return false;
        if(Objects.equals(condition, ContractOption.UNDEAD_BUFF) && Objects.equals(victim.getMobType(), MobType.UNDEAD)) event.setAmount(event.getAmount()*2);
        return false;
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.put("condition", condition.saveToNbt());
        nbt.put("reward", reward.saveToNbt());
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.condition = ContractAbility.ContractOption.loadFromNbt(nbt.getCompound("condition")).get();
        this.reward = ContractAbility.ContractOption.loadFromNbt(nbt.getCompound("reward")).get();
    }
}