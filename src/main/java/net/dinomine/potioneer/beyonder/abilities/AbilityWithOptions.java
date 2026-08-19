package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import static net.dinomine.potioneer.beyonder.abilities.AbilityOptionsUtil.validadeArguments;

public abstract class AbilityWithOptions extends Ability {
    private AbilityOptions primaryOptions = null;
    private AbilityOptions secondaryOptions = null;
    public AbilityOptions getPrimaryOptions(){return primaryOptions;}
    public AbilityOptions getSecondaryOptions(){return secondaryOptions;}

    protected void setPrimaryOptions(AbilityOptions primaryOptions){
        this.primaryOptions = primaryOptions;
    }

    protected void setSecondaryOptions(AbilityOptions secondaryOptions){
        this.secondaryOptions = secondaryOptions;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(primaryOptions == null) return primary(cap, target);

        String choice = validadeArguments(args, this, primaryOptions, target.level().isClientSide, true);
        if(choice.isEmpty()) return false;
        return primaryWithArgument(cap, target, choice);
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(secondaryOptions == null) return secondary(cap, target);

        String choice = validadeArguments(args, this, secondaryOptions, target.level().isClientSide, false);
        if(choice.isEmpty()) return false;
        return secondaryWithArgument(cap, target, choice);
    }
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args){return false;};
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity target, String args){return false;};
}
