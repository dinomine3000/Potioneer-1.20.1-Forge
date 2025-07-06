package net.dinomine.potioneer.block.entity.renderer;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.dinomine.potioneer.block.entity.PriestLightBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PriestBlockModel extends GeoModel<PriestLightBlockEntity> {
    @Override
    public ResourceLocation getModelResource(PriestLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "geo/priest_light.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PriestLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "textures/block/priest_light.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PriestLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "animations/priest_light.animation.json");
    }
}
