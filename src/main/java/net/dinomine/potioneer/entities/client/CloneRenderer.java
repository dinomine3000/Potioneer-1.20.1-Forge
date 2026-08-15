package net.dinomine.potioneer.entities.client;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.client.resources.SkinManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.Map;
import java.util.UUID;

public class CloneRenderer extends LivingEntityRenderer<CloneEntity, PlayerModel<CloneEntity>> {
    private final PlayerModel<CloneEntity> defaultModel;
    private final HumanoidArmorLayer<CloneEntity, PlayerModel<CloneEntity>, HumanoidArmorModel<CloneEntity>> defaultArmor;
    private final PlayerModel<CloneEntity> slimModel;
    private final HumanoidArmorLayer<CloneEntity, PlayerModel<CloneEntity>, HumanoidArmorModel<CloneEntity>> slimArmor;

    public CloneRenderer(EntityRendererProvider.Context pContext) {
        super(pContext, new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER), false), 0.5f);

        this.addLayer(new ItemInHandLayer(this, pContext.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer(pContext, this));
        this.addLayer(new CustomHeadLayer(this, pContext.getModelSet(), pContext.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer(this, pContext.getModelSet()));

        this.defaultModel = this.model;
        this.slimModel = new PlayerModel<>(pContext.bakeLayer(ModelLayers.PLAYER_SLIM), true);
        this.defaultArmor = new HumanoidArmorLayer(this, new HumanoidArmorModel(pContext.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(pContext.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), pContext.getModelManager());
        this.slimArmor = new HumanoidArmorLayer(this, new HumanoidArmorModel(pContext.bakeLayer(ModelLayers.PLAYER_SLIM_INNER_ARMOR)), new HumanoidArmorModel(pContext.bakeLayer(ModelLayers.PLAYER_SLIM_OUTER_ARMOR)), pContext.getModelManager());

    }
    public void render(CloneEntity clone, float yaw, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight) {
        GameProfile profile = clone.getProfile();
        if (profile != null) {
            if (!clone.isSkinLoaded()) CloneSkinHelper.fetchAndCacheSkin(clone);
            boolean isSlim;
            if (clone.isSkinLoaded())isSlim = clone.isClientSlim();
            else isSlim = "slim".equals(DefaultPlayerSkin.getSkinModelName(profile.getId()));
            updateArmorLayer(isSlim);
            if(isSlim) this.model = slimModel;
            else this.model = defaultModel;
        }

        this.setModelProperties(clone);
        super.render(clone, yaw, partialTick, poseStack, multiBufferSource, packedLight);
    }

    private void updateArmorLayer(boolean slim) {
        if (slim) {
            if (!this.layers.contains(this.slimArmor)) {
                this.layers.remove(this.defaultArmor);
                this.layers.add(this.slimArmor);
            }
        } else {
            if (!this.layers.contains(this.defaultArmor)) {
                this.layers.remove(this.slimArmor);
                this.layers.add(this.defaultArmor);
            }
        }
    }
    @Override
    public ResourceLocation getTextureLocation(CloneEntity clone) {
        if (clone.isSkinLoaded() && clone.getClientSkinLocation() != null) {
            return clone.getClientSkinLocation();
        }

        GameProfile profile = clone.getProfile();
        if (profile != null && profile.getId() != null) {
            return DefaultPlayerSkin.getDefaultSkin(profile.getId());
        }
        return DefaultPlayerSkin.getDefaultSkin();
    }


    public Vec3 getRenderOffset(CloneEntity pEntity, float pPartialTicks) {
        return pEntity.isShiftKeyDown() ? new Vec3(0.0, -0.125, 0.0) : super.getRenderOffset(pEntity, pPartialTicks);
    }

    private void setModelProperties(CloneEntity pClientPlayer) {
        PlayerModel<CloneEntity> playermodel = this.getModel();
        if (pClientPlayer.isSpectator()) {
            playermodel.setAllVisible(false);
            playermodel.head.visible = true;
            playermodel.hat.visible = true;
        } else {
            playermodel.setAllVisible(true);

            playermodel.crouching = pClientPlayer.isShiftKeyDown();
            HumanoidModel.ArmPose humanoidmodel$armpose = getArmPose(pClientPlayer, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose humanoidmodel$armpose1 = getArmPose(pClientPlayer, InteractionHand.OFF_HAND);
            if (humanoidmodel$armpose.isTwoHanded()) {
                humanoidmodel$armpose1 = pClientPlayer.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
            }

            if (pClientPlayer.getMainArm() == HumanoidArm.RIGHT) {
                playermodel.rightArmPose = humanoidmodel$armpose;
                playermodel.leftArmPose = humanoidmodel$armpose1;
            } else {
                playermodel.rightArmPose = humanoidmodel$armpose1;
                playermodel.leftArmPose = humanoidmodel$armpose;
            }
        }

    }

    private static HumanoidModel.ArmPose getArmPose(CloneEntity pPlayer, InteractionHand pHand) {
        ItemStack itemstack = pPlayer.getItemInHand(pHand);
        if (itemstack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        } else {
            if (pPlayer.getUsedItemHand() == pHand && pPlayer.getUseItemRemainingTicks() > 0) {
                UseAnim useanim = itemstack.getUseAnimation();
                if (useanim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }

                if (useanim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }

                if (useanim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }

                if (useanim == UseAnim.CROSSBOW && pHand == pPlayer.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }

                if (useanim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }

                if (useanim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }

                if (useanim == UseAnim.BRUSH) {
                    return HumanoidModel.ArmPose.BRUSH;
                }
            } else if (!pPlayer.swinging && itemstack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemstack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }

            HumanoidModel.ArmPose forgeArmPose = IClientItemExtensions.of(itemstack).getArmPose(pPlayer, pHand, itemstack);
            return forgeArmPose != null ? forgeArmPose : HumanoidModel.ArmPose.ITEM;
        }
    }

    protected void scale(CloneEntity pLivingEntity, PoseStack pPoseStack, float pPartialTickTime) {
        float scale = 0.9375F;
        pPoseStack.scale(scale, scale, scale);
    }

    @Override
    protected void renderNameTag(CloneEntity pEntity, Component pDisplayName, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {
        if(shouldShowName(pEntity)) super.renderNameTag(pEntity, pDisplayName, pPoseStack, pBuffer, pPackedLight);
    }

    protected boolean shouldShowName(CloneEntity pEntity) {
        return super.shouldShowName(pEntity) && (pEntity.shouldShowName() || pEntity.hasCustomName() && pEntity == this.entityRenderDispatcher.crosshairPickEntity);
    }


    protected void setupRotations(CloneEntity pEntityLiving, PoseStack pPoseStack, float pAgeInTicks, float pRotationYaw, float pPartialTicks) {
        float f = pEntityLiving.getSwimAmount(pPartialTicks);
        float f3;
        float f2;
        if (pEntityLiving.isFallFlying()) {
            super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            f3 = (float)pEntityLiving.getFallFlyingTicks() + pPartialTicks;
            f2 = Mth.clamp(f3 * f3 / 100.0F, 0.0F, 1.0F);
            if (!pEntityLiving.isAutoSpinAttack()) {
                pPoseStack.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - pEntityLiving.getXRot())));
            }

            Vec3 vec3 = pEntityLiving.getViewVector(pPartialTicks);
            Vec3 vec31 = pEntityLiving.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0 && d1 > 0.0) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                pPoseStack.mulPose(Axis.YP.rotation((float)(Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
            f3 = !pEntityLiving.isInWater() && !pEntityLiving.isInFluidType((fluidType, height) -> pEntityLiving.canSwimInFluidType(fluidType)) ? -90.0F : -90.0F - pEntityLiving.getXRot();
            f2 = Mth.lerp(f, 0.0F, f3);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(f2));
            if (pEntityLiving.isVisuallySwimming()) {
                pPoseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(pEntityLiving, pPoseStack, pAgeInTicks, pRotationYaw, pPartialTicks);
        }

    }

}
