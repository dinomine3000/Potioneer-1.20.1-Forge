package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderHungerRegenEffect extends BeyonderEffect {
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(cap.getLuckManager().passesLuckCheck(0.1f, 0, 0, target.getRandom())){
            Player player = ((Player) target);
            player.getFoodData().eat(player.getRandom().nextInt(4), player.getRandom().nextInt(2));
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }

}
