package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderInvisibilityEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderRegenEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderCraftingBonusEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderDurabilityEffect;
import net.dinomine.potioneer.beyonder.effects.redpriest.BeyonderWeaponProficiencyEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderWaterAffinityEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderHideInBlockEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;

public class BeyonderEffects {
    //change to registries. check relics code for help
    public enum EFFECT{
        TYRANT_WATER_AFFINITY,
        WHEEL_MINING,
        PARAGON_CRAFTING_BONUS,
        RED_WEAPON_PROFICIENCY,
        MYSTERY_REGEN,
        WHEEL_BLOCK,
        PARAGON_DURABILITY_REGEN,
        MYSTERY_INVIS
    }

    public static BeyonderEffect byId(EFFECT id, int sequence, float cost, int duration, boolean active){
        return switch(id){
            case TYRANT_WATER_AFFINITY -> new BeyonderWaterAffinityEffect(sequence, cost, duration, active, EFFECT.TYRANT_WATER_AFFINITY);
            case WHEEL_MINING -> new BeyonderMiningSpeedEffect(sequence, cost, duration, active, EFFECT.WHEEL_MINING);
            case PARAGON_CRAFTING_BONUS -> new BeyonderCraftingBonusEffect(sequence, cost, duration, active, EFFECT.PARAGON_CRAFTING_BONUS);
            case RED_WEAPON_PROFICIENCY -> new BeyonderWeaponProficiencyEffect(sequence, cost, duration, active, EFFECT.RED_WEAPON_PROFICIENCY);
            case MYSTERY_REGEN -> new BeyonderRegenEffect(sequence, cost, duration, active, EFFECT.MYSTERY_REGEN);
            case WHEEL_BLOCK -> new BeyonderHideInBlockEffect(sequence, cost, duration, active, EFFECT.WHEEL_BLOCK);
            case PARAGON_DURABILITY_REGEN -> new BeyonderDurabilityEffect(sequence, cost, duration, active, EFFECT.PARAGON_DURABILITY_REGEN);
            case MYSTERY_INVIS -> new BeyonderInvisibilityEffect(sequence, cost, duration, active, EFFECT.MYSTERY_INVIS);
        };
    }
}
