package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class MiningLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_FORTUNE.getEffectId(), 5, 0, 20*120, true), cap, target);
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.byId(BeyonderEffects.WHEEL_MINING.getEffectId(), 5, 0, 20*120, true), cap, target);
        target.sendSystemMessage(Component.translatable("potioneer.luck.fortune_event"));
    }
}
