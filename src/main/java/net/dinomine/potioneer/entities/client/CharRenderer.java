package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.dinomine.potioneer.util.TintableGlowingGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

import static net.dinomine.potioneer.entities.custom.DivinationRodEntity.lerpRotation;

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
