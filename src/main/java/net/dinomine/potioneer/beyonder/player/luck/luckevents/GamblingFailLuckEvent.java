package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderGamblingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;

public class GamblingFailLuckEvent extends LuckEvent {

    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        BeyonderGamblingEffect.applyNegativeEffect(cap, target, luck, 5, 5*20, 30*20, 4, target.getRandom());
    }
}
