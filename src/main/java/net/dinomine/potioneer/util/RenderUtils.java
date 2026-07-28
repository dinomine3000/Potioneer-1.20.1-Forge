package net.dinomine.potioneer.util;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

public class RenderUtils {

    /**
     * Adds a single vertex to a VertexConsumer buffer with full parameters.
     */
    public static void addVertex(PoseStack poseStack, VertexConsumer consumer,
                                 float x, float y, float z,
                                 float red, float green, float blue, float alpha,
                                 float u, float v, int packedLight) {

        PoseStack.Pose last = poseStack.last();
        Matrix4f poseMatrix = last.pose();
        Matrix3f normalMatrix = last.normal();

        consumer.vertex(poseMatrix, x, y, z)
                .color(red, green, blue, alpha)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normalMatrix, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }

    /**
     * Renders a 2D quad face in 3D space.
     */
    public static void renderQuad(PoseStack poseStack, VertexConsumer consumer,
                                  float x1, float y1, float z1,
                                  float x2, float y2, float z2,
                                  float x3, float y3, float z3,
                                  float x4, float y4, float z4,
                                  float r, float g, float b, float a,
                                  int packedLight) {

        addVertex(poseStack, consumer, x1, y1, z1, r, g, b, a, 0.0F, 0.0F, packedLight);
        addVertex(poseStack, consumer, x2, y2, z2, r, g, b, a, 0.0F, 1.0F, packedLight);
        addVertex(poseStack, consumer, x3, y3, z3, r, g, b, a, 1.0F, 1.0F, packedLight);
        addVertex(poseStack, consumer, x4, y4, z4, r, g, b, a, 1.0F, 0.0F, packedLight);
    }

    /**
     * Renders a single quad using a specific UV crop window from a texture, repeating it vertically.
     *
     * @param uMin         Starting horizontal UV (0.0F to 1.0F)
     * @param uMax         Ending horizontal UV (0.0F to 1.0F)
     * @param vMin         Starting vertical UV (0.0F to 1.0F)
     * @param vMax         Ending vertical UV (0.0F to 1.0F)
     * @param vRepeatCount How many times the specified V segment should repeat vertically along the quad
     */
    public static void renderTiledSubTextureQuad(
            PoseStack poseStack, VertexConsumer consumer,
            float x1, float y1, float z1, // Top-Left
            float x2, float y2, float z2, // Bottom-Left
            float x3, float y3, float z3, // Bottom-Right
            float x4, float y4, float z4, // Top-Right
            float uMin, float uMax,
            float vMin, float vMax,
            float vRepeatCount,
            float red, float green, float blue, float alpha,
            int packedLight)
    {

        PoseStack.Pose lastPose = poseStack.last();
        Matrix4f poseMatrix = lastPose.pose();
        Matrix3f normalMatrix = lastPose.normal();

        // Calculate total scaled V distance for repeating the sub-region
        float vSpan = (vMax - vMin) * vRepeatCount;
        float vTiledMax = vMin + vSpan;

        // 1. Top-Left Point (x1, y1, z1) -> Gets Top-Right UV (uMax, vMin)
        addVertex(poseMatrix, normalMatrix, consumer, x1, y1, z1, uMax, vMin, red, green, blue, alpha, packedLight);

        // 2. Bottom-Left Point (x2, y2, z2) -> Gets Top-Left UV (uMin, vMin)
        addVertex(poseMatrix, normalMatrix, consumer, x2, y2, z2, uMin, vMin, red, green, blue, alpha, packedLight);

        // 3. Bottom-Right Point (x3, y3, z3) -> Gets Bottom-Left UV (uMin, vTiledMax)
        addVertex(poseMatrix, normalMatrix, consumer, x3, y3, z3, uMin, vTiledMax, red, green, blue, alpha, packedLight);

        // 4. Top-Right Point (x4, y4, z4) -> Gets Bottom-Right UV (uMax, vTiledMax)
        addVertex(poseMatrix, normalMatrix, consumer, x4, y4, z4, uMax, vTiledMax, red, green, blue, alpha, packedLight);
    }

    private static void addVertex(
            Matrix4f pose, Matrix3f normal, VertexConsumer consumer,
            float x, float y, float z,
            float u, float v,
            float r, float g, float b, float a,
            int packedLight) {

        consumer.vertex(pose, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(normal, 0.0F, 1.0F, 0.0F)
                .endVertex();
    }
}