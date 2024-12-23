package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class PecanModel extends GeoModel<PecanEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/pecan.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/pecan.png");
    private final ResourceLocation animations = new ResourceLocation(Potioneer.MOD_ID, "animations/pecan_animations.json");
    @Override
    public ResourceLocation getModelResource(PecanEntity pecanEntity) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(PecanEntity pecanEntity) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(PecanEntity pecanEntity) {
        return animations;
    }
}
