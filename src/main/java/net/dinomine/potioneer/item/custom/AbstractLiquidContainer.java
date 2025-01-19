package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractLiquidContainer extends Item {
    int maxLevel;
    String name;

    public AbstractLiquidContainer(Properties pProperties, int maxLevel) {
        super(pProperties);
        this.maxLevel = maxLevel;
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

    @Override
    public Component getName(ItemStack pStack) {
        if(pStack.hasTag()){
            if(!pStack.getTag().getCompound("potion_info").isEmpty()){
                String name = pStack.getTag().getCompound("potion_info").getString("name");
                return Component.translatable(this.getDescriptionId() + "." + name);
            }
        }
        return super.getName(pStack);
    }

    public static void registerColor(RegisterColorHandlersEvent.Item event){

    }

    @OnlyIn(Dist.CLIENT)
    public static class LiquidContainerTint implements ItemColor {

        @Override
        public int getColor(ItemStack itemStack, int i) {
            if(i != 1) return -1;
            if(itemStack.hasTag()){
                if(!itemStack.getTag().getCompound("potion_info").isEmpty()){
                    return itemStack.getTag().getCompound("potion_info").getInt("color");
                }
            }
            return 0x00000000;
        }
    }
}
