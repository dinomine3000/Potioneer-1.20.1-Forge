package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderGamblingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class GamblingAbility extends Ability {
    private static final int cd = 60*20 + 30*20;
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public GamblingAbility(int sequenceLevel) {
        super(sequenceLevel);
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("quick", false);
        setDataSilent(tag);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "gambling";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        this.defaultMaxCooldown = cd;
        if(target.level().isClientSide()) return true;
        cap.requestActiveSpiritualityCost(cost());
        BeyonderGamblingEffect eff = (BeyonderGamblingEffect) BeyonderEffects.WHEEL_GAMBLING.createInstance(getSequenceLevel(), 0, 2, true);
        eff.setQuick(getData().getBoolean("quick"));
        cap.getEffectsManager().addEffectNoRefresh(eff, cap, target);
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        this.defaultMaxCooldown = 20;
        if(target.level().isClientSide()) return true;
        boolean newQuick = !getData().getBoolean("quick");
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("quick", newQuick);
        setData(tag, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.gambling_" + (newQuick ? "quick" : "slow")));
        return true;
    }
}
