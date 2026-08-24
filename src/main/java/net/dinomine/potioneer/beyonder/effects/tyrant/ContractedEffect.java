package net.dinomine.potioneer.beyonder.effects.tyrant;

import lombok.Getter;
import lombok.Setter;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractAbility.ContractOption;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ContractViewAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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

    private ContractOption condition;
    private ContractOption reward;
    @Setter
    @Getter
    private UUID casterId = null;
    private int time = 0;
    public int getTime(){return time;}


    @Override
    public boolean canBeCleansed() {
        return false;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        super.onAcquire(cap, target);
        if(reward.getId().equalsIgnoreCase("ability")){
            manageAbilityBuffs(cap, target, true);
        }

        if(target.level().isClientSide()) return;
        ContractViewAbility abl = (ContractViewAbility) Abilities.CONTRACT_VIEW.get().construct(0, AbilityInfo.Group.CONTRACT);
        abl.setConditions(condition, reward);
        if(!cap.getAbilitiesManager().addAndInitializeAbility(abl, cap, target, false, true)){
            Potioneer.LOGGER.warn("Important: On activating ContractedEffect, the ability to view it failed to be added! This might have happened because another ContractedEffect exists, or because the ability failed to be removed previously. Please report this bug if you find it.");
            System.out.println("Warning: Failed to add Contract Viewer Ability. Either another effect already exists or the ability failed to be removed before.-");
        }
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(Objects.equals(condition, ContractOption.HP_COND)){
            if(target.getHealth() < HEALTH_THRESHOLD.get()) invalidate(target, cap);
        }
        else if(Objects.equals(condition, ContractOption.NETHER_COND) && Objects.equals(target.level().dimension(), Level.NETHER)) invalidate(target, cap);
        else if(Objects.equals(condition, ContractOption.SPIRITUALITY_COND) && cap.getSpirituality() < cap.getMaxSpirituality()*SPIRITUALITY_THRESHOLD.get()) invalidate(target, cap);
        if(!condition.isValid()) return;

        if(time <= 20*60*60) time++;
        BeyonderStats statsHolder = cap.getEffectsManager().statsHolder;
        if(Objects.equals(reward, ContractOption.DAMAGE_BUFF)) statsHolder.addDamage(DAMAGE_BUFF.get());
        else if(Objects.equals(reward, ContractOption.REGENERATION_BUFF)) statsHolder.addRegeneration(REGENERATION_BUFF.get());
        else if(Objects.equals(reward, ContractOption.HEALTH_BUFF)) statsHolder.addHealth(HEALTH_BUFF.get());
        else if(Objects.equals(reward, ContractOption.STAMINA_BUFF)) statsHolder.addStamina(STAMINA_BUFF.get());
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(reward.getId().equalsIgnoreCase("ability")) manageAbilityBuffs(cap, target, false);

        //we filter to server side to make sure nothing weird happens, since ability management is mainly handled by server.
        if(target.level().isClientSide()) return;
        //cap.getAbilitiesManager().removeAbility(generateKeyForViewerAbility(), cap, target, true);
        cap.getAbilitiesManager().removeFirstAbilityLike(Abilities.CONTRACT_VIEW.getId(), AbilityInfo.Group.CONTRACT, cap, target, true);
    }

    private void manageAbilityBuffs(BeyonderCapability cap, LivingEntity target, boolean doBuff){
        if(!reward.getId().equalsIgnoreCase("ability")) return;

        ResourceLocation ablId = new ResourceLocation(reward.getArguments().get(0));
        cap.getAbilitiesManager().getAllAbilities(ablId).forEach(abl ->  {
            if(doBuff)
                abl.temporarilyUpgradeToLevel(SEQUENCE_LEVEL_UPGRADE_ID, -1, cap, target);
            else
                abl.removeTemporaryUpgrade(SEQUENCE_LEVEL_UPGRADE_ID, cap, target);
        });
    }

    public static ContractedEffect getInstance(ContractOption condition, ContractOption reward, UUID casterId){
        ContractedEffect eff = (ContractedEffect) BeyonderEffects.TYRANT_CONTRACT.createInstance(0, 0, -1, true);
        eff.setConditions(condition, reward);
        eff.setCasterId(casterId);
        return eff;
    }

    public void setConditions(ContractOption condition, ContractOption reward){
        this.condition = condition;
        this.reward = reward;
    }

    public void testAbilityCast(Ability abl, BeyonderCapability cap, LivingEntity target){
        if(condition.getId().equalsIgnoreCase("ability_cond")){
            if(!condition.isValid()) return;
            for(String ablArg: condition.getArguments()){
                ContractAbility.ContractAbilityOption contractOption = ContractAbility.ContractAbilityOption.fromString(ablArg);
                if(contractOption == null){
                    System.out.println("Error: Failed to parse Contract Ability Option: " + ablArg);
                    continue;
                }
                if(abl.getAbilityId().equals(contractOption.getAblId())){
                    endEffectWhenPossible();
                    return;
                }
            }
        }
        if(!condition.isValid() || !reward.getId().equalsIgnoreCase("ability")) return;

        ContractAbility.ContractAbilityOption rewardOption = ContractAbility.ContractAbilityOption.fromString(reward.getArguments().get(0));
        if(rewardOption == null){
            System.out.println("Error: Failed to parse Contract Ability Option: " + reward.getArguments().get(0));
            return;
        }
        if(abl.is(rewardOption.getAblId())){
            abl.temporarilyUpgradeToLevel(SEQUENCE_LEVEL_UPGRADE_ID, -1, cap, target);
        }
    }

    private void invalidate(LivingEntity target, BeyonderCapability cap){
        condition.markInvalid();
        endEffectWhenPossible();
        RulePylonAbility.Punishment.STRIKE.execution().execute(target, cap, null, sequenceLevel);
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim) return false;
        if(Objects.equals(victim.getMobType(), MobType.UNDEAD) && Objects.equals(condition, ContractOption.UNDEAD_COND)) invalidate(attacker, attackerCap.orElse(null));
        return false;
    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
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
        nbt.putInt("time", time);
        if (this.casterId != null) {
            nbt.putUUID("casterId", this.casterId);
        }
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.condition = ContractAbility.ContractOption.loadFromNbt(nbt.getCompound("condition")).get();
        this.reward = ContractAbility.ContractOption.loadFromNbt(nbt.getCompound("reward")).get();
        this.time = nbt.getInt("time");
        if (nbt.hasUUID("casterId")) {
            this.casterId = nbt.getUUID("casterId");
        }
    }
}