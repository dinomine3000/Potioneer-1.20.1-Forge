package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderMysticismEffect;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderPlagueEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.*;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderCraftingSpiritualityEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderDurabilityEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderXpReduceEffect;
import net.dinomine.potioneer.beyonder.effects.redpriest.*;
import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderElectrificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderWaterAffinityEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;

public class BeyonderEffects {
    /*public static final DeferredRegister<BeyonderEffectType> REGISTRY =
            DeferredRegister.create(new ResourceLocation(Potioneer.MOD_ID, "beyonder_effects"), Potioneer.MOD_ID);


    public static void init(IEventBus bus) {
        REGISTRY.makeRegistry(RegistryBuilder::new);
        REGISTRY.register(bus);
    }

    public static final RegistryObject<BeyonderEffectType> RED_WEAPON_PROFICIENCY =
            REGISTRY.register("red_weapon_proficiency", () ->
                    new BeyonderEffectType(
                            BeyonderWeaponProficiencyEffect::new
                    )
            );

    public static final RegistryObject<BeyonderEffectType> MISC_MYST =
            REGISTRY.register("misc_mist", () ->
                    new BeyonderEffectType(
                            BeyonderMysticismEffect::new
                    )
            );*/

    public enum EFFECT{
        TYRANT_WATER_AFFINITY,
        TYRANT_ELECTRIFICATION,
        RED_WEAPON_PROFICIENCY,
        RED_FIRE_BUFF,
        RED_FIRE_AURA,
        RED_LIGHT_BUFF,
        RED_PURIFICATION,
        PARAGON_DURABILITY_REGEN,
        PARAGON_XP_REDUCE,
        PARAGON_CRAFTING_SPIRITUALITY,
        MYSTERY_INVIS,
        MYSTERY_FALL,
        MYSTERY_FALL_NEGATE,
        MYSTERY_REGEN,
        MYSTERY_REACH,
        MYSTERY_STEP,
        MYSTERY_FIGURINE,
        WHEEL_TEMP_LUCK,
        WHEEL_SILK_TOUCH,
        WHEEL_FORTUNE,
        WHEEL_MINING,
        WHEEL_DAMAGE_REDUCE,
        WHEEL_GAMBLING,
        MISC_MYST,
        MISC_PLAGUE,

    }

    public static BeyonderEffect byId(EFFECT id, int sequence, float cost, int duration, boolean active){
        return switch(id){
            case TYRANT_WATER_AFFINITY -> new BeyonderWaterAffinityEffect(sequence, cost, duration, active, EFFECT.TYRANT_WATER_AFFINITY);
            case TYRANT_ELECTRIFICATION -> new BeyonderElectrificationEffect(sequence, cost, duration, active, EFFECT.TYRANT_ELECTRIFICATION);
            case WHEEL_MINING -> new BeyonderMiningSpeedEffect(sequence, cost, duration, active, EFFECT.WHEEL_MINING);
            case RED_WEAPON_PROFICIENCY -> new BeyonderWeaponProficiencyEffect(sequence, cost, duration, active, EFFECT.RED_WEAPON_PROFICIENCY);
            case RED_FIRE_BUFF -> new BeyonderFireBuffEffect(sequence, cost, duration, active, EFFECT.RED_FIRE_BUFF);
            case RED_FIRE_AURA -> new BeyonderFireAuraEffect(sequence, cost, duration, active, EFFECT.RED_FIRE_AURA);
            case RED_LIGHT_BUFF -> new BeyonderLightBuffEffect(sequence, cost, duration, active, EFFECT.RED_LIGHT_BUFF);
            case RED_PURIFICATION -> new BeyonderPurificationEffect(sequence, cost, duration, active, EFFECT.RED_PURIFICATION);
            case MYSTERY_REGEN -> new BeyonderRegenEffect(sequence, cost, duration, active, EFFECT.MYSTERY_REGEN);
            case MYSTERY_INVIS -> new BeyonderInvisibilityEffect(sequence, cost, duration, active, EFFECT.MYSTERY_INVIS);
            case MYSTERY_FALL -> new BeyonderFallDmgReduceEffect(sequence, cost, duration, active, EFFECT.MYSTERY_FALL);
            case MYSTERY_FALL_NEGATE -> new BeyonderFallDmgCancelEffect(sequence, cost, duration, active, EFFECT.MYSTERY_FALL_NEGATE);
            case MYSTERY_REACH -> new BeyonderExtendedReachEffect(sequence, cost, duration, active, EFFECT.MYSTERY_REACH);
            case MYSTERY_STEP -> new BeyonderStepUpEffect(sequence, cost, duration, active, EFFECT.MYSTERY_STEP);
            case MYSTERY_FIGURINE -> new BeyonderFigurineEffect(sequence, cost, duration, active, EFFECT.MYSTERY_FIGURINE);
            case PARAGON_DURABILITY_REGEN -> new BeyonderDurabilityEffect(sequence, cost, duration, active, EFFECT.PARAGON_DURABILITY_REGEN);
            case PARAGON_XP_REDUCE -> new BeyonderXpReduceEffect(sequence, cost, duration, active, EFFECT.PARAGON_XP_REDUCE);
            case WHEEL_TEMP_LUCK -> new BeyonderTempLuckEffect(sequence, cost, duration, active, EFFECT.WHEEL_TEMP_LUCK);
            case WHEEL_SILK_TOUCH -> new BeyonderSilkTouchEffect(sequence, cost, duration, active, EFFECT.WHEEL_SILK_TOUCH);
            case WHEEL_FORTUNE -> new BeyonderFortuneEffect(sequence, cost, duration, active, EFFECT.WHEEL_FORTUNE);
            case WHEEL_DAMAGE_REDUCE -> new BeyonderLuckReduceDamageEffect(sequence, cost, duration, active, EFFECT.WHEEL_DAMAGE_REDUCE);
            case WHEEL_GAMBLING -> new BeyonderGamblingEffect(sequence, cost, duration, active, EFFECT.WHEEL_GAMBLING);
            case PARAGON_CRAFTING_SPIRITUALITY -> new BeyonderCraftingSpiritualityEffect(sequence, cost, duration, active, EFFECT.PARAGON_CRAFTING_SPIRITUALITY);
            case MISC_MYST -> new BeyonderMysticismEffect(sequence, cost, duration, active, EFFECT.MISC_MYST);
            case MISC_PLAGUE -> new BeyonderPlagueEffect(sequence, cost, duration, active, EFFECT.MISC_PLAGUE);
        };
    }
}
