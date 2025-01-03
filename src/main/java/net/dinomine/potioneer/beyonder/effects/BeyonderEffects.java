package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderWaterAffinityEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;

public class BeyonderEffects {
    public enum EFFECT{
        TYRANT_WATER_AFFINITY,
        WHEEL_MINING,
    }

    public static BeyonderEffect byId(EFFECT id, int sequence, float cost, int duration, boolean active){
        return switch(id){
            case TYRANT_WATER_AFFINITY -> new BeyonderWaterAffinityEffect(sequence, cost, duration, active, EFFECT.TYRANT_WATER_AFFINITY);
            case WHEEL_MINING -> new BeyonderMiningSpeedEffect(sequence, cost, duration, active, EFFECT.WHEEL_MINING);
        };
    }
}
