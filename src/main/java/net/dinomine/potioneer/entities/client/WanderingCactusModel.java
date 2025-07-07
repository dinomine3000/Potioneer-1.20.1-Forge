package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.WanderingCactusEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class WanderingCactusModel extends GeoModel<WanderingCactusEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/wandering_cactus.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/wandering_cactus.png");
    private final ResourceLocation animations = new ResourceLocation(Potioneer.MOD_ID, "animations/wandering_cactus_animation.json");
    @Override
    public ResourceLocation getModelResource(WanderingCactusEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(WanderingCactusEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(WanderingCactusEntity chryonEntity) {
        return this.animations;
    }
}
