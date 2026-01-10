package net.dinomine.potioneer.beyonder.effects;


import net.dinomine.potioneer.beyonder.effects.misc.*;
import net.dinomine.potioneer.beyonder.effects.mystery.*;
import net.dinomine.potioneer.beyonder.effects.tyrant.BeyonderWaterAffinityEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.*;

import java.util.HashMap;
import java.util.function.Supplier;

public class BeyonderEffects {
    private static final HashMap<String, BeyonderEffectType> EFFECTS = new HashMap<>();

    public static final BeyonderEffectType TYRANT_WATER_AFFINITY = registerEffect("water_affinity", new BeyonderEffectType(BeyonderWaterAffinityEffect::new));
    public static final BeyonderEffectType MYSTERY_FALL_NEGATE = registerEffect("negate_fall", new BeyonderEffectType(BeyonderFallDmgCancelEffect::new));
    public static final BeyonderEffectType COGITATION = registerEffect("cogitation", new BeyonderEffectType(BeyonderCogitationEffect::new));
    public static final BeyonderEffectType MYSTERY_FIGURINE = registerEffect("figurine_substitute", new BeyonderEffectType(BlankEffect::new));
    public static final BeyonderEffectType MYSTERY_INVISIBLE = registerEffect("invisibility", new BeyonderEffectType(BlankEffect::new));
    public static final BeyonderEffectType MYSTERY_REACH = registerEffect("extended_reach", new BeyonderEffectType(BeyonderExtendedReachEffect::new));
    public static final BeyonderEffectType MYSTERY_REGEN = registerEffect("spirituality_regen", new BeyonderEffectType(BeyonderRegenEffect::new));
    public static final BeyonderEffectType MYSTERY_STEP_UP = registerEffect("step_up", new BeyonderEffectType(BeyonderStepUpEffect::new));
    public static final BeyonderEffectType WHEEL_CALAMITY = registerEffect("calamity", new BeyonderEffectType(BeyonderCalamityEffect::new));
    public static final BeyonderEffectType WHEEL_FORTUNE = registerEffect("fortune", new BeyonderEffectType(BeyonderFortuneEffect::new));
    public static final BeyonderEffectType WHEEL_SILK = registerEffect("silk", new BeyonderEffectType(BeyonderSilkTouchEffect::new));
    public static final BeyonderEffectType WHEEL_PATIENCE = registerEffect("patience", new BeyonderEffectType(BeyonderPatienceEffect::new));
    public static final BeyonderEffectType WHEEL_TEMP_LUCK = registerEffect("temp_luck", new BeyonderEffectType(BeyonderTempLuckEffect::new));
    public static final BeyonderEffectType WHEEL_LUCK_DODGE = registerEffect("luck_dodge", new BeyonderEffectType(BeyonderLuckDodgeEffect::new));
    public static final BeyonderEffectType WHEEL_LUCK_TREND = registerEffect("lucky_trend", new BeyonderEffectType(BeyonderLuckTrendEffect::new));
    public static final BeyonderEffectType WHEEL_MINING = registerEffect("mining", new BeyonderEffectType(BeyonderMiningSpeedEffect::new));


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
