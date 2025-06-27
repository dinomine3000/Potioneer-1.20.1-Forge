package net.dinomine.potioneer.block.entity.renderer;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class MinerBlockModel extends GeoModel<MinerLightBlockEntity> {
    @Override
    public ResourceLocation getModelResource(MinerLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "geo/miner_light.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(MinerLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "textures/block/miner_light.png");
    }

    @Override
    public ResourceLocation getAnimationResource(MinerLightBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "animations/miner_light.animation.json");
    }
}
