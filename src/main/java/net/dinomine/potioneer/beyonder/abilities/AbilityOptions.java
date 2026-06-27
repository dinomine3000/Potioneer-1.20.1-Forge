package net.dinomine.potioneer.beyonder.abilities;

import java.util.ArrayList;
import java.util.List;

public class AbilityOptions {
    private static final int MAX_OPTIONS = 6;
    private String option;
    private final List<AbilityOptions> furtherOptions = new ArrayList<>();

    public AbilityOptions(){
        this.option = "root";
    }

    public AbilityOptions addOption(String optionName, AbilityOptions option){
        if(furtherOptions.size() >= MAX_OPTIONS) return this;
        furtherOptions.add(option.withName(optionName));
        return this;
    }

    public AbilityOptions addOption(String optionName){
        return addOption(new AbilityOptions(optionName));
    }

    private AbilityOptions addOption(AbilityOptions option){
        return addOption(option);
    }

    private AbilityOptions(String optionName){
        this.option = optionName;
    }

    private AbilityOptions withName(String optionName){
        this.option = optionName;
        return this;
    }

    private boolean isFinalOption(){
        return furtherOptions.isEmpty();
    }

    public List<String> getPossibleOptions(){
        if(isFinalOption()) return List.of(option);

        ArrayList<String> result = new ArrayList<>();
        result.add(option);
        for(AbilityOptions opt: furtherOptions){
            result.addAll(opt.getPossibleOptions());
        }
        return result;
    }

    public List<String> getPossibleFinalOptions(){
        if(isFinalOption()) return List.of(option);

        ArrayList<String> result = new ArrayList<>();
        for(AbilityOptions opt: furtherOptions){
            result.addAll(opt.getPossibleFinalOptions());
        }
        return result;
    }

    public boolean is(String choice) {
        return this.option.equals(choice);
    }

    public List<AbilityOptions> getFurtherOptions(){
        return furtherOptions;
    }
}
