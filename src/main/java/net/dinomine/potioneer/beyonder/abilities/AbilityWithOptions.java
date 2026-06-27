package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

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

    protected void addPrimaryOptions(AbilityOptions primaryOptions){
        this.primaryOptions = primaryOptions;
    }

    protected void addSecondaryOptions(AbilityOptions secondaryOptions){
        this.secondaryOptions = secondaryOptions;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(primaryOptions == null) return primary(cap, target);

        String choice = validadeArguments(args, primaryOptions, target.level().isClientSide);
        if(choice.isEmpty()) return false;
        return primaryWithArgument(cap, target, choice);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(secondaryOptions == null) return secondary(cap, target);

        String choice = validadeArguments(args, secondaryOptions, target.level().isClientSide);
        if(choice.isEmpty()) return false;
        return secondaryWithArgument(cap, target, choice);
    }

    private String validadeArguments(CompoundTag args, AbilityOptions options, boolean clientSide){
        //if nothing is selected, prompt choice
        if(!args.contains("option"))
            return promptChoice(options, clientSide);

        //if something is selected, verify its final
        String choice = args.getString("option");
        if(!isFinalOption(choice, options))
            return promptChoice(choice, options, clientSide);

        return choice;
    }

    private boolean isFinalOption(String choice, AbilityOptions options){
        return options.getPossibleFinalOptions().contains(choice);
    }

    private String promptChoice(AbilityOptions rootOptions, boolean clientSide){
        if(!clientSide || rootOptions == null) return "";
        //TODO: screen logic here.
        return "";
    }
    private String promptChoice(String choice, AbilityOptions rootOptions, boolean clientSide){
        if(!clientSide || rootOptions == null) return "";
        AbilityOptions choiceOption = findOptionWithName(rootOptions, choice);
        return promptChoice(choiceOption, true);
    }

    private AbilityOptions findOptionWithName(AbilityOptions rootOptions, String choice){
        //if the root options doesnt even define that choice as an option, return null
        if(!rootOptions.getPossibleOptions().contains(choice)) return null;

        AbilityOptions currentOption = rootOptions;
        boolean changedFlag;
        while (true) {
            if(currentOption.is(choice)) return currentOption;

            changedFlag = false;
            List<AbilityOptions> tempOptions = currentOption.getFurtherOptions();
            for (AbilityOptions opt : tempOptions) {
                if (!opt.getPossibleOptions().contains(choice)) continue;
                changedFlag = true;
                currentOption = opt;
                break;
            }
            if(!changedFlag) return null;
        }
    }

    protected boolean secondaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args){return false;};
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args){return false;};
}
