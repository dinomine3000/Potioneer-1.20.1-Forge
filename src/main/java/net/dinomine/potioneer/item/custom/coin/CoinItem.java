package net.dinomine.potioneer.item.custom.coin;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class CoinItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public CoinItem(Properties pProperties) {
        super(pProperties);
    }

    private PlayState predicate(AnimationState animationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
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
    public InteractionResult useOn(UseOnContext pContext) {
        ItemStack pStack = pContext.getItemInHand();
        if(!pStack.hasTag()){
            CompoundTag tag = new CompoundTag();
            tag.putBoolean("potioneer_yesno", false);
            pStack.setTag(tag);
        } else {
            System.out.println(pStack.getTag());
            CompoundTag result = pStack.getTag();
            boolean level = result.getBoolean("potioneer_yesno");
            result.putBoolean("potioneer_yesno", !level);
            pStack.setTag(result);
        }
        return InteractionResult.SUCCESS;
    }
}
