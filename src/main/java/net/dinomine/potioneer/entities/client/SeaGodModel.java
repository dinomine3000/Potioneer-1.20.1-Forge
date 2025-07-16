package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.dinomine.potioneer.entities.custom.SeaGodScepterEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class SeaGodModel extends GeoModel<SeaGodScepterEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/sea_god_scepter.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/sea_god_scepter.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/sea_god_scepter.animations.json");
    @Override
    public ResourceLocation getModelResource(SeaGodScepterEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(SeaGodScepterEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(SeaGodScepterEntity chryonEntity) {
        return this.animation;
    }

}
