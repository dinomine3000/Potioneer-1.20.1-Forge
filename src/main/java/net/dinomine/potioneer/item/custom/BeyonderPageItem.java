package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BeyonderPageItem extends Item {
    private static final float NEW_PAGE_CHANCE = 0.35f;
    public BeyonderPageItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        if(!heldItem.is(ModItems.BEYONDER_PAGE.get())) return new InteractionResultHolder<>(InteractionResult.FAIL, heldItem);
        if(pLevel.isClientSide()) return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);

        pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            int pageId = applyOrReadPageId(heldItem, (ServerLevel) pLevel, cap);
            cap.addPage(pageId);
            PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book, pageId), pPlayer);
        });

        if(PotioneerCommonConfig.CONSUME_PAGE_ON_USE.get()){
            pPlayer.setItemInHand(pUsedHand, ItemStack.EMPTY);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Override
    public Component getName(ItemStack pStack) {
        if(pStack.hasTag() && pStack.getTag().contains("pageId"))
            return Component.translatable("item.potioneer.page", PageRegistry.getPageById(pStack.getTag().getInt("pageId")).getTitle());
        return super.getName(pStack);
    }

    private static int applyOrReadPageId(ItemStack heldItem, ServerLevel pLevel, @NotNull LivingEntityBeyonderCapability cap) {
        if(heldItem.hasTag() && heldItem.getTag().contains("pageId")) return heldItem.getTag().getInt("pageId");
        List<Integer> pages = cap.getPageList();
        List<Integer> newPages = PageRegistry.getNewKeys(pages);
        List<Integer> nonDefaultPages = PageRegistry.getAllNonDefaultKeys();
        int pageId;
        if(!newPages.isEmpty() && cap.getLuckManager().passesLuckCheck(NEW_PAGE_CHANCE, 10, 10, pLevel.random)){
            pageId = newPages.get(pLevel.random.nextInt(newPages.size()));
        } else
            pageId = nonDefaultPages.get(pLevel.random.nextInt(nonDefaultPages.size()));
        heldItem.getOrCreateTag().putInt("pageId", pageId);
        return pageId;
    }

    public static ItemStack generatePage(int pageId){
        if(!PageRegistry.pageExists(pageId)) return ItemStack.EMPTY;
        ItemStack stack = new ItemStack(ModItems.BEYONDER_PAGE.get());
        CompoundTag tag = new CompoundTag();
        tag.putInt("pageId", pageId);
        stack.setTag(tag);
        return stack;
    }
}
