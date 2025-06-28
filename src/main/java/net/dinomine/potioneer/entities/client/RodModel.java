package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class RodModel extends GeoModel<DivinationRodEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/divination_rod.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/divination_rod.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/rod.animation.json");
    @Override
    public ResourceLocation getModelResource(DivinationRodEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(DivinationRodEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(DivinationRodEntity chryonEntity) {
        return this.animation;
    }

//    @Override
//    public void setCustomAnimations(ChryonEntity animatable, long instanceId, AnimationState<ChryonEntity> animationState) {
//        CoreGeoBone head = getAnimationProcessor().getBone("head");
//
//        if(head != null){
//            EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
//
//            head.setRotX(entityData.headPitch() * Mth.DEG_TO_RAD);
//            head.setRotY(entityData.netHeadYaw() * Mth.DEG_TO_RAD);
//        }
//    }
}
