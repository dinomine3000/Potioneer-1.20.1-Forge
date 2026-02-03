package net.dinomine.potioneer.item.custom.coin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.misc.DivinationResult;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.joml.Vector3f;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.Optional;
import java.util.function.Consumer;

public class CoinItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    private static final RawAnimation HEADS = RawAnimation.begin().thenLoop("animation.coin.loopHead");
    private static final RawAnimation TAILS = RawAnimation.begin().thenLoop("animation.coin.loopTails");


    public CoinItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    private PlayState predicate(AnimationState animationState) {
        AnimationController controller = animationState.getController();

        if(controller.hasAnimationFinished()){
            controller.setAnimation(controller.getCurrentAnimation().animation().name().equalsIgnoreCase("animation.coin.hTt") ? TAILS : HEADS);
            controller.forceAnimationReset();
        }
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "toss_controller", 0, this::predicate)
                .triggerableAnim("coin_toss_tails", RawAnimation.begin().thenPlay("animation.coin.hTt"))
                .triggerableAnim("coin_toss_heads", RawAnimation.begin().thenPlay("animation.coin.tTh")));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            public static final HumanoidModel.ArmPose CUSTOM_FORWARD_POSE =
                    HumanoidModel.ArmPose.create(
                            "FORWARD_POSE",
                            false,
                            (model, entity, arm) -> {
                                model.rightArm.xRot = -((float)Math.PI / 3); // point arm forward
                                model.rightArm.yRot = 0f;
                            }
                    );
            @Override
            public HumanoidModel.ArmPose getArmPose(LivingEntity entity, InteractionHand hand, ItemStack itemStack) {
                if (!itemStack.isEmpty()) {
                    // return your custom pose when holding this item
                    return CUSTOM_FORWARD_POSE;
                }
                return HumanoidModel.ArmPose.EMPTY;
            }

            private CoinItemRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Don't instantiate until ready. This prevents race conditions breaking things
                if (this.renderer == null)
                    this.renderer = new CoinItemRenderer();

                return renderer;
            }

        });
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        InteractionResult res = useCoin(pLevel, pPlayer.getMainHandItem(), pPlayer);

        return new InteractionResultHolder<>(res, pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        return useCoin(pContext.getLevel(), pContext.getItemInHand(), pContext.getPlayer());
    }

    private InteractionResult useCoin(Level level, ItemStack mainHandItem, Player player){
        if(!player.getMainHandItem().is(this)) return InteractionResult.PASS;
        if(level.isClientSide()){
            return InteractionResult.CONSUME_PARTIAL;
        }
        long id = GeoItem.getOrAssignId(mainHandItem, (ServerLevel) level);
        level.playSound(null, player.getOnPos(), ModSounds.COIN.get(), SoundSource.PLAYERS, 1f, 0.95f + player.getRandom().nextFloat()/10f);

        ItemStack pStack = mainHandItem;

        boolean newState = getDivinationResult(player, player.getOffhandItem());

        if(!pStack.hasTag() || !pStack.getTag().contains("potioneer_yesno")){
            CompoundTag tag = pStack.getTag();
            if (tag == null) tag = new CompoundTag();
            tag.putBoolean("potioneer_yesno", newState);
            pStack.setTag(tag);
        } else {
            CompoundTag result = pStack.getTag();
            result.putBoolean("potioneer_yesno", newState);
            pStack.setTag(result);
        }
        triggerAnim(player, id, "toss_controller", "coin_toss_" + (newState ? "heads" : "tails"));
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResult.CONSUME_PARTIAL;
    }

    private boolean getDivinationResult(Player player, ItemStack divinationTarget){

        int sequence = -1;
        boolean seer = false;
        boolean appraiser = false;
        boolean lucky = false;
        if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
            Optional<LivingEntityBeyonderCapability> cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
            sequence = cap.get().getPathwaySequenceId();
            seer = cap.get().getEffectsManager().hasEffect(BeyonderEffects.MISC_DIVINATION.getEffectId());
            appraiser = cap.get().getAbilitiesManager().hasAbility(Abilities.APPRAISAL.getAblId());
            lucky = cap.get().getAbilitiesManager().hasAbility(Abilities.WHEEL_DIVINATION.getAblId()) && cap.get().getLuckManager().passesLuckCheck(0.7f, 0, 0, player.getRandom());
        }

        DivinationResult result = MysticismHelper.doDivination(divinationTarget, player, sequence, player.getRandom());
        if(seer){
            MysticismHelper.updateOrApplyMysticismTag(player.getMainHandItem(), 0.2f, player);
            return result.yesNo();
        } else if(lucky || (appraiser && divinationTarget.is(ModItems.BEYONDER_POTION.get()))){
            return result.yesNo();
        } else {
            if(player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
                Optional<LivingEntityBeyonderCapability> cap = player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                float luck = cap.get().getLuckManager().checkLuck(0.5f);
                if(player.getRandom().nextFloat() < luck) return result.yesNo();
                return player.getRandom().nextBoolean();
            }
        }

        return player.getRandom().nextBoolean();
    }

}
