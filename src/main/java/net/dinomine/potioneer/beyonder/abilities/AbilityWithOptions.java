package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import static net.dinomine.potioneer.beyonder.abilities.AbilityOptionsUtil.validadeArguments;

public abstract class AbilityWithOptions extends Ability {
    private AbilityOptions primaryOptions = null;
    private AbilityOptions secondaryOptions = null;
    public AbilityOptions getPrimaryOptions(){return primaryOptions;}
    public AbilityOptions getSecondaryOptions(){return secondaryOptions;}
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public AbilityWithOptions(int sequenceLevel) {
        super(sequenceLevel);
    }
    public AbilityWithOptions(int sequenceLevel, int defaultCooldown) {
        super(sequenceLevel, defaultCooldown);
    }

    protected void setPrimaryOptions(AbilityOptions primaryOptions){
        this.primaryOptions = primaryOptions;
    }

    protected void setSecondaryOptions(AbilityOptions secondaryOptions){
        this.secondaryOptions = secondaryOptions;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(primaryOptions == null) return primary(cap, target);

        String choice = validadeArguments(args, this, primaryOptions, target.level().isClientSide, true);
        if(choice.isEmpty()) return false;
        return primaryWithArgument(cap, target, choice);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(secondaryOptions == null) return secondary(cap, target);

        String choice = validadeArguments(args, this, secondaryOptions, target.level().isClientSide, false);
        if(choice.isEmpty()) return false;
        return secondaryWithArgument(cap, target, choice);
    }
    protected boolean secondaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args){return false;};
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args){return false;};
}
