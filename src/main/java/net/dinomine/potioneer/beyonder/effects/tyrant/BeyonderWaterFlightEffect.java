package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderWaterFlightEffect extends BeyonderEffect {
    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(target instanceof Player player){
            if(TyrantPathway.isInWater(player) && cap.getSequenceLevel() < 9 && cap.getSpirituality() > 0){
                //TODO check if cost should be in effects or abilities
                if(player.getAbilities().flying) cap.requestPassiveSpiritualityCost(3f);
                cap.getEffectsManager().statsHolder.enableFlight();
            }
        }
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }
}
