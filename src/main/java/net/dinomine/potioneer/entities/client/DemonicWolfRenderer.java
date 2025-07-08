package net.dinomine.potioneer.entities.client;

import net.dinomine.potioneer.entities.custom.DemonicWolfEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class DemonicWolfRenderer extends GeoEntityRenderer<DemonicWolfEntity> {
    public DemonicWolfRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, new DemonicWolfModel());
    }
}
