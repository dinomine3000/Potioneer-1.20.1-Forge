package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class ShrinkItemLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        ItemStack stack = target.getMainHandItem();
        if(!luck.passesLuckCheck(0.5f, 50, 50, target.getRandom())){
            int shrinkAmount = luck.getRandomNumber(1, 10, false, target.getRandom());
            stack.shrink(shrinkAmount);
            luck.grantLuck(shrinkAmount*10);
            target.level().playSound(null, target.getOnPos(), SoundEvents.ITEM_BREAK, SoundSource.NEUTRAL, 1, 1);
        }
    }
}
