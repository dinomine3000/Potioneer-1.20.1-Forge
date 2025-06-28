package net.dinomine.potioneer.item.custom.cane;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class CaneItemModel extends GeoModel {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/divination_rod_item.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/divination_rod.png");
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
        return null;
    }

}
