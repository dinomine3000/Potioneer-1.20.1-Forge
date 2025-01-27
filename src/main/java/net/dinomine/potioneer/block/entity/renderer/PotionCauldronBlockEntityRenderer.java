package net.dinomine.potioneer.block.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EnchantmentTableParticle;
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
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.EnchantmentTableBlock;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.system.MathUtil;

import java.util.function.Supplier;

import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.DIRECTION;
import static net.dinomine.potioneer.block.custom.PotionCauldronBlock.WATER_LEVEL;

public class PotionCauldronBlockEntityRenderer implements BlockEntityRenderer<PotionCauldronBlockEntity> {

    private boolean inside = false;

    public PotionCauldronBlockEntityRenderer(BlockEntityRendererProvider.Context context){
    }

    @Override
    public void render(PotionCauldronBlockEntity bEntity, float v, PoseStack poseStack,
                       MultiBufferSource multiBufferSource, int i, int i1) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
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
            //float[][] rotationMat = mathHelper.getRotationMatrixY(theta);
            //float[][] invTranslationMat = mathHelper.getTranslationMatrix(0.5f, 0, 0.5f);

            float scale = 0.25f;
//            float y = switch (level.getBlockState(pos).getValue(WATER_LEVEL)) {
//                case 1 -> 0.3f;
//                case 2 -> 0.55f;
//                default -> 0.8f;
//            };
            int size = bEntity.caretPosition();
            float invScale = 25f/8;

            float[][] concoctMat = null;
            float[][] tFall = null;
            /*Player nearestPlayer = level.getNearestPlayer((double)pos.getX() + (double)0.5F,
                                                          (double)pos.getY() + (double)0.5F,
                                                          (double)pos.getZ() + (double)0.5F,
                                                              (double)3.0F, false);
            if(nearestPlayer != null){
                float[][] playerPos = mathHelper.getPositionMatrix(nearestPlayer.position());
                float[][] blockPos = mathHelper.getPositionMatrix(pos.getX(), pos.getY(), pos.getZ());
                float[][] pointingVector = mathHelper.subMatrices(blockPos, playerPos);
                System.out.println(mathHelper.getString(pointingVector));
                float[][] baseVector = mathHelper.multiply(mathHelper.getRotationMatrixY(theta), new float[][]{{1}, {0}, {0}, {0}});
                rotationMatrix = mathHelper.multiply(rotationMatrix, mathHelper.getRotationMatrixY(mathHelper.getAngleFromVector(baseVector, pointingVector)));
            }*/

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
                if(inside){
//                    if(!itemStacks.get(j).isEmpty() && j%2 == 0){
//                        poseStack.pushPose();
//                        float diff = 0.06f*((pos.getX() +  pos.getZ() + (float) j /2) % 2) - 0.03f;
//                        switch(j){
//                            case 0:
//                                poseStack.translate(0.65f + diff, 0.05 + y + diff, 0.3f);
//                                poseStack.scale(scale, scale, scale);
//                                poseStack.mulPose(Axis.XP.rotationDegrees(320));
//                                poseStack.mulPose(Axis.YP.rotationDegrees(150));
//                                poseStack.mulPose(Axis.YP.rotationDegrees(30));
//                                break;
//                            case 2:
//                                poseStack.translate(0.25f, y + diff, 0.5f + diff);
//                                poseStack.scale(scale, scale, scale);
//                                poseStack.mulPose(Axis.XP.rotationDegrees(270));
//                                poseStack.mulPose(Axis.ZP.rotationDegrees(45));
//                                poseStack.mulPose(Axis.YP.rotationDegrees(75));
//                                break;
//                            case 4:
//                                poseStack.translate(0.7f + diff, y + diff, 0.65f + diff);
//                                poseStack.scale(scale, scale, scale);
//                                poseStack.mulPose(Axis.XP.rotationDegrees(140));
//                                poseStack.mulPose(Axis.YP.rotationDegrees(290));
//                                break;
//                            case 6:
//                                poseStack.translate(0.5f + diff, y + diff, 0.7f);
//                                poseStack.scale(scale, scale, scale);
//                                poseStack.mulPose(Axis.XP.rotationDegrees(170));
//                                poseStack.mulPose(Axis.ZP.rotationDegrees(65));
//                                poseStack.mulPose(Axis.YP.rotationDegrees(210));
//                                break;
//                            case 8:
//                                poseStack.translate(0.6f + diff, y + diff, 0.5f);
//                                poseStack.scale(scale, scale, scale);
//                                poseStack.mulPose(Axis.XP.rotationDegrees(130));
//                                poseStack.mulPose(Axis.ZP.rotationDegrees(145));
//                                break;
//
//                        }
//
//                        itemRenderer.renderStatic(itemStacks.get(j), ItemDisplayContext.FIXED,
//                                getLightLevel(bEntity.getLevel(), bEntity.getBlockPos()), OverlayTexture.NO_OVERLAY, poseStack, multiBufferSource,
//                                bEntity.getLevel(), 1);
//                        poseStack.popPose();
//                    }
                } else {
                    poseStack.pushPose();

                    float posX = 0.3f + (j%3)*0.2f;
                    float posY = 1.5f - Math.floorDiv(j, 3)*0.2f;
                    float posZ = 0.3f + Math.floorDiv(j, 3)*0.2f + (j%3)*0.02f;
                    float[][] posMat = mathHelper.getPositionMatrix(posX, posY, posZ);
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

    }

    private int getLightLevel(Level level, BlockPos blockPos){
        int bLight = level.getBrightness(LightLayer.BLOCK, blockPos);
        int sLight = level.getBrightness(LightLayer.SKY,blockPos);
        return LightTexture.pack(bLight, sLight);
    }
}
