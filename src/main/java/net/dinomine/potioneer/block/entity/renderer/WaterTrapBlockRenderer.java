package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.client.ClientAllyData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.dinomine.potioneer.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChainBlock;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.UUID;

public class WaterTrapBlockRenderer extends GeoBlockRenderer<WaterTrapBlockEntity> {
    public static final ResourceLocation CHAINS_LOCATION = new ResourceLocation(Potioneer.MOD_ID, "textures/block/water_trap_chains.png");

    public WaterTrapBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WaterTrapBlockModel());
    }

    @Override
    public void defaultRender(PoseStack poseStack, WaterTrapBlockEntity waterTrap, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            UUID playerUUID = mc.player.getUUID();
            boolean isInAoj = waterTrap.isInAOJ();
            boolean isAlly = waterTrap.isOwner(playerUUID) || ClientAllyData.isPlayerInGroups(waterTrap.getCasterAllyGroups());
            if (isInAoj && ! isAlly) {
                return;
            }
        }

        int chainCount = waterTrap.getChains();
        if(chainCount > 0) renderChains(chainCount, poseStack, bufferSource, packedLight);
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }

    private static void renderChains(int chainCount, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight){
        poseStack.pushPose();
        poseStack.translate(0.5D, -0.5D, 0.5D);
        //poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        poseStack.pushPose();
            float increment = 1/16f;
            RenderUtils.renderTiledSubTextureQuad(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(CHAINS_LOCATION)),
                    -1.5f*increment, 0.5f, 1.5f*increment,
                    1.5f*increment, 0.5f, -1.5f*increment,
                    1.5f*increment, -0.5f - chainCount, -1.5f*increment,
                    -1.5f*increment, -0.5f - chainCount, 1.5f*increment,
                    0f, 3/16f, 0, 1, chainCount,
                    1f, 1f, 1f, 1f, packedLight
            );
        poseStack.popPose();
        poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(-90));
            RenderUtils.renderTiledSubTextureQuad(poseStack, bufferSource.getBuffer(RenderType.entityTranslucent(CHAINS_LOCATION)),
                    -1.5f*increment, 0.5f, 1.5f*increment,
                    1.5f*increment, 0.5f, -1.5f*increment,
                    1.5f*increment, -0.5f - chainCount, -1.5f*increment,
                    -1.5f*increment, -0.5f - chainCount, 1.5f*increment,
                    3/16f, 6/16f, 0, 1, chainCount,
                    1f, 1f, 1f, 1f, packedLight
            );
        poseStack.popPose();
        poseStack.popPose();
    }


    @Override
    public boolean shouldRenderOffScreen(WaterTrapBlockEntity blockEntity) {
        return blockEntity.getChains() > 0;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRender(WaterTrapBlockEntity blockEntity, Vec3 cameraPos) {
        // Only checks horizontal (X/Z) distance so the beam stays rendered even when looking straight up
        Vec3 blockPosHorizontal = Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 cameraPosHorizontal = cameraPos.multiply(1.0D, 0.0D, 1.0D);

        return blockPosHorizontal.closerThan(cameraPosHorizontal, (double) this.getViewDistance());
    }
}
