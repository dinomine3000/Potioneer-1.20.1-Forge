package net.dinomine.potioneer.item.custom.BeyonderPotion;

import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
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
        if(pStack.hasTag()){
            CompoundTag info = pStack.getTag().getCompound("potion_info");
            String name = info.getString("name");
            if(pEntityLiving instanceof Player player && !pLevel.isClientSide()){
                if(name.equals("conflict")){
                    if(!player.isCreative()){
                        player.kill();
                        //reduce sequence
                    }
                    player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
                }
            }
            if(pEntityLiving instanceof Player player && pLevel.isClientSide()){
                boolean beyonder = true;
                try {
                    Integer.parseInt(name);
                } catch (Exception e){
                    beyonder = false;
                }
                if(beyonder){
                    player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                        if(Math.floorDiv(Integer.parseInt(name), 10) != Math.floorDiv(cap.getPathwayId(), 10) && cap.isBeyonder()){
                            if(!player.isCreative()){
                                player.kill();
                                //reduce sequence
                            }
                            System.out.println("Pathway mismatch: " + name + " for pathway " + Math.floorDiv(cap.getPathwayId(), 10));
                            player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
                        } else {
                            ClientStatsData.attemptAdvancement(Integer.parseInt(name));
                        }
                    });
                }
            }
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
//
//    @Override
//    public InteractionResult useOn(UseOnContext pContext) {
//        ItemStack pStack = pContext.getItemInHand();
//        if(!pStack.hasTag()){
//            CompoundTag tag = new CompoundTag();
//            tag.putInt("level", 0);
//            pStack.setTag(tag);
//        } else {
//            System.out.println(pStack.getTag());
//            CompoundTag result = pStack.getTag();
//            int level = result.getInt("level");
//            result.putInt("level", (level+1)%(maxLevel+1));
//            pStack.setTag(result);
//        }
//        return InteractionResult.SUCCESS;
//    }
}
