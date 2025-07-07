package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class CharModel extends GeoModel<CharacteristicEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/beyonder_characteristic.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/beyonder_characteristic.png");
    @Override
    public ResourceLocation getModelResource(CharacteristicEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(CharacteristicEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CharacteristicEntity chryonEntity) {
        return null;
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
