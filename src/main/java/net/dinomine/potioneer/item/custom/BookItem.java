package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BookItem extends Item {

    public BookItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide()) return InteractionResultHolder.success(stack);
        PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book), pPlayer);
        return super.use(pLevel, pPlayer, pUsedHand);
    }
}
