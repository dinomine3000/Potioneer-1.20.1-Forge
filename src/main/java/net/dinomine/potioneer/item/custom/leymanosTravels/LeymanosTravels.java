package net.dinomine.potioneer.item.custom.leymanosTravels;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
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
    public static final RawAnimation OPEN = RawAnimation.begin().then("animation.book.opening", Animation.LoopType.PLAY_ONCE).thenLoop("animation.book.open");
    public static final RawAnimation CLOSED = RawAnimation.begin().then("animation.book.closing", Animation.LoopType.PLAY_ONCE).thenLoop("animation.book.closed");
    public static final RawAnimation CLOSED_LOOP = RawAnimation.begin().thenLoop("animation.book.closed");
    public static final RawAnimation OPEN_LOOP = RawAnimation.begin().thenLoop("animation.book.open");
    public static final RawAnimation FLIP_RIGHT = RawAnimation.begin().then("animation.book.flipRight", Animation.LoopType.PLAY_ONCE);
    public static final RawAnimation FLIP_LEFT = RawAnimation.begin().then("animation.book.flipLeft", Animation.LoopType.PLAY_ONCE);
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
                .triggerableAnim("open_book", OPEN)
                .triggerableAnim("close_book", CLOSED)
                .triggerableAnim("flip_right", FLIP_RIGHT)
                .triggerableAnim("flip_left", FLIP_LEFT));
    }

    private PlayState predicate(AnimationState animationState) {
        AnimationController controller = animationState.getController();
        ItemDisplayContext context = (ItemDisplayContext) animationState.getData(DataTickets.ITEM_RENDER_PERSPECTIVE);
        ItemStack stack = cachedStack.get();
//
//        if (context != ItemDisplayContext.FIRST_PERSON_LEFT_HAND &&
//                context != ItemDisplayContext.FIRST_PERSON_RIGHT_HAND &&
//                context != ItemDisplayContext.THIRD_PERSON_LEFT_HAND &&
//                context != ItemDisplayContext.THIRD_PERSON_RIGHT_HAND) {
//            controller.forceAnimationReset();
//            controller.setAnimation(CLOSED_LOOP);
//            return PlayState.STOP;
//        }

        if(controller.hasAnimationFinished() && controller.getCurrentAnimation().animation().name().contains("flip")){
            controller.setAnimation(OPEN_LOOP);
            return PlayState.CONTINUE;
        }

        if (stack != null && stack.hasTag()) {
            boolean open = stack.getOrCreateTag().getBoolean("potioneer_open");
            String currentAnim = controller.getCurrentAnimation() != null
                    ? controller.getCurrentAnimation().animation().name()
                    : "";

            if(open && currentAnim.equalsIgnoreCase("animation.book.closed")){
                controller.setAnimation(OPEN);
                return PlayState.CONTINUE;
            }
            if(!open && currentAnim.equalsIgnoreCase("animation.book.open")){
                controller.setAnimation(CLOSED);
                return PlayState.CONTINUE;
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public boolean onEntitySwing(ItemStack stack, LivingEntity entity) {
        if(!(entity instanceof Player player) || !player.isCrouching()) return false;
        if (player.getMainHandItem().hasTag()
            && player.getMainHandItem().getTag().contains("potioneer_open")
            && player.getMainHandItem().getTag().getBoolean("potioneer_open"))
         flipPage(player.level(), player.getMainHandItem(), player, false);
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {

        if(!pPlayer.isCrouching())
            openCloseBook(pLevel, pPlayer.getItemInHand(pUsedHand), pPlayer);
        else if (pPlayer.getMainHandItem().hasTag()
                && pPlayer.getMainHandItem().getTag().contains("potioneer_open")
                && pPlayer.getMainHandItem().getTag().getBoolean("potioneer_open"))
            flipPage(pLevel, pPlayer.getItemInHand(pUsedHand), pPlayer, true);
        return InteractionResultHolder.pass(pPlayer.getItemInHand(pUsedHand));
    }

//    @Override
//    public InteractionResult useOn(UseOnContext pContext) {
//        return useBook(pContext.getLevel(), pContext.getItemInHand(), pContext.getPlayer());
//    }

    private InteractionResult flipPage(Level level, ItemStack mainHandItem, Player player, boolean right){
        if(level.isClientSide()) return InteractionResult.PASS;
        long id = GeoItem.getOrAssignId(mainHandItem, (ServerLevel) level);
        triggerAnim(player, id, "book_controller", "flip_" + (right ? "right":"left"));
        return InteractionResult.FAIL;
    }

    private InteractionResult openCloseBook(Level level, ItemStack mainHandItem, Player player){
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
        pStack.getTag().putBoolean("potioneer_open", shouldOpenBook);
        if(shouldOpenBook){
            System.out.println("Opening book");
            triggerAnim(player, id, "book_controller", "open_book");
        } else {
            System.out.println("Closing book");
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
