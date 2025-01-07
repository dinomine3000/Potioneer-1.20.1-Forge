package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

public abstract class AbstractLiquidContainer extends Item {
    int maxLevel;
    String name;

    public AbstractLiquidContainer(Properties pProperties, int maxLevel) {
        super(pProperties);
        this.maxLevel = maxLevel;
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        Level pLevel = pContext.getLevel();
        Player pPlayer = pContext.getPlayer();
        ItemStack pStack = pContext.getItemInHand();
        if(!pStack.hasTag()){
            CompoundTag tag = new CompoundTag();
            tag.putInt("level", 0);
            pStack.setTag(tag);
        } else {
            System.out.println(pStack.getTag());
            CompoundTag result = pStack.getTag();
            int level = result.getInt("level");
            result.putInt("level", (level+1)%(maxLevel+1));
            pStack.setTag(result);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public Component getName(ItemStack pStack) {
        if(pStack.getTag() != null && !pStack.getTag().getString("name").isEmpty()){
            String name = pStack.getTag().getString("name");
            return Component.translatable(this.getDescriptionId() + "." + name);
        }
        return super.getName(pStack);
    }

    public static void registerColor(RegisterColorHandlersEvent.Item event){
        event.register(((itemStack, i) -> {
            return i == 0 ? -1 : 0xFF00FF00;
        }), ModItems.VIAL.get(), ModItems.FLASK.get());
    }
}
