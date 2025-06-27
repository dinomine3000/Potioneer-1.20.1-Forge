package net.dinomine.potioneer.item.custom.coin;

import net.dinomine.potioneer.item.custom.BeyonderPotion.BeyonderPotionItem;
import net.dinomine.potioneer.item.custom.BeyonderPotion.BeyonderPotionModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class CoinItemRenderer extends GeoItemRenderer<CoinItem> {
    public CoinItemRenderer() {
        super(new CoinItemModel());
    }
}
