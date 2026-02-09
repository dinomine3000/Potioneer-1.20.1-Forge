package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.DamageRecordingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class DamageRecordingAbility extends PassiveAbility {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public DamageRecordingAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.WHEEL_DAMAGE_RECORDING, ignored -> "damage_recording");
        enabledOnAcquire();
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        DamageRecordingEffect effect = (DamageRecordingEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_DAMAGE_RECORDING.getEffectId(), getSequenceLevel());
        if(effect == null || effect.isRecording()) return false;
        float amount = effect.getRecordedDamage(false);
        if(target.isCrouching()){
            target.sendSystemMessage(Component.translatable("ability.potioneer.damage_recording_info", Math.round(amount)));
            return false;
        }
        effect.setRecording(target.level());
        target.sendSystemMessage(Component.translatable("ability.potioneer.damage_recording_start"));
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(cap.getSpirituality() < cost()) return false;
        DamageRecordingEffect effect = (DamageRecordingEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_DAMAGE_RECORDING.getEffectId(), getSequenceLevel());
        if(effect == null) return false;
        float amount = effect.getRecordedDamage(false);
        if(target.isCrouching()){
            target.sendSystemMessage(Component.translatable("ability.potioneer.damage_recording_info", Math.round(amount)));
            return false;
        }
        if(amount < 1 || effect.isRecording()) return false;
        AllySystemSaveData allyData = AllySystemSaveData.from(((ServerLevel) target.level()));
        List<LivingEntity> targets = AbilityFunctionHelper.getLivingEntitiesAround(target, 8, ent -> !allyData.areEntitiesAllies(target, ent));
        for(LivingEntity victim: targets){
            victim.hurt(PotioneerDamage.crit((ServerLevel) target.level(), target), effect.getRecordedDamage(true));
        }
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }
}
