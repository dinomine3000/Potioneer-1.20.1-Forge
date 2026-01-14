package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.block.entity.RitualPedestalBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RitualPedestalBlockEntityRenderer implements BlockEntityRenderer<RitualPedestalBlockEntity> {
    private ItemRenderer itemRenderer;
    public RitualPedestalBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(RitualPedestalBlockEntity bEntity, float partialTicks, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        ItemStack itemStack = bEntity.getRenderStack();
        if(itemStack.isEmpty()) return;
        BlockPos pos = bEntity.getBlockPos();
        Level level = bEntity.getLevel();
        if(!level.getBlockState(pos).isAir() && !level.getBlockState(pos.above()).canOcclude()) {
            float posX = 0.5f;
            float posY = 1.3f;
            float posZ = 0.5f;

            // Get rotation based on world time
            float tickReference = (level.getGameTime() + partialTicks) % 360;
            float rotation = (-tickReference) * 4f;

            poseStack.pushPose();
            poseStack.translate(posX, posY + 0.05*Math.cos(tickReference*Math.PI/180), posZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(rotation)); // Rotate around Y

            itemRenderer.renderStatic(itemStack, ItemDisplayContext.GROUND,
                    packedLight, packedOverlay, poseStack, multiBufferSource, level, 1);
            poseStack.popPose();
        }
    }
}
