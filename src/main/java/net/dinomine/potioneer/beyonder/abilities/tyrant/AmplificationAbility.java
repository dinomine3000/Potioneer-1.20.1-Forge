package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class AmplificationAbility extends AbilityWithOptions {
    private static final int EFFECT_DURATION = 20*60;
    public AmplificationAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*70;
        AbilityOptions options = new AbilityOptions()
                .addEmptyOption("ability", Component.literal("Buff Ability Levels"))
                .addEmptyOption("stats", Component.literal("Buff Stats"));
        setPrimaryOptions(options);
        setSecondaryOptions(options);
        withCost(100);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "amplification";
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity caster, String args){
        if(cap.getSpirituality() < cost()) return false;
        if(caster.level().isClientSide) return false;
        boolean buffAbility = args.equalsIgnoreCase("ability");
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 2, 1);
        if(target == null) return false;

        applyEffectsTo(caster, target, target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get(), sequenceLevel, buffAbility);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }

    @Override
    protected boolean secondaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity caster, String args) {
        if(cap.getSpirituality() < cost()) return false;
        if(caster.level().isClientSide) return false;
        boolean buffAbility = args.equalsIgnoreCase("ability");
        applyEffectsTo(caster, caster, cap, sequenceLevel, buffAbility);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }

    private static void applyEffectsTo(LivingEntity caster, LivingEntity target, LivingEntityBeyonderCapability targetCap, int sequenceLevel, boolean buffAbilities){
        if(AbilityFunctionHelper.areEntitiesAllies(caster, target)){
            AmplificationEffect amp = (AmplificationEffect) BeyonderEffects.TYRANT_AMPLIFICATION.createInstance(sequenceLevel, 0, EFFECT_DURATION, true);
            if(buffAbilities) amp.setAmplificationsLeft(sequenceLevel > 6 ? 1 : 3);
            targetCap.getEffectsManager().addEffectNoRefresh(amp, targetCap, target);
        } else {
            WeakeningEffect weaken = (WeakeningEffect) BeyonderEffects.TYRANT_WEAKENING.createInstance(sequenceLevel, 0, EFFECT_DURATION, true);
            if(buffAbilities) weaken.setWeakeningsLeft(sequenceLevel > 6 ? 1 : 3);
            targetCap.getEffectsManager().addEffectNoRefresh(weaken, targetCap, target);
        }
    }
}
