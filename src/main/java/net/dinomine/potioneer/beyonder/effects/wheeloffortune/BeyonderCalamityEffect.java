package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;
import java.util.function.Function;

public class BeyonderCalamityEffect extends BeyonderEffect {
    private static final Function<Integer, Integer> addedChance = level -> 2 + Math.max(7-level, 0);
    private static final UUID luckAttributeUUID = UUID.fromString("3aa8f6cd-4039-427b-98f1-a52c0825a5f9");

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel <= 7)
            cap.getLuckManager().chanceLuckEventChange(luckAttributeUUID, addedChance.apply(getSequenceLevel()));
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int numArtifacts = cap.getAbilitiesManager().getNumArtifacts();
        if(sequenceLevel <= 7){
            cap.getLuckManager().changeLuckRange(luckAttributeUUID, 100, 100, -numArtifacts*25);
            return;
        }
        cap.getLuckManager().changeLuckRange(luckAttributeUUID, 0, 0, -numArtifacts*25);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(luckAttributeUUID);
        cap.getLuckManager().removeLuckEventModifier(luckAttributeUUID);
    }
}
