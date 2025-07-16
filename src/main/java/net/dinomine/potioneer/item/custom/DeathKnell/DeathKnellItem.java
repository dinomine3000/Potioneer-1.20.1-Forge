package net.dinomine.potioneer.item.custom.DeathKnell;

import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.ClientUtils;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class DeathKnellItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final RawAnimation FIRE = RawAnimation.begin().then("fire", Animation.LoopType.PLAY_ONCE);

    public DeathKnellItem(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController<>(this, "gun_controller", 0, this::predicate)
                .triggerableAnim("shoot", FIRE)
                .setSoundKeyframeHandler(state -> {
                    // Use helper method to avoid client-code in common class
                    Player player = ClientUtils.getClientPlayer();
                    switch(state.getKeyframeData().getSound()){
                        case "revolver_shot":
                            player.playSound(ModSounds.GUN_SHOOT.get(), 0.6f, 1.25f - player.getRandom().nextFloat()/2f);
                            break;
                        case "gun_reload_sfx":
                            player.playSound(ModSounds.GUN_RELOAD.get(), 0.6f, 1.25f - player.getRandom().nextFloat()/2f);
                            break;
                        case "cloth2":
                            player.playSound(ModSounds.GUN_CLOTH.get(), 0.6f, 1.25f - player.getRandom().nextFloat()/2f);
                            break;
                        default:
                            break;
                    }
                }));
    }

    private PlayState predicate(AnimationState animationState) {
        if(animationState.isCurrentAnimation(FIRE) && !animationState.getController().hasAnimationFinished()) return PlayState.STOP;
        animationState.getController().setAnimation(RawAnimation.begin().then("reload", Animation.LoopType.HOLD_ON_LAST_FRAME));
        return PlayState.CONTINUE;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if(entity.getMainHandItem().is(ModItems.DEATH_KNELL.get())){
            ItemStack mainHandItem = entity.getMainHandItem();
            long id = GeoItem.getId(mainHandItem);
            DeathKnellItem delegate = (DeathKnellItem) ModItems.DEATH_KNELL.get();
            delegate.triggerAnim(entity, id, "gun_controller", "shoot");
            return true;
        }
        return super.onEntitySwing(stack, entity);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if(!pLevel.isClientSide())
            triggerAnim(pPlayer, GeoItem.getOrAssignId(pPlayer.getItemInHand(pUsedHand), (ServerLevel) pLevel), "gun_controller", "shoot");
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    //    @Override
//    public boolean isPerspectiveAware() {
//            return true;
//    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private DeathKnellRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Don't instantiate until ready. This prevents race conditions breaking things
                if (this.renderer == null)
                    this.renderer = new DeathKnellRenderer();

                return renderer;
            }

        });
    }
}
