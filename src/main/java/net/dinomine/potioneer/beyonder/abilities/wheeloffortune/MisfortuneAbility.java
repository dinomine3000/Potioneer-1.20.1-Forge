package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.Optional;

public class MisfortuneAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public MisfortuneAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || cap.getSpirituality() < cost()) return false;
        Optional<LivingEntity> misfortuneTarget = AbilityFunctionHelper.getTargetEntity(target, target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + 1, false);
        if(misfortuneTarget.isPresent()){
            LivingEntityBeyonderCapability targetCap = misfortuneTarget.get().getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            targetCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.WHEEL_INSTANT_BAD_LUCK.createInstance(getSequenceLevel(), 0, 5, true), targetCap, misfortuneTarget.get());
            cap.getLuckManager().consumeLuck(50);
            cap.requestActiveSpiritualityCost(cost());
            ParticleMaker.createDiceEffectForEntity(target.level(), misfortuneTarget.get());
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.MISFORTUNE_ACTING_INC, 5);
        }
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || cap.getSpirituality() < cost()) return false;
        Optional<LivingEntity> misfortuneTarget = AbilityFunctionHelper.getTargetEntity(target, target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + 1, false);
        if(misfortuneTarget.isPresent()){
            LivingEntityBeyonderCapability targetCap = misfortuneTarget.get().getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
            targetCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.WHEEL_BAD_LUCK.createInstance(getSequenceLevel(), 0, 5, true), targetCap, misfortuneTarget.get());
            cap.getLuckManager().consumeLuck(50);
            ParticleMaker.createDiceEffectForEntity(target.level(), misfortuneTarget.get());
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.MISFORTUNE_ACTING_INC, 5);
            cap.requestActiveSpiritualityCost(cost());
        }
        return true;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "misfortune";
    }
}
