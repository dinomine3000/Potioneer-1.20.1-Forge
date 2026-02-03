package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class DamageToolsLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        if(target.getMainHandItem().isDamageableItem())
            target.getMainHandItem().hurtAndBreak(luck.getRandomNumber(0, 10, false, target.getRandom()), target, ignored -> {});
        for(ItemStack stack: target.getAllSlots()){
            if(!stack.isDamageableItem()) continue;
            if(!luck.passesLuckCheck(0.9f, 0, 10, target.getRandom())){
                stack.hurtAndBreak(luck.getRandomNumber(0, 100, false, target.getRandom()), target, ignored -> {});
            }
        }
    }
}
