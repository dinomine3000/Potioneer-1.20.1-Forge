package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderTempLuckEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().consumeLuck(60);
        target.sendSystemMessage(Component.literal("All is not without its price. Your luck has been taken back."));
        if (cap.getLuckManager().getLuck() < 0){
            target.sendSystemMessage(Component.literal("Unlucky..."));
            cap.getLuckManager().instantlyCastEvent(target);
        }
    }
}
