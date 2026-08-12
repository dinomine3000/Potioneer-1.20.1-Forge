package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;

public class DigestPotionLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(BeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        int pathseq = cap.getPathwaySequenceId();
        cap.getCharacteristicManager().progressActing(0.25f, pathseq);
    }
}
