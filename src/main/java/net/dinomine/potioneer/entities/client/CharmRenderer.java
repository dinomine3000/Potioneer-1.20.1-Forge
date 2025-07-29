package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.dinomine.potioneer.entities.custom.CharmEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoEntityRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CharmRenderer extends GeoEntityRenderer<CharmEntity> {
    public CharmRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new CharmModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }

    @Override
    public void applyRenderLayersForBone(PoseStack poseStack, CharmEntity animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay) {
        super.applyRenderLayersForBone(poseStack, animatable, bone, renderType, bufferSource, buffer, partialTick, packedLight, packedOverlay);

        int id = animatable.getEntityData().get(CharmEntity.PATHWAY_ID);
        switch (bone.getName()){
            case "minerCharm":
                bone.setHidden(id != 0);
                break;
            case "swimmerCharm":
                bone.setHidden(id != 1);
                break;
            case "tricksterCharm":
                bone.setHidden(id != 2);
                break;
            case "warriorCharm":
                bone.setHidden(id != 3);
                break;
            case "crafterCharm":
                bone.setHidden(id != 4);
                break;
        }
    }

    //TODO: maybe make it use different textures or models depending on the pathway

//    public ResourceLocation getTextureLocation(CharacteristicEntity animatable){
//        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/beyonder_characteristic.png");
//    }
}
