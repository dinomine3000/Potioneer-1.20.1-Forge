package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.entities.custom.WanderingCactusEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class WanderingCactusRenderer extends GeoEntityRenderer<WanderingCactusEntity> {
    public WanderingCactusRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new WanderingCactusModel());
    }
}
