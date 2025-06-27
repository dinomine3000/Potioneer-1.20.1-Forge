package net.dinomine.potioneer.item.custom.coin;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;

public class CoinItemModel extends GeoModel {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/coin.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/item/coin.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/coin.animation.json");
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
