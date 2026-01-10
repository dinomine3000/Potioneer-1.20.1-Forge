package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderHungerRegenEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getLuckManager().passesLuckCheck(0.1f, 0, 0, target.getRandom())){
            Player player = ((Player) target);
            player.getFoodData().eat(player.getRandom().nextInt(4), player.getRandom().nextInt(2));
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

}
