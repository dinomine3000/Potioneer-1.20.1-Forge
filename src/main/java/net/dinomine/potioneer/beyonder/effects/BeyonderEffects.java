package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.beyonder.effects.misc.BeyonderCogitationEffect;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderHungerRegenEffect;
import net.dinomine.potioneer.beyonder.effects.misc.BlankEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderExtendedReachEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFallDmgCancelEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderRegenEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderStepUpEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderCraftingSpiritualityEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderDurabilityEffect;
import net.dinomine.potioneer.beyonder.effects.paragon.BeyonderXpReduceEffect;
import net.dinomine.potioneer.beyonder.effects.redpriest.*;
import net.dinomine.potioneer.beyonder.effects.tyrant.*;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class BeyonderEffects {
    private static final HashMap<String, BeyonderEffectType> EFFECTS = new HashMap<>();

    public static final BeyonderEffectType COGITATION = registerEffect("cogitation", new BeyonderEffectType(BeyonderCogitationEffect::new));
    public static final BeyonderEffectType MYSTERY_FALL_NEGATE = registerEffect("negate_fall", new BeyonderEffectType(BeyonderFallDmgCancelEffect::new));
    public static final BeyonderEffectType MYSTERY_FIGURINE = registerEffect("figurine_substitute", new BeyonderEffectType(BlankEffect::new));
    public static final BeyonderEffectType MYSTERY_INVISIBLE = registerEffect("invisibility", new BeyonderEffectType(BlankEffect::new));
    public static final BeyonderEffectType MYSTERY_REACH = registerEffect("extended_reach", new BeyonderEffectType(BeyonderExtendedReachEffect::new));
    public static final BeyonderEffectType MYSTERY_REGEN = registerEffect("spirituality_regen", new BeyonderEffectType(BeyonderRegenEffect::new));
    public static final BeyonderEffectType MYSTERY_STEP_UP = registerEffect("step_up", new BeyonderEffectType(BeyonderStepUpEffect::new));
    public static final BeyonderEffectType WHEEL_CALAMITY = registerEffect("calamity", new BeyonderEffectType(CalamityEffect::new));
    public static final BeyonderEffectType WHEEL_FORTUNE = registerEffect("fortune", new BeyonderEffectType(FortuneEffect::new));
    public static final BeyonderEffectType WHEEL_SILK = registerEffect("silk", new BeyonderEffectType(SilkTouchEffect::new));
    public static final BeyonderEffectType WHEEL_PATIENCE = registerEffect("patience", new BeyonderEffectType(PatienceEffect::new));
    public static final BeyonderEffectType WHEEL_INSTANT_LUCK = registerEffect("instant_luck", new BeyonderEffectType(InstantLuckEffect::new));
    public static final BeyonderEffectType WHEEL_LUCK_EFFECT = registerEffect("luck_effect", new BeyonderEffectType(LuckEffect::new));
    public static final BeyonderEffectType WHEEL_LUCK = registerEffect("luck", new BeyonderEffectType(TempLuckEffect::new));
    public static final BeyonderEffectType WHEEL_ZERO_DAMAGE = registerEffect("zero_damage", new BeyonderEffectType(ZeroDamageEffect::new));
    public static final BeyonderEffectType WHEEL_MINING = registerEffect("mining", new BeyonderEffectType(MiningSpeedEffect::new));
    public static final BeyonderEffectType WHEEL_VELOCITY = registerEffect("velocity", new BeyonderEffectType(VelocityEffect::new));
    public static final BeyonderEffectType WHEEL_COOLDOWN = registerEffect("cooldown", new BeyonderEffectType(CooldownRecipientEffect::new));
    public static final BeyonderEffectType WHEEL_COOLDOWN_DEFENCE = registerEffect("defensive_cooldown", new BeyonderEffectType(CooldownSourceEffect::new));
    public static final BeyonderEffectType WHEEL_GAMBLING = registerEffect("gambling", new BeyonderEffectType(GamblingEffect::new));
    public static final BeyonderEffectType WHEEL_CRIT = registerEffect("critical", new BeyonderEffectType(CritEffect::new));
    public static final BeyonderEffectType WHEEL_ARROW = registerEffect("arrow_gravitate", new BeyonderEffectType(ArrowGravitateEffect::new));
    public static final BeyonderEffectType WHEEL_ZERO_BLOCK = registerEffect("zero_block", new BeyonderEffectType(ZeroDamageBlockEffect::new));
    public static final BeyonderEffectType WHEEL_FATE = registerEffect("fate", new BeyonderEffectType(FateEffect::new));
    public static final BeyonderEffectType WHEEL_PHASING = registerEffect("phasing", new BeyonderEffectType(PhasingEffect::new));
    public static final BeyonderEffectType WHEEL_INSTANT_BAD_LUCK = registerEffect("instant_bad_luck", new BeyonderEffectType(InstantUnluckEffect::new));
    public static final BeyonderEffectType WHEEL_DAMAGE_RECORDING = registerEffect("damage_recording", new BeyonderEffectType(DamageRecordingEffect::new));
    public static final BeyonderEffectType WHEEL_BAD_LUCK = registerEffect("bad_luck", new BeyonderEffectType(TempBadLuckEffect::new));
    public static final BeyonderEffectType WHEEL_CHAOTIC_LUCK = registerEffect("chaotic_luck", new BeyonderEffectType(ChaoticLuckEffect::new));
    public static final BeyonderEffectType TYRANT_WATER_AFFINITY = registerEffect("water_affinity", new BeyonderEffectType(WaterAffinityEffect::new));
    public static final BeyonderEffectType TYRANT_OCEAN_ORDER = registerEffect("ocean_order", new BeyonderEffectType(OceanOrderEffect::new));
    public static final BeyonderEffectType TYRANT_LIGHTNING_TARGET = registerEffect("lightning_target", new BeyonderEffectType(LightningTargetEffect::new));
    public static final BeyonderEffectType TYRANT_WATER_PRISON = registerEffect("water_prison", new BeyonderEffectType(WaterPrisonEffect::new));
    public static final BeyonderEffectType TYRANT_SCALES = registerEffect("scales", new BeyonderEffectType(ScalesEffect::new));
    public static final BeyonderEffectType TYRANT_DROWNING = registerEffect("drowning", new BeyonderEffectType(DrowningEffect::new));
    public static final BeyonderEffectType TYRANT_AOJ_SOURCE = registerEffect("aoj_viewer", new BeyonderEffectType(AoJSourceEffect::new));
    public static final BeyonderEffectType TYRANT_AOJ_RECIPIENT = registerEffect("aoj_influence", new BeyonderEffectType(AoJRecipientEffect::new));
    public static final BeyonderEffectType TYRANT_AURA_SOURCE = registerEffect("aura_source", new BeyonderEffectType(AuraSourceEffect::new));
    public static final BeyonderEffectType TYRANT_AURA_RECIPIENT = registerEffect("aura_recipient", new BeyonderEffectType(AuraRecipientEffect::new));
    public static final BeyonderEffectType TYRANT_ARREST_SOURCE = registerEffect("arrest_source", new BeyonderEffectType(ArrestSourceEffect::new));
    public static final BeyonderEffectType TYRANT_ARREST_RECIPIENT = registerEffect("arrest_recipient", new BeyonderEffectType(ArrestRecipientEffect::new));
    public static final BeyonderEffectType TYRANT_MIST_EFFECT = registerEffect("mist", new BeyonderEffectType(MistEffect::new));
    public static final BeyonderEffectType TYRANT_SENSE_OF_ORDER = registerEffect("sense_of_order", new BeyonderEffectType(SenseOfOrderEffect::new));
    public static final BeyonderEffectType RED_FIRE_AURA = registerEffect("fire_aura", new BeyonderEffectType(BeyonderFireAuraEffect::new));
    public static final BeyonderEffectType RED_FIRE_BUFF = registerEffect("fire_buff", new BeyonderEffectType(BeyonderFireBuffEffect::new));
    public static final BeyonderEffectType RED_LIGHT_BUFF = registerEffect("light_buff", new BeyonderEffectType(BeyonderLightBuffEffect::new));
    public static final BeyonderEffectType RED_PURIFICATION = registerEffect("purification", new BeyonderEffectType(BeyonderPurificationEffect::new));
    public static final BeyonderEffectType RED_PROFICIENCY = registerEffect("weapon_proficiency", new BeyonderEffectType(BeyonderWeaponProficiencyEffect::new));
    public static final BeyonderEffectType PARAGON_CRAFTING = registerEffect("crafting_spirituality", new BeyonderEffectType(BeyonderCraftingSpiritualityEffect::new));
    public static final BeyonderEffectType PARAGON_XP = registerEffect("reduced_xp", new BeyonderEffectType(BeyonderXpReduceEffect::new));
    public static final BeyonderEffectType PARAGON_REGEN = registerEffect("durability_regen", new BeyonderEffectType(BeyonderDurabilityEffect::new));
    public static final BeyonderEffectType HUNGER_REGEN = registerEffect("hunger_regen", new BeyonderEffectType(BeyonderHungerRegenEffect::new));


    public static BeyonderEffectType registerEffect(String effectId, BeyonderEffectType baseEffect){
        if(EFFECTS.containsKey(effectId)){
            throw new RuntimeException("Error: Tried to register an effect with an already existing ID");
        }
        baseEffect.setEffectId(effectId);
        EFFECTS.put(effectId, baseEffect);
        return baseEffect;
    }

    public static BeyonderEffectType getEffect(String effectId){
        return EFFECTS.get(effectId);
    }
//
//    public enum EFFECT{
//        TYRANT_WATER_AFFINITY,
//        TYRANT_ELECTRIFICATION,
//        TYRANT_LIGHTNING_TARGET,
//        RED_WEAPON_PROFICIENCY,
//        RED_FIRE_BUFF,
//        RED_FIRE_AURA,
//        RED_LIGHT_BUFF,
//        RED_PURIFICATION,
//        PARAGON_DURABILITY_REGEN,
//        PARAGON_XP_REDUCE,
//        PARAGON_CRAFTING_SPIRITUALITY,
//        MYSTERY_INVIS,
//        MYSTERY_FALL,
//        MYSTERY_FALL_NEGATE,
//        MYSTERY_REGEN,
//        MYSTERY_REACH,
//        MYSTERY_STEP,
//        MYSTERY_FIGURINE,
//        WHEEL_TEMP_LUCK,
//        WHEEL_SILK_TOUCH,
//        WHEEL_FORTUNE,
//        WHEEL_MINING,
//        WHEEL_DAMAGE_REDUCE,
//        WHEEL_GAMBLING,
//        WHEEL_LUCK_TREND,
//        MISC_MYST,
//        MISC_PLAGUE,
//        MISC_COGITATION,
//        MISC_HUNGER_REGEN
//
//    }

    public static BeyonderEffect byId(String effectId, int sequence, int cost, int duration, boolean active){
        return EFFECTS.get(effectId).createInstance(sequence, cost, duration, active);
    }

    public static BeyonderEffectType getRandomEffect(String prefix) {
        List<BeyonderEffectType> typeList = new ArrayList<>();
        switch (prefix.toLowerCase()){
            case "wheel":
                typeList = List.of(WHEEL_FORTUNE, WHEEL_SILK, WHEEL_MINING);
            case "tyrant":
                typeList = List.of(TYRANT_LIGHTNING_TARGET, TYRANT_WATER_AFFINITY);
            case "mystery":
                typeList = List.of(MYSTERY_INVISIBLE, MYSTERY_REACH, MYSTERY_STEP_UP, MYSTERY_FALL_NEGATE);
            case "red":
                typeList = List.of(RED_FIRE_BUFF, RED_LIGHT_BUFF, RED_PROFICIENCY, RED_PURIFICATION);
            case "paragon":
                typeList = List.of(PARAGON_CRAFTING, PARAGON_XP, PARAGON_REGEN);
        }
        return typeList.isEmpty() ? null : typeList.get((int)(Math.random()*typeList.size()));

    }

    public static class BeyonderEffectType{
        private final Supplier<BeyonderEffect> factory;
        private String effectId;

        public BeyonderEffectType(Supplier<BeyonderEffect> factory) {
            this.factory = factory;
        }

        public String getEffectId(){
            return this.effectId;
        }

        public void setEffectId(String effectId){
            this.effectId = effectId;
        }

        public BeyonderEffect createInstance(int sequence, int duration, boolean active) {
            return factory.get().withParams(sequence, duration, active).setId(effectId);
        }

        public BeyonderEffect createInstance(int sequence, int cost, int duration, boolean active) {
            return factory.get().withParams(sequence, duration, active, cost).setId(effectId);
        }


    }
}
