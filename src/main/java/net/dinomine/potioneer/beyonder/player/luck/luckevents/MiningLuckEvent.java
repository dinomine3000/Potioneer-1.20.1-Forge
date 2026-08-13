package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class MiningLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(BeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_FORTUNE.getEffectId(), 5, 0, 20*120, true), cap, target);
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_MINING.getEffectId(), 5, 0, 20*120, true), cap, target);
        target.sendSystemMessage(Component.translatable("luck.potioneer.fortune_event"));
    }
}
