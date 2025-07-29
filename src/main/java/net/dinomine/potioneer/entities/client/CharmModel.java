package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.dinomine.potioneer.entities.custom.CharmEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;

public class CharmModel extends GeoModel<CharmEntity> {
    private final ResourceLocation model = new ResourceLocation(Potioneer.MOD_ID, "geo/charm.geo.json");
    private final ResourceLocation texture = new ResourceLocation(Potioneer.MOD_ID, "textures/entity/charm_entity.png");
    private final ResourceLocation animation = new ResourceLocation(Potioneer.MOD_ID, "animations/charm.animation.json");
    @Override
    public ResourceLocation getModelResource(CharmEntity chryonEntity) {
        return this.model;
    }

    @Override
    public ResourceLocation getTextureResource(CharmEntity chryonEntity) {
        return this.texture;
    }

    @Override
    public ResourceLocation getAnimationResource(CharmEntity chryonEntity) {
        return this.animation;
    }

    @Override
    public void setCustomAnimations(CharmEntity animatable, long instanceId, AnimationState<CharmEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        // Access the root bone
        var controller = getAnimationProcessor();
        var rootBone = controller.getBone("bone2"); // Replace with your actual bone name

        if (rootBone != null && !animatable.getEntityData().get(CharmEntity.DISTANCE_CHECK)) {
            // Rotate model to match entity's yaw (in radians)
            //float yaw = Mth.rotLerp(animationState.getPartialTick(), animatable.yRotO, animatable.yaw);
            rootBone.setRotY((float) Math.toRadians(-animatable.yaw)); // Negative to match MC coordinate system
        }
    }
}
