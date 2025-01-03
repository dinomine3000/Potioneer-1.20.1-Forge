package net.dinomine.potioneer.item.custom.BeyonderPotion;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.*;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class BeyonderPotionItem extends PotionItem implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public BeyonderPotionItem(Properties pProperties) {
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
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    public int getUseDuration(ItemStack pStack) {
        return 50;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public String getDescriptionId(ItemStack pStack) {
        return this.getDescriptionId();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        if(pEntityLiving instanceof Player player && !pLevel.isClientSide()){
            if(pStack.hasTag() && pStack.getTag().getBoolean("conflict")){
                    if(!player.isCreative()){
                        player.kill();
                        //reduce sequence
                    }
                player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
            }
        }
        if(pEntityLiving instanceof Player player && pLevel.isClientSide()){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
                if(pStack.hasTag()){
                    if(!pStack.getTag().getBoolean("conflict")){
                        if(player.level().isClientSide()) stats.attemptAdvancement(pStack.getTag().getInt("pathwayId"));
                    }
                }
            });
        }
        return super.finishUsingItem(pStack, pLevel, pEntityLiving);
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private BeyonderPotionItemRenderer renderer = null;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                // Don't instantiate until ready. This prevents race conditions breaking things
                if (this.renderer == null)
                    this.renderer = new BeyonderPotionItemRenderer();

                return renderer;
            }

        });
    }

}
