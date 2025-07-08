package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.DemonicWolfEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DemonicWolfModel extends GeoModel<DemonicWolfEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/demonic_wolf.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/demonic_wolf.png");
    private final ResourceLocation animations = new ResourceLocation(Potioneer.MOD_ID, "animations/demonic_wolf_animations.json");
    @Override
    public ResourceLocation getModelResource(DemonicWolfEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(DemonicWolfEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DemonicWolfEntity chryonEntity) {
        return this.animations;
    }

}
