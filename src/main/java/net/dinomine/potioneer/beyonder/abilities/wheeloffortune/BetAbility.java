package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderGamblingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class BetAbility extends Ability {
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public BetAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "bet";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost() || target.level().isClientSide()) return false;
        cap.requestActiveSpiritualityCost(cost());
        int luck = cap.getLuckManager().getLuck();
        int minDuration = 25;
        int maxDuration = 300;
        int maxLevel = 1;
        float multiplier = 1f + Math.max(luck, 0)/150f;
        BeyonderGamblingEffect.applyPositiveEffect(cap, target, cap.getLuckManager(), getSequenceLevel(), (int)(multiplier*minDuration), (int)(multiplier*maxDuration), (int)(multiplier*maxLevel), target.getRandom());
        cap.getAbilitiesManager().putAbilityOnCooldown(Abilities.GAMBLING.getAblId(), getSequenceLevel(), GamblingAbility.cd, target);
        cap.getLuckManager().consumeLuck(Math.max(luck, 100));
        return true;
    }
}
