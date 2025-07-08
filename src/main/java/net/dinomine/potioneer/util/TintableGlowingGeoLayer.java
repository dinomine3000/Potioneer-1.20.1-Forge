package net.dinomine.potioneer.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.texture.AutoGlowingTexture;
import software.bernie.geckolib.renderer.GeoRenderer;
import software.bernie.geckolib.renderer.layer.GeoRenderLayer;

public class TintableGlowingGeoLayer<T extends GeoTintable> extends GeoRenderLayer<T> {
    public TintableGlowingGeoLayer(GeoRenderer<T> renderer) {
        super(renderer);
    }

    protected RenderType getRenderType(T animatable) {
        return AutoGlowingTexture.getRenderType(this.getTextureResource(animatable));
    }

    @Override
    public void render(PoseStack poseStack, T animatable, BakedGeoModel bakedModel,
                       RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                       float partialTick, int packedLight, int packedOverlay) {

        RenderType emissiveRenderType = this.getRenderType(animatable);
        VertexConsumer tintedBuffer = bufferSource.getBuffer(emissiveRenderType);

        int tintColor = animatable.getHexColor();  // Reads cached NBT

        float r = ((tintColor >> 16) & 0xFF) / 255f;
        float g = ((tintColor >> 8) & 0xFF) / 255f;
        float b = (tintColor & 0xFF) / 255f;

        this.getRenderer().reRender(
                bakedModel,
                poseStack,
                bufferSource,
                animatable,
                emissiveRenderType,
                tintedBuffer,
                partialTick,
                0xF000F0, // full brightness
                OverlayTexture.NO_OVERLAY,
                r, g, b, 1.0f
        );
    }
}
