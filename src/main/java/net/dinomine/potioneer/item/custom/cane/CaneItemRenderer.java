package net.dinomine.potioneer.item.custom.cane;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CaneItemRenderer extends GeoItemRenderer<CaneItem> {
    public CaneItemRenderer() {
        super(new CaneItemModel());
    }
}
