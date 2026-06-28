package net.dinomine.potioneer.beyonder.abilities;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class AbilityOptions {
    private static final int MAX_OPTIONS = 6;
    private String option;
    private final List<AbilityOptions> furtherOptions = new ArrayList<>();
    public ResourceLocation textureLocation = null;
    public int textureX, textureY, sizeX, sizeY, textureWidth = 0, textureHeight = 0;
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

    public AbilityOptions(ResourceLocation parentTextureLocation, int textureWidth, int textureHeight){
        this();
        this.withParentTexture(parentTextureLocation, textureWidth, textureHeight);
    }

    public AbilityOptions addOption(String optionName, AbilityOptions option, Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY, int textureWidth, int textureHeight){
        if(furtherOptions.size() >= MAX_OPTIONS) return this;
        furtherOptions.add(option.withName(optionName).withTexture(name, textureLocation, posX, posY, sizeX, sizeY, textureWidth, textureHeight));
        return this;
    }

    public AbilityOptions addOption(String optionName, Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY, int textureWidth, int textureHeight){
        return addOption(optionName, new AbilityOptions(optionName), name, textureLocation, posX, posY, sizeX, sizeY, textureWidth, textureHeight);
    }
    public AbilityOptions addOption(String optionName, AbilityOptions option, Component name, int posX, int posY, int sizeX, int sizeY){
        return addOption(optionName, option, name, this.textureLocation, posX, posY, sizeX, sizeY, this.textureWidth, this.textureHeight);
    }
    public AbilityOptions addOption(String optionName, Component name, int posX, int posY, int sizeX, int sizeY){
        return addOption(optionName, name, this.textureLocation, posX, posY, sizeX, sizeY, this.textureWidth, this.textureHeight);
    }

    public AbilityOptions addEmptyOption(String optionName, AbilityOptions option, Component name){
        return addOption(optionName, option, name, null, 0, 0, 0, 0, 0, 0);
    }

    public AbilityOptions addEmptyOption(String optionName, Component name){
        return addOption(optionName, new AbilityOptions(optionName), name, null, 0, 0, 0, 0, 0, 0);
    }

    private AbilityOptions(String optionName){
        this.option = optionName;
    }

    private AbilityOptions withName(String optionName){
        this.option = optionName;
        return this;
    }

    private AbilityOptions withParentTexture(ResourceLocation textureLocation, int textureWidth, int textureHeight){
        this.textureLocation = textureLocation;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        return this;
    }

    private AbilityOptions withTexture(Component name, ResourceLocation textureLocation, int posX, int posY, int sizeX, int sizeY, int textureWidth, int textureHeight){
        this.nameComponent = name;
        this.textureLocation = textureLocation;
        this.textureX = posX;
        this.textureY = posY;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.textureHeight = textureHeight;
        this.textureWidth = textureWidth;
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
