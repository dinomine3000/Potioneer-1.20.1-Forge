package net.dinomine.potioneer.item.custom.DeathKnell;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class DeathKnellModel extends GeoModel {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/death_knell.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/item/death_knell.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/death_knell_animations.json");
    @Override
    public ResourceLocation getModelResource(GeoAnimatable geoAnimatable) {
        return model;
    }

    @Override
    public ResourceLocation getTextureResource(GeoAnimatable geoAnimatable) {
        return texture;
    }

    @Override
    public ResourceLocation getAnimationResource(GeoAnimatable geoAnimatable) {
        return animation;
    }

}
