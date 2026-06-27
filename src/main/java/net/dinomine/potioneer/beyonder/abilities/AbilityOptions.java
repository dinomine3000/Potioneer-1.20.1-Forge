package net.dinomine.potioneer.beyonder.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AbilityOptions {
    private static final int MAX_OPTIONS = 6;
    private String option;
    private final List<AbilityOptions> furtherOptions = new ArrayList<>();
    public ResourceLocation textureLocation;
    public int textureX, textureY, sizeX, sizeY;
    public Component nameComponent;

    public AbilityOptions(){
        this.option = "root";
        textureLocation = null;
        textureX = 0;
        textureY = 0;
        sizeX = 1;
        sizeY = 1;
        nameComponent = Component.literal("root");
    }

    public AbilityOptions addOption(String optionName, AbilityOptions option, Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY){
        if(furtherOptions.size() >= MAX_OPTIONS) return this;
        furtherOptions.add(option.withName(optionName).withTexture(name, textureLocation, posX, posY, sizeX, sizeY));
        return this;
    }

    public AbilityOptions addOption(String optionName, AbilityOptions option, Component name){
        return addOption(optionName, option, name, null, 0, 0, 0, 0);
    }

    public AbilityOptions addOption(String optionName, Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY){
        return addOption(optionName, new AbilityOptions(optionName), name, textureLocation, posX, posY, sizeX, sizeY);
    }

    public AbilityOptions addOption(String optionName, Component name){
        return addOption(optionName, new AbilityOptions(optionName), name, null, 0, 0, 0, 0);
    }

    private AbilityOptions(String optionName){
        this.option = optionName;
    }

    private AbilityOptions withName(String optionName){
        this.option = optionName;
        return this;
    }

    private AbilityOptions withTexture(Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY){
        this.nameComponent = name;
        this.textureLocation = textureLocation;
        this.textureX = posX;
        this.textureY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        return this;
    }

    public boolean isFinalOption(){
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

    public String name(){return option;}

    public List<AbilityOptions> getFurtherOptions(){
        return furtherOptions;
    }
}
