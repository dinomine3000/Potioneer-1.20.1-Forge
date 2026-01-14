package net.dinomine.potioneer.item.custom.leymanosTravels;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class LeymanosTravelsRenderer extends GeoItemRenderer<LeymanosTravels> {
    public LeymanosTravelsRenderer() {
        super(new LeymanosTravelsModel());
    }



    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext transformType, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        LeymanosTravels.capture(stack);
        super.renderByItem(stack, transformType, poseStack, bufferSource, packedLight, packedOverlay);
        LeymanosTravels.clear();
    }
}
