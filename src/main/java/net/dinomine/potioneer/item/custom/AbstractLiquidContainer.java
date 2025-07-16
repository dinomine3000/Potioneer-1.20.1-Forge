package net.dinomine.potioneer.item.custom;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;

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
