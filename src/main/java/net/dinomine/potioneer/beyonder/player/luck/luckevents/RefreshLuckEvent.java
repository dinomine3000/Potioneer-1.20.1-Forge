package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class RefreshLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        cap.setSanity(-1);
        cap.setSpirituality(-1);
        target.setHealth(target.getMaxHealth());
        if(target instanceof Player player){
            player.getFoodData().setFoodLevel(20);
            player.getFoodData().setSaturation(20);
            player.getFoodData().setExhaustion(0);
        }
        for(MobEffectInstance effect: target.getActiveEffects()){
            if(effect.getEffect().isBeneficial()) continue;
            target.removeEffect(effect.getEffect());
        }
    }
}
