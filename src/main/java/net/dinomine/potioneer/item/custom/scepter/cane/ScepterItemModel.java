package net.dinomine.potioneer.item.custom.scepter.cane;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class ScepterItemModel extends GeoModel {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/sea_god_scepter_item.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/sea_god_scepter.png");
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
