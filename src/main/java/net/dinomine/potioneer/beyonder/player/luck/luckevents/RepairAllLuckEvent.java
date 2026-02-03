package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class RepairAllLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        for(ItemStack stack: target.getAllSlots()){
            if(!stack.isDamageableItem()) continue;
            if(luck.passesLuckCheck(0.9f, 0, 10, target.getRandom())){
                stack.setDamageValue(0);
            }
        }
    }
}
