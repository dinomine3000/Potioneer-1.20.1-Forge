package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.effects.DiceEffectEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DiceEffectModel extends GeoModel<DiceEffectEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/dice_model.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/dice.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/dice_animation.json");
    @Override
    public ResourceLocation getModelResource(DiceEffectEntity diceEffectEntity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(DiceEffectEntity diceEffectEntity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DiceEffectEntity diceEffectEntity) {
        return animation;
    }
}
