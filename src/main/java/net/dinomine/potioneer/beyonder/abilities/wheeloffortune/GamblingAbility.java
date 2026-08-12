package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.GamblingEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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
        super(sequenceLevel, PotioneerAbilityConfig.GAMBLING_COOLDOWN.get());
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("quick", false);
        setDataSilent(tag);
        withCost(PotioneerAbilityConfig.GAMBLING_COST.get());
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return sequenceLevel < 6 ? "gambling_2" : "gambling_1";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        cap.requestActiveSpiritualityCost(cost());
        GamblingEffect eff = (GamblingEffect) BeyonderEffects.WHEEL_GAMBLING.createInstance(getSequenceLevel(), 0, 2, true);
        eff.setQuick(getData().getBoolean("quick"));
        cap.getEffectsManager().addEffectNoRefresh(eff, cap, target);
        return true;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        boolean newQuick = !getData().getBoolean("quick");
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("quick", newQuick);
        setData(tag, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.gambling_" + (newQuick ? "quick" : "slow")));
        setNextCooldownAs(20);
        return true;
    }
}
