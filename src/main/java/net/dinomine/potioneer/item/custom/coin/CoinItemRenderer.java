package net.dinomine.potioneer.item.custom.coin;

import software.bernie.geckolib.renderer.GeoItemRenderer;

public class CoinItemRenderer extends GeoItemRenderer<CoinItem> {
    public CoinItemRenderer() {
        super(new CoinItemModel());
    }
}
