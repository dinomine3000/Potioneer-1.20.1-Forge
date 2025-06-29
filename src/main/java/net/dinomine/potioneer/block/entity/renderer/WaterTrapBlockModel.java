package net.dinomine.potioneer.block.entity.renderer;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class WaterTrapBlockModel extends GeoModel<WaterTrapBlockEntity> {
    @Override
    public ResourceLocation getModelResource(WaterTrapBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "geo/water_trap.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(WaterTrapBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "textures/block/water_trap_1.png");
    }

    @Override
    public ResourceLocation getAnimationResource(WaterTrapBlockEntity minerLightBlockEntity) {
        return new ResourceLocation(Potioneer.MOD_ID, "animations/water_trap.animation.json");
    }

    @Override
    public RenderType getRenderType(WaterTrapBlockEntity animatable, ResourceLocation texture) {
        return RenderType.entityTranslucent(texture);
    }
}
