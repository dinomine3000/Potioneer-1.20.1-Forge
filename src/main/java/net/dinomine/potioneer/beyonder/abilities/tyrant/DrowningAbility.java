package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;

public class DrowningAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public DrowningAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "drowning";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost()){
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
            int duration = 20*10*(10-sequenceLevel);
            ArrayList<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround((ServerLevel) target.level(), target, radius);
            //hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
            for(LivingEntity entity: hits){
                if(entity.is(target)) continue;
                entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap ->
                        victimCap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_DROWNING.createInstance(getSequenceLevel(), 0, duration, true), victimCap, entity));
            }
            ParticleMaker.summonAOEParticles(target.level(), target.getEyePosition(), (int)(2*radius), radius, ParticleMaker.Preset.AOE_END_ROD);
            //target.level().playSound(null, target.getOnPos(), SoundEvents.MINECART_INSIDE_UNDERWATER, SoundSource.PLAYERS, 1, 1);
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }
}
