package net.dinomine.potioneer.item.custom.BeyonderPotion;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.util.TintableGlowingGeoLayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.example.client.renderer.entity.CoolKidRenderer;
import software.bernie.geckolib.core.object.Color;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoItemRenderer;
import software.bernie.geckolib.renderer.layer.AutoGlowingGeoLayer;

public class BeyonderPotionItemRenderer extends GeoItemRenderer<BeyonderPotionItem> {
    public BeyonderPotionItemRenderer() {
        super(new BeyonderPotionModel());
        this.addRenderLayer(new TintableGlowingGeoLayer<>(this));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext ctx,
                             PoseStack pose, MultiBufferSource buf,
                             int light, int overlay) {
        BeyonderPotionItem.capture(stack);              // Capture before rendering
        super.renderByItem(stack, ctx, pose, buf, light, overlay);
        BeyonderPotionItem.clear();                     // Clear after rendering
    }

}
