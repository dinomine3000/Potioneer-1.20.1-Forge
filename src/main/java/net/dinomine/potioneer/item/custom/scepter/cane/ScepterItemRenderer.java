package net.dinomine.potioneer.item.custom.scepter.cane;

import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class ScepterItemRenderer extends GeoItemRenderer<ScepterItem> {
    public ScepterItemRenderer() {
        super(new ScepterItemModel());
        addRenderLayer(new AutoGlowingGeoLayer<>(this));
    }
}
