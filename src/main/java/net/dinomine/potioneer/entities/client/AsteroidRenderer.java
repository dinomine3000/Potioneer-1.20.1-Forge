package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class AsteroidRenderer extends GeoEntityRenderer<AsteroidEntity> {
    public AsteroidRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new AsteroidModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    //TODO: maybe make it use different textures or models depending on the pathway

//    public ResourceLocation getTextureLocation(CharacteristicEntity animatable){
//        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/beyonder_characteristic.png");
//    }
}
