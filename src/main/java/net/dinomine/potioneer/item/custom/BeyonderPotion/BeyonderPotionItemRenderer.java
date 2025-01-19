package net.dinomine.potioneer.item.custom.BeyonderPotion;

import software.bernie.example.client.renderer.entity.CoolKidRenderer;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BeyonderPotionItemRenderer extends GeoItemRenderer<BeyonderPotionItem> {
    public BeyonderPotionItemRenderer() {
        super(new BeyonderPotionModel());
        this.addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
