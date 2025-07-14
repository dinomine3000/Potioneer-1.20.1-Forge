package net.dinomine.potioneer.item.custom.leymanosTravels;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.animatable.SingletonGeoAnimatable;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class LeymanosTravels extends Item implements GeoItem {
    public static final RawAnimation OPEN = RawAnimation.begin().thenLoop("animation.book.open");
    public static final RawAnimation CLOSED = RawAnimation.begin().thenLoop("animation.book.closed");
    private static final ThreadLocal<ItemStack> cachedStack = new ThreadLocal<>();

    public static void capture(ItemStack stack) {
        cachedStack.set(stack);
    }

    public static void clear() {
        cachedStack.remove();
    }

    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public LeymanosTravels(Properties pProperties) {
        super(pProperties);
        SingletonGeoAnimatable.registerSyncedAnimatable(this);
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "book_controller", 0, this::predicate)
                .triggerableAnim("open_book", RawAnimation.begin().then("animation.book.triggerOpen", Animation.LoopType.PLAY_ONCE))
                .triggerableAnim("close_book", RawAnimation.begin().then("animation.book.triggerClose", Animation.LoopType.PLAY_ONCE)));
    }

    private PlayState predicate(AnimationState animationState) {
        AnimationController controller = animationState.getController();
        ItemDisplayContext obj = (ItemDisplayContext) animationState.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);

        if(obj != ItemDisplayContext.FIRST_PERSON_LEFT_HAND &&
                obj != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND){
            controller.setAnimation(CLOSED);
            controller.forceAnimationReset();
            return PlayState.STOP;
        }

        // If animation is finished, decide what to loop
        if (controller.hasAnimationFinished()) {
            String current = controller.getCurrentAnimation() != null
                    ? controller.getCurrentAnimation().animation().name()
                    : "";

            if (current.equals("animation.book.triggerClose")) {
                controller.setAnimation(RawAnimation.begin().thenPlay("animation.book.closing")
                        .thenLoop("animation.book.closed"));
            } else {
                controller.setAnimation(RawAnimation.begin().thenPlay("animation.book.opening")
                        .thenLoop("animation.book.open"));
            }

            controller.forceAnimationReset();
        }

        return PlayState.CONTINUE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        useBook(pLevel, pPlayer.getItemInHand(pUsedHand), pPlayer);

        return InteractionResultHolder.fail(pPlayer.getItemInHand(pUsedHand));
    }

//    @Override
//    public InteractionResult useOn(UseOnContext pContext) {
//        return useBook(pContext.getLevel(), pContext.getItemInHand(), pContext.getPlayer());
//    }


    private InteractionResult useBook(Level level, ItemStack mainHandItem, Player player){
        if(level.isClientSide()) return InteractionResult.PASS;
        long id = GeoItem.getOrAssignId(mainHandItem, (ServerLevel) level);

        ItemStack pStack = mainHandItem;
        if(!mainHandItem.is(this) || (mainHandItem.is(this) && player.getOffhandItem().is(this)))

        if(!pStack.hasTag() || !pStack.getTag().contains("potioneer_open")){
            CompoundTag tag = pStack.getOrCreateTag();
            tag.putBoolean("potioneer_open", false);
            pStack.setTag(tag);
        }
        boolean shouldOpenBook = !pStack.getTag().getBoolean("potioneer_open");
        System.out.println("Should open book: " + shouldOpenBook);
        pStack.getTag().putBoolean("potioneer_open", shouldOpenBook);
        if(shouldOpenBook){
            triggerAnim(player, id, "book_controller", "open_book");
        } else {
            triggerAnim(player, id, "book_controller", "close_book");
        }
        return InteractionResult.FAIL;
    }

    @Override
    public boolean isPerspectiveAware() {
            return true;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private LeymanosTravelsRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Don't instantiate until ready. This prevents race conditions breaking things
                if (this.renderer == null)
                    this.renderer = new LeymanosTravelsRenderer();

                return renderer;
            }

        });
    }
}
