package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.client.screen.AbilityOptionsScreen;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.client.Minecraft;
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

        String choice = validadeArguments(args, primaryOptions, target.level().isClientSide, true);
        if(choice.isEmpty()) return false;
        return primaryWithArgument(cap, target, choice);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(secondaryOptions == null) return secondary(cap, target);

        String choice = validadeArguments(args, secondaryOptions, target.level().isClientSide, false);
        if(choice.isEmpty()) return false;
        return secondaryWithArgument(cap, target, choice);
    }

    private String validadeArguments(CompoundTag args, AbilityOptions options, boolean clientSide, boolean castPrimary){
        //if nothing is selected, prompt choice
        if(!args.contains("option"))
            return promptChoice(options, clientSide, castPrimary);

        //if something is selected, verify its final
        String choice = args.getString("option");
        if(!isFinalOption(choice, options))
            return promptChoice(choice, options, clientSide, castPrimary);

        return choice;
    }

    private boolean isFinalOption(String choice, AbilityOptions options){
        return options.getPossibleFinalOptions().contains(choice);
    }

    private String promptChoice(AbilityOptions rootOptions, boolean clientSide, boolean castPrimary){
        if(!clientSide || rootOptions == null) return "";

        Minecraft.getInstance().tell(() -> {
            Minecraft.getInstance().setScreen(new AbilityOptionsScreen(rootOptions, this.abilityKey, castPrimary));
        });
        return "";
    }
    private String promptChoice(String choice, AbilityOptions rootOptions, boolean clientSide, boolean castPrimary){
        if(!clientSide || rootOptions == null) return "";
        AbilityOptions choiceOption = findOptionWithName(rootOptions, choice);
        return promptChoice(choiceOption, true, castPrimary);
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
