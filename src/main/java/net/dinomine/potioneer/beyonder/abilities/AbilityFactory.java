package net.dinomine.potioneer.beyonder.abilities;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;
import java.util.function.Supplier;

public class AbilityFactory {
    private final Supplier<Ability> constructionFunction;
    @Getter
    private final ResourceLocation ablId;
    @Getter
    private final int posY;
    @Getter
    private final int pathwayId;

    public AbilityFactory(Supplier<Ability> constructionFunction, ResourceLocation ablId, int posY, int pathwayId) {
        this.constructionFunction = constructionFunction;
        this.ablId = ablId;
        this.posY = posY;
        this.pathwayId = pathwayId;
    }

    public Ability construct(int level, AbilityInfo.Group group){
        Ability abl = constructionFunction.get();
        abl.preInit(ablId, level, group);
        return abl;
    }

}
