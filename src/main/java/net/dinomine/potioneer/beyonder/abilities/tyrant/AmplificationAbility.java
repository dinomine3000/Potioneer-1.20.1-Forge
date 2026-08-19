package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Supplier;

public class AmplificationAbility extends AbilityWithOptions {
    private static final Supplier<Integer> EFFECT_DURATION = PotioneerAbilityConfig.AMPLIFICATION_DURATION;
    private int cost = 0;

    @Override
    public void init() {
        super.init();
        defaultMaxCooldown = EFFECT_DURATION.get() + 20;
        AbilityOptions options = new AbilityOptions()
                .addEmptyOption("ability", Component.translatable("abilityoption.potioneer.amplify_ability"))
                .addEmptyOption("stats", Component.translatable("abilityoption.potioneer.amplify_stats"));
        setPrimaryOptions(options);
        setSecondaryOptions(options);
        cost = PotioneerAbilityConfig.AMPLIFICATION_COST.get();
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "amplification";
    }

    @Override
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity caster, String args){
        if(cap.getSpirituality() < cost) return false;
        if(caster.level().isClientSide) return false;
        boolean buffAbility = args.equalsIgnoreCase("ability");
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 2, 1);
        if(target == null) return false;

        applyEffectsTo(caster, target, target.getCapability(CapProvider.BEYONDER_STATS).resolve().get(), getSequenceLevel(), buffAbility);
        cap.requestActiveSpiritualityCost(cost);
        return true;
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity caster, String args) {
        if(cap.getSpirituality() < cost) return false;
        if(caster.level().isClientSide) return false;
        boolean buffAbility = args.equalsIgnoreCase("ability");
        applyEffectsTo(caster, caster, cap, getSequenceLevel(), buffAbility);
        cap.requestActiveSpiritualityCost(cost);
        return true;
    }

    private static void applyEffectsTo(LivingEntity caster, LivingEntity target, BeyonderCapability targetCap, int sequenceLevel, boolean buffAbilities){
        if(AbilityFunctionHelper.areEntitiesAllies(caster, target)){
            AmplificationEffect amp = (AmplificationEffect) BeyonderEffects.TYRANT_AMPLIFICATION.createInstance(sequenceLevel, 0, EFFECT_DURATION.get(), true);
            if(buffAbilities) amp.setAmplificationsLeft(sequenceLevel > 6 ? 1 : 3);
            targetCap.getEffectsManager().addEffectNoRefresh(amp, targetCap, target);
        } else {
            WeakeningEffect weaken = (WeakeningEffect) BeyonderEffects.TYRANT_WEAKENING.createInstance(sequenceLevel, 0, EFFECT_DURATION.get(), true);
            if(buffAbilities) weaken.setWeakeningsLeft(sequenceLevel > 6 ? 1 : 3);
            targetCap.getEffectsManager().addEffectNoRefresh(weaken, targetCap, target);
        }
    }
}
