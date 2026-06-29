package net.dinomine.potioneer.entities.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.entities.custom.effects.WaterBlockEffectEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.BeaconBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;

import java.util.Optional;
import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class WaterEffectEntityRenderer extends AbstractEffectEntityRenderer<WaterBlockEffectEntity> {
    public WaterEffectEntityRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(WaterBlockEffectEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack poseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if(Minecraft.getInstance().player != null && Minecraft.getInstance().options.getCameraType().isFirstPerson()){
            UUID targetId = pEntity.getTargetId();
            if(targetId != null && targetId.equals(Minecraft.getInstance().player.getUUID())){
                return;
            }
        }
        super.render(pEntity, pEntityYaw, pPartialTick, poseStack, pBuffer, pPackedLight);


        LivingEntity targetEntity = pEntity.getTargetEntity();

        BlockState water = ModBlocks.FAKE_WATER.get().defaultBlockState();

        BlockRenderDispatcher dispatcher =
                Minecraft.getInstance().getBlockRenderer();

        dispatcher.renderSingleBlock(
                water,
                poseStack,
                pBuffer,
                pPackedLight,
                OverlayTexture.NO_OVERLAY,
                ModelData.EMPTY,
                RenderType.translucent()
        );

//        super.render(pEntity, pEntityYaw, pPartialTick, poseStack, pBuffer, pPackedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(WaterBlockEffectEntity waterBlockEffectEntity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
