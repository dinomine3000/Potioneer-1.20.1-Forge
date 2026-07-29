package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.client.screen.AbilityOptionsScreen;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class AbilityOptionsUtil {

    /**
     * Method to validade arguments given to an ability cast against the given AbilityOptions instance.
     * @param args Argument compound tag
     * @param abl Ability instance
     * @param options AbilityOptions instance
     * @param clientSide Whether its called on client side or not
     * @param castPrimary Whether it's based on secondary or primary cast
     * @return a final option/choice, or empty string if nothing found.
     */
    public static String validadeArguments(CompoundTag args, Ability abl, AbilityOptions options, boolean clientSide, boolean castPrimary){
        if(options == null) return "";
        //if nothing is selected, prompt choice
        if(options.getPossibleFinalOptions().size() == 1) return options.getPossibleFinalOptions().get(0);
        if(!args.contains("option"))
            return promptChoice(options, abl, clientSide, castPrimary);

        //if something is selected, verify its final
        String choice = args.getString("option");
        if(!isFinalOption(choice, options))
            return promptChoice(choice, abl, options, clientSide, castPrimary);

        return choice;
    }

    private static boolean isFinalOption(String choice, AbilityOptions options){
        return options.getPossibleFinalOptions().contains(choice);
    }

    private static String promptChoice(AbilityOptions rootOptions, Ability abl, boolean clientSide, boolean castPrimary){
        if(!clientSide || rootOptions == null) return "";

        AbilityOptionsScreen.start(rootOptions, abl, castPrimary);
        return "";
    }
    private static String promptChoice(String choice, Ability abl, AbilityOptions rootOptions, boolean clientSide, boolean castPrimary){
        if(!clientSide || rootOptions == null) return "";
        AbilityOptions choiceOption = findOptionWithName(rootOptions, choice);
        return promptChoice(choiceOption, abl, true, castPrimary);
    }

    private static AbilityOptions findOptionWithName(AbilityOptions rootOptions, String choice){
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

}
