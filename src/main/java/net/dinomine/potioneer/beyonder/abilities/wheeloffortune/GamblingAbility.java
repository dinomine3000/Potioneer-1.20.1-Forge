package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class GamblingAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public GamblingAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 4*60*20 + 30*20;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "gambling";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestActiveSpiritualityCost(cost());
        cap.getEffectsManager().addEffectNoRefresh(BeyonderEffects.WHEEL_GAMBLING.createInstance(getSequenceLevel(), 0, 2, true),
                cap, target);
        return true;
    }
}
