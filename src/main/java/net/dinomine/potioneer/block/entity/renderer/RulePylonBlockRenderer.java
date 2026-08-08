package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAllyData;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.dinomine.potioneer.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.UUID;

public class RulePylonBlockRenderer extends GeoBlockRenderer<RulePylonBlockEntity> {
    public static final ResourceLocation BEAM_LOCATION = new ResourceLocation("textures/entity/beacon_beam.png");
    public static final int MAX_RENDER_Y = 1024;

    public RulePylonBlockRenderer(BlockEntityRendererProvider.Context context){
        super(new RulePylonBlockModel());
    }

    @Override
    public void defaultRender(PoseStack poseStack, RulePylonBlockEntity blockEntity, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, blockEntity, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
        if(!blockEntity.isWorking()) return;
        long gameTime = blockEntity.getLevel().getGameTime();
        renderBeaconBeam(poseStack, bufferSource, partialTick, gameTime, 0, MAX_RENDER_Y);
    }

    @Override
    public boolean shouldRenderOffScreen(RulePylonBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(RulePylonBlockEntity blockEntity, Vec3 cameraPos) {
        Vec3 blockPosHorizontal = Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 cameraPosHorizontal = cameraPos.multiply(1.0D, 0.0D, 1.0D);
        return blockPosHorizontal.closerThan(cameraPosHorizontal, (double) this.getViewDistance());
    }

    private static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, float partialTick, long gameTime, int yOffset, int height) {
        renderBeaconBeam(poseStack, bufferSource, BEAM_LOCATION, partialTick, 1.0F, gameTime, yOffset, height, 0.2F, 0.25F);
    }

    public static void renderBeaconBeam(PoseStack poseStack, MultiBufferSource bufferSource, ResourceLocation beamLocation, float partialTick, float textureScale, long gameTime, int yOffset, int height, float beamRadius, float glowRadius) {
        int maxY = yOffset + height;

        poseStack.pushPose();
        // Center the beam inside the block (0.5, 0.0, 0.5)
        poseStack.translate(0.5D, 0.0D, 0.5D);

        // Animation calculations
        float animatedTime = (float) Math.floorMod(gameTime, 40) + partialTick;
        float scrollDirection = height < 0 ? animatedTime : -animatedTime;
        float vScroll = Mth.frac(scrollDirection * 0.2F - (float) Mth.floor(scrollDirection * 0.1F));

        float red = 1f;
        float green = 1f;
        float blue = 1f;

        // --- 1. RENDER INNER BEAM ---
        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees(animatedTime * 2.25F - 45.0F)); // Rotating inner core

        float minVInner = -1.0F + vScroll;
        float maxVInner = (float) height * textureScale * (0.5F / beamRadius) + minVInner;

        renderPart(
                poseStack,
                bufferSource.getBuffer(RenderType.beaconBeam(beamLocation, false)),
                red, green, blue, 1.0F,
                yOffset, maxY,
                0.0F, beamRadius, beamRadius, 0.0F,
                -beamRadius, 0.0F, 0.0F, -beamRadius,
                0.0F, 1.0F, maxVInner, minVInner
        );
        poseStack.popPose();

        // --- 2. RENDER OUTER GLOW ---
        float minVOuter = -1.0F + vScroll;
        float maxVOuter = (float) height * textureScale + minVOuter;

        renderPart(
                poseStack,
                bufferSource.getBuffer(RenderType.beaconBeam(beamLocation, true)),
                red, green, blue, 0.125F, // Outer glow alpha
                yOffset, maxY,
                -glowRadius, -glowRadius, glowRadius, -glowRadius,
                -glowRadius, glowRadius, glowRadius, glowRadius,
                0.0F, 1.0F, maxVOuter, minVOuter
        );

        poseStack.popPose();
    }

    private static void renderPart(PoseStack poseStack, VertexConsumer consumer, float red, float green, float blue, float alpha, int minY, int maxY, float x0, float z0, float x1, float z1, float x2, float z2, float x3, float z3, float minU, float maxU, float minV, float maxV) {
        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f poseMatrix = lastPose.pose();
        Matrix3f normalMatrix = lastPose.normal();

        // Draw 4 surrounding quad faces
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x0, z0, x1, z1, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x3, z3, x2, z2, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x1, z1, x3, z3, minU, maxU, minV, maxV);
        renderQuad(poseMatrix, normalMatrix, consumer, red, green, blue, alpha, minY, maxY, x2, z2, x0, z0, minU, maxU, minV, maxV);
    }

    private static void renderQuad(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, float red, float green, float blue, float alpha, int minY, int maxY, float minX, float minZ, float maxX, float maxZ, float minU, float maxU, float minV, float maxV) {
        addVertex(pose, normal, consumer, red, green, blue, alpha, maxY, minX, minZ, maxU, minV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, minY, minX, minZ, maxU, maxV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, minY, maxX, maxZ, minU, maxV);
        addVertex(pose, normal, consumer, red, green, blue, alpha, maxY, maxX, maxZ, minU, minV);
    }

    private static void addVertex(Matrix4f pose, Matrix3f normal, VertexConsumer consumer, float red, float green, float blue, float alpha, int y, float x, float z, float u, float v) {
        consumer.vertex(pose, x, (float) y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(15728880) // Full bright lightmap value
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    public static class RulePylonBlockModel extends GeoModel<RulePylonBlockEntity> {
        @Override
        public ResourceLocation getModelResource(RulePylonBlockEntity minerLightBlockEntity) {
            return new ResourceLocation(Potioneer.MOD_ID, "geo/rule_pylon.geo.json");
        }

        @Override
        public ResourceLocation getTextureResource(RulePylonBlockEntity minerLightBlockEntity) {
            return new ResourceLocation(Potioneer.MOD_ID, "textures/block/pylon.png");
        }

        @Override
        public ResourceLocation getAnimationResource(RulePylonBlockEntity minerLightBlockEntity) {
            return new ResourceLocation(Potioneer.MOD_ID, "animations/water_trap.animation.json");
        }

        @Override
        public RenderType getRenderType(RulePylonBlockEntity animatable, ResourceLocation texture) {
            return RenderType.entityTranslucent(texture);
        }
    }

}
