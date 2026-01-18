package net.dinomine.potioneer.item.custom.BeyonderPotion;

import net.dinomine.potioneer.beyonder.client.ClientAdvancementManager;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.advancement.BeginAdvancementMessage;
import net.dinomine.potioneer.util.GeoTintable;
import net.dinomine.potioneer.util.misc.CharacteristicHelper;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;
import software.bernie.geckolib.util.RenderUtils;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class BeyonderPotionItem extends PotionItem implements GeoItem, GeoTintable {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final ThreadLocal<ItemStack> cachedStack = new ThreadLocal<>();

    public static void capture(ItemStack stack) {
        cachedStack.set(stack);
    }

    public static void clear() {
        cachedStack.remove();
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    public BeyonderPotionItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void inventoryTick(ItemStack pStack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(pStack, pLevel, pEntity, pSlotId, pIsSelected);
    }

    private PlayState predicate(AnimationState animationState) {
        return PlayState.CONTINUE;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllerRegistrar) {
        controllerRegistrar.add(new AnimationController(this, "controller", 0, this::predicate));
    }

    public int getUseDuration(ItemStack pStack) {
        return 50;
    }

    @Override
    public double getTick(Object itemStack) {
        return RenderUtils.getCurrentTick();
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltip, TooltipFlag pFlag) {}

    @Override
    public Component getName(ItemStack pStack) {
        //they keep replacing the name of my damn item
        return Component.translatable("item.potioneer.beyonder_potion");
    }

    @Override
    public ItemStack finishUsingItem(ItemStack pStack, Level pLevel, LivingEntity pEntityLiving) {
        if(!pStack.hasTag()) return super.finishUsingItem(pStack, pLevel, pEntityLiving);
        if(!(pEntityLiving instanceof Player player)) return super.finishUsingItem(pStack, pLevel, pEntityLiving);
        CompoundTag info = pStack.getTag().getCompound("potion_info");
        String name = info.getString("name");

        boolean validPotion = true;
        try {
            Integer.parseInt(name);
        } catch (Exception e){
            validPotion = name.equals("conflict");
        }
        if(!validPotion) return super.finishUsingItem(pStack, pLevel, pEntityLiving);
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(pLevel.isClientSide()) return;
            if(name.equals("conflict")){
                if(!player.isCreative()){
                    cap.setSanity(0);
                }
                player.sendSystemMessage(Component.literal("Lost control on the spot. oh well."));
                return;
            }
            int pathwaySequenceId = Integer.parseInt(name);
            BeyonderPathway newPathway = Pathways.getPathwayById(Math.floorDiv(pathwaySequenceId, 10));
            int addedDifficulty = newPathway.isRitualComplete(pathwaySequenceId%10, player, pLevel);

            int originalPathSeqId = cap.getPathwaySequenceId();
            BeyonderPathway ogPathway = Pathways.getPathwayById(Math.floorDiv(originalPathSeqId, 10));
            //if changing pathways
            if(ogPathway != newPathway){
                //sequence level for changing check
                if(pathwaySequenceId%10 >= PotioneerCommonConfig.MIN_SEQUENCE_TO_SWITCH_PATHWAYS.get()) addedDifficulty += ClientAdvancementManager.DIFF_CHANGE_INVALID_LEVEL;
                //pathway group check
                List<String> intPaths = PotioneerCommonConfig.INTERCHANGEABLE_PATHWAYS.get();
                boolean matchGroup = false;
                String ogPathId = String.valueOf(Math.floorDiv(originalPathSeqId, 10));
                String newPathId = String.valueOf(Math.floorDiv(pathwaySequenceId, 10));
                for(String group: intPaths){
                    if (group.contains(ogPathId) && group.contains(newPathId)) {
                        matchGroup = true;
                        break;
                    }
                }
                if(!matchGroup) addedDifficulty += ClientAdvancementManager.DIFF_CHANGE_INVALID_GROUP;
            }
            PacketHandler.sendMessageSTC(new BeginAdvancementMessage(pathwaySequenceId, addedDifficulty), player);
        });
        return super.finishUsingItem(pStack, pLevel, pEntityLiving);
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

    @Override
    public int getHexColor() {
        ItemStack stack = cachedStack.get();
        if(stack != null && stack.is(this) && stack.hasTag() && stack.getTag().contains("potion_info")){
            return stack.getTag().getCompound("potion_info").getInt("color");
        }

        return 16742143;
    }
}
