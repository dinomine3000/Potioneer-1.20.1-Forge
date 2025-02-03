package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFallDmgCancelEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFallDmgReduceEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderInvisibilityEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderRegenEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderCraftingBonusEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderDurabilityEffect;
import net.dinomine.potioneer.beyonder.effects.redpriest.BeyonderWeaponProficiencyEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderWaterAffinityEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderFortuneEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderSilkTouchEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderTempLuckEffect;

public class BeyonderEffects {
    //change to registries. check relics code for help
    public enum EFFECT{
        TYRANT_WATER_AFFINITY,
        WHEEL_MINING,
        PARAGON_CRAFTING_BONUS,
        RED_WEAPON_PROFICIENCY,
        MYSTERY_REGEN,
        WHEEL_FORTUNE,
        PARAGON_DURABILITY_REGEN,
        MYSTERY_INVIS,
        MYSTERY_FALL,
        MYSTERY_FALL_NEGATE,
        WHEEL_TEMP_LUCK,
        WHEEL_SILK_TOUCH
    }

    public static BeyonderEffect byId(EFFECT id, int sequence, float cost, int duration, boolean active){
        return switch(id){
            case TYRANT_WATER_AFFINITY -> new BeyonderWaterAffinityEffect(sequence, cost, duration, active, EFFECT.TYRANT_WATER_AFFINITY);
            case WHEEL_MINING -> new BeyonderMiningSpeedEffect(sequence, cost, duration, active, EFFECT.WHEEL_MINING);
            case PARAGON_CRAFTING_BONUS -> new BeyonderCraftingBonusEffect(sequence, cost, duration, active, EFFECT.PARAGON_CRAFTING_BONUS);
            case RED_WEAPON_PROFICIENCY -> new BeyonderWeaponProficiencyEffect(sequence, cost, duration, active, EFFECT.RED_WEAPON_PROFICIENCY);
            case MYSTERY_REGEN -> new BeyonderRegenEffect(sequence, cost, duration, active, EFFECT.MYSTERY_REGEN);
            case PARAGON_DURABILITY_REGEN -> new BeyonderDurabilityEffect(sequence, cost, duration, active, EFFECT.PARAGON_DURABILITY_REGEN);
            case MYSTERY_INVIS -> new BeyonderInvisibilityEffect(sequence, cost, duration, active, EFFECT.MYSTERY_INVIS);
            case MYSTERY_FALL -> new BeyonderFallDmgReduceEffect(sequence, cost, duration, active, EFFECT.MYSTERY_FALL);
            case MYSTERY_FALL_NEGATE -> new BeyonderFallDmgCancelEffect(sequence, cost, duration, active, EFFECT.MYSTERY_FALL_NEGATE);
            case WHEEL_TEMP_LUCK -> new BeyonderTempLuckEffect(sequence, cost, duration, active, EFFECT.WHEEL_TEMP_LUCK);
            case WHEEL_SILK_TOUCH -> new BeyonderSilkTouchEffect(sequence, cost, duration, active, EFFECT.WHEEL_SILK_TOUCH);
            case WHEEL_FORTUNE -> new BeyonderFortuneEffect(sequence, cost, duration, active, EFFECT.WHEEL_FORTUNE);
        };
    }
}
