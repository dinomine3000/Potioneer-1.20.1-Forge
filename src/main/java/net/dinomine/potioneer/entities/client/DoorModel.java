package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.DoorEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class DoorModel extends GeoModel<DoorEntity> {
    @Override
    public ResourceLocation getModelResource(DoorEntity doorEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "geo/door.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(DoorEntity doorEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "textures/entity/door.png");
    }

    @Override
    public ResourceLocation getAnimationResource(DoorEntity doorEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "animations/door.animation.json");
    }
}
