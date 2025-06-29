package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.dinomine.potioneer.block.entity.MinerLightBlockEntity;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.renderer.GeoBlockRenderer;

import java.util.UUID;

public class WaterTrapBlockRenderer extends GeoBlockRenderer<WaterTrapBlockEntity> {
    public WaterTrapBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(new WaterTrapBlockModel());
    }

    @Override
    public void defaultRender(PoseStack poseStack, WaterTrapBlockEntity animatable, MultiBufferSource bufferSource, @Nullable RenderType renderType, @Nullable VertexConsumer buffer, float yaw, float partialTick, int packedLight) {

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            UUID playerUUID = mc.player.getUUID();
            UUID ownerUUID = animatable.getOwnerUUID(); // your custom method

            if (ownerUUID != null && ownerUUID.compareTo(playerUUID) != 0) {
                // Do not render for non-owners
                return;
            }
        }
        super.defaultRender(poseStack, animatable, bufferSource, renderType, buffer, yaw, partialTick, packedLight);
    }
}
