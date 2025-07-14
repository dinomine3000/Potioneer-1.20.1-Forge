package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class AsteroidModel extends GeoModel<AsteroidEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/asteroid.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/asteroid.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/asteroid.animation.json");
    @Override
    public ResourceLocation getModelResource(AsteroidEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(AsteroidEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(AsteroidEntity chryonEntity) {
        return this.animation;
    }
}
