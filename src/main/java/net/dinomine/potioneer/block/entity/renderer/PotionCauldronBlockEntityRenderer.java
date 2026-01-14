package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;

import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.DIRECTION;

public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<PotionCauldronBlockEntity> {

    ItemRenderer itemRenderer;

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context){
        itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(PotionCauldronBlockEntity bEntity, float v, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int i, int i1) {
        NonNullList<ItemStack> itemStacks = bEntity.getRenderStack();

        BlockPos pos = bEntity.getBlockPos();
        Level level = bEntity.getLevel();
        if(!level.getBlockState(pos).isAir() && !level.getBlockState(pos.above()).canOcclude()){
            PotioneerMathHelper.MatrixHelper mathHelper = new PotioneerMathHelper.MatrixHelper();
            //float[][] translationMat = mathHelper.getTranslationMatrix(-0.5f, 0, -0.5f);
            Direction dir = level.getBlockState(pos).getValue(DIRECTION);
            float theta = switch(dir){
                //North defaults to 0
                case SOUTH -> (float) Math.PI;
                case EAST -> (float) Math.PI*3/2;
                case WEST -> (float) Math.PI/2;
                default -> 0;
            };
            float[][] rotationMatrix = switch(dir){
                case NORTH -> mathHelper.transformMatrix180;
                case EAST -> mathHelper.transformMatrix90;
                case WEST -> mathHelper.transformMatrix270;
                default -> mathHelper.transformMatrix0;
            };

            float scale = 0.25f;

            int size = bEntity.caretPosition();
            float invScale = 25f/8;

            float[][] concoctMat = null;
            float[][] tFall = null;

            if(bEntity.countDown > 0){
                float prog1 = Mth.clamp(bEntity.countDown, 0, 20)/20f;
                float prog2 = Mth.clamp(bEntity.countDown-20, 0, 20)/20f;
                float[][] t1 = mathHelper.getTranslationMatrix(-0.5f, -1.3f, -0.5f);
                float[][] sc = mathHelper.getScaleMatrix(-0.9f*prog1*prog1*prog1*prog1 + 1f);
                float[][] t2 = mathHelper.getTranslationMatrix(0.5f, 1.3f, 0.5f);
                concoctMat = mathHelper.multiply(t2, mathHelper.multiply(sc, t1));
                float calcPosition = -20*prog2*prog2*prog2*prog2/2f;
                tFall = mathHelper.getTranslationMatrix(0, Mth.clamp(calcPosition, -1f, 0), 0);
            }
            for (int j = 0; j < itemStacks.size(); j++) {
                poseStack.pushPose();

                float posX = 0.3f + (j%3)*0.2f;
                float posY = 1.5f - Math.floorDiv(j, 3)*0.2f;
                float posZ = 0.3f + Math.floorDiv(j, 3)*0.2f + (j%3)*0.02f;
                float[][] posMat = PotioneerMathHelper.MatrixHelper.getPositionMatrix(posX, posY, posZ);
                //float[][] resMat = mathHelper.multiply(invTranslationMat,mathHelper.multiply(rotationMat,mathHelper.multiply(translationMat, posMat)));
                float[][] resMat = mathHelper.multiply(rotationMatrix, posMat);

                float zShaking = (float)(Math.random()*((size - 1)*invScale) - ((size - 1)*invScale/2));
                if(bEntity.countDown > 0){
                    resMat = mathHelper.multiply(concoctMat, resMat);
                    if(bEntity.countDown >= 20){
                        resMat = mathHelper.multiply(tFall, resMat);
                        zShaking = 0;
                    }
                }

                poseStack.translate(resMat[0][0], resMat[1][0], resMat[2][0]);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees((float) ((theta*180)/Math.PI)));
                poseStack.mulPose(Axis.ZP.rotationDegrees(zShaking));
                poseStack.mulPose(Axis.XP.rotationDegrees(25));

                itemRenderer.renderStatic(itemStacks.get(j), ItemDisplayContext.FIXED,
                        getLightLevel(bEntity.getLevel(), bEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource,
                        bEntity.getLevel(), 1);
                poseStack.popPose();
            }

        }

    }

    public static int getLightLevel(Level level, BlockPos blockPos){
        int bLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int sLight = level.getBrightness(LightLayer.SKY,blockPos);
        return LightTexture.pack(bLight, sLight);
    }
}
