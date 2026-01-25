package net.dinomine.potioneer.event;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class DurabilityHurtEvent extends LivingEvent {
    private int amount;
    private final ItemStack stack;
    public DurabilityHurtEvent(LivingEntity entity, int amount, ItemStack stack) {
        super(entity);
        this.stack = stack;
        this.amount = amount;
    }

    public ItemStack getStack(){
        return stack;
    }

    public int getAmount(){
        return amount;
    }

    public void multAmount(float mult){
        this.amount = (int)(mult * amount);
    }

    public void changeAmount(int diff){
        this.amount += diff;
    }
}
