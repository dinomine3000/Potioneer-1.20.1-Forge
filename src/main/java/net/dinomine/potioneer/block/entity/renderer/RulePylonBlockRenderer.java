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
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.UUID;

public class RulePylonBlockRenderer extends GeoBlockRenderer<RulePylonBlockEntity> {

    public RulePylonBlockRenderer(BlockEntityRendererProvider.Context context){
        super(new RulePylonBlockModel());
    }


    @Override
    public void defaultRender(PoseStack poseStack, RulePylonBlockEntity waterTrap, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {
        super.defaultRender(poseStack, waterTrap, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
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
        // Only checks horizontal (X/Z) distance so the beam stays rendered even when looking straight up
        Vec3 blockPosHorizontal = Vec3.atCenterOf(blockEntity.getBlockPos()).multiply(1.0D, 0.0D, 1.0D);
        Vec3 cameraPosHorizontal = cameraPos.multiply(1.0D, 0.0D, 1.0D);

        return blockPosHorizontal.closerThan(cameraPosHorizontal, (double) this.getViewDistance());
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
