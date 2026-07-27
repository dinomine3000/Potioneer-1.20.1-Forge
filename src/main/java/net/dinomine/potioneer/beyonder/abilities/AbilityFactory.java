package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class AbilityFactory {
    private ResourceLocation textureLocation;
    private int posY;
    private String ablId;
    /**
     * base cost in spirituality for client checking. if the client has less than this spirituality, the ability wont trigger
     */
    private Function<Integer, Integer> costFunction;
    private Function<Integer, Boolean> secondaryCheck;
    private int pathwayId;
    private Function<Integer, Ability> createFunction;
    private boolean passive = false;
    private boolean active = true;

    public AbilityFactory(ResourceLocation textureLocation, int posY, int pathwayId, Function<Integer, Integer> costFunction, String ablId, Function<Integer, Ability> createFunction){
        this.costFunction = costFunction;
        this.textureLocation = textureLocation;
        this.posY = 32 + 24*posY;
        this.pathwayId = pathwayId;
        this.createFunction = createFunction;
        this.ablId = ablId;
        secondaryCheck = lvl -> false;
    }

    public AbilityFactory(int posY, int pathwayId, Function<Integer, Integer> costFunction, String ablId, Function<Integer, Ability> createFunction){
        this(new ResourceLocation(Potioneer.MOD_ID, "textures/gui/ability_icon_atlas.png"), posY, pathwayId, costFunction, ablId, createFunction);
    }

    public AbilityFactory hasSecondaryFunction(){
        return this.hasSecondaryFunction(9);
    }
    public AbilityFactory passiveAndActive(){
        this.active = true;
        this.passive = true;
        return this;
    }

    public AbilityFactory passive(){
        this.active = false;
        this.passive = true;
        return this;
    }

    public AbilityFactory hasSecondaryFunction(Function<Integer, Boolean> levelCheck){
        this.secondaryCheck = levelCheck;
        return this;
    }

    public AbilityFactory hasSecondaryFunction(int levelCheck){
        return this.hasSecondaryFunction(lvl -> lvl <= levelCheck);
    }

    public boolean getHasSecondaryFunction(int level){
        return this.secondaryCheck.apply(level);
    }

    public int getPosY() {
        return posY;
    }

    public String getAblId() {
        return ablId;
    }

    public String getOuterId(int sequenceLevel){
        return getAblId().concat(":" + sequenceLevel);
    }

    public Function<Integer, Integer> getCostFunction() {
        return costFunction;
    }

    public ResourceLocation getTextureLocation(){
        return textureLocation;
    }

    public int getPathwayId(){return pathwayId;}

    public Ability create(int pathwaySequenceId){
        return createFunction.apply(pathwaySequenceId).withAbilityId(ablId).withCost(costFunction);
    }

    public AbilityInfo getInfo(int cooldown, int maxCd, boolean enabled, String descId, String innerId) {
        return new AbilityInfo(pathwayId, cooldown, maxCd, enabled, descId, innerId);
    }

    public AbilityInfo getInfo(int cooldown, int maxCd, boolean enabled, String descId, String innerId, int pathwayId) {
        return new AbilityInfo(pathwayId, cooldown, maxCd, enabled, descId, innerId);
    }
}
