package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.util.TintableGlowingGeoLayer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class CharRenderer extends GeoEntityRenderer<CharacteristicEntity> {
    public CharRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CharModel());
        this.addRenderLayer(new TintableGlowingGeoLayer<>(this));
    }

    //TODO: maybe make it use different textures or models depending on the pathway

//    public ResourceLocation getTextureLocation(CharacteristicEntity animatable){
//        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/beyonder_characteristic.png");
//    }
}
