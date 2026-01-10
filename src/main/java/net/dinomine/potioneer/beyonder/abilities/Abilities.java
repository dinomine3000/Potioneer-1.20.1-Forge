package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.abilities.misc.BoneMealAbility;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.abilities.misc.TimedPassiveAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public class Abilities {
    private static final HashMap<String, AbilityFactory> ABILITIES = new HashMap<>();

    public static final AbilityFactory COGITATION = registerAbility("cogitation",
            (Integer pathwayId) -> (new CogitationAbility(pathwayId)).canFlip(),
            320, 0, 0);

    public static final AbilityFactory CALAMITY_INCREASE = registerAbility("calamity",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_CALAMITY,
                            (ignored) -> "calamity")
                    .enabledOnAcquire(),
            272, 0, 0);

    public static final AbilityFactory CHECK_LUCK = registerAbility("check_luck",
            CheckLuckAbility::new,
            104, 0, 0);

    public static final AbilityFactory CONJURE_PICKAXE = registerAbility("pick",
            ConjurePickaxeAbility::new,
            80, 0, 10);

    public static final AbilityFactory FORTUNE_ABILITY = registerAbility("fortune",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_FORTUNE,
                            (ignored) -> "fortune")
                    .canFlip().withThreshold(0.1f).withCost(level -> 5),
            176, 0, 0);

    public static final AbilityFactory SILK_TOUCH_ABILITY = registerAbility("silk",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_SILK,
                            (ignored) -> "silk")
                    .canFlip().withThreshold(0.1f).withCost(level -> 5),
            152, 0, 0);

    public static final AbilityFactory PATIENCE = registerAbility("patience",
            PatienceAbility::new,
            296, 0, 0).hasSecondaryFunction(false);
    //add a secondary function, maybe, that tells you how much luck you will get.

    public static final AbilityFactory LUCK_BOOST = registerAbility("luck_boost",
            LuckBoostAbility::new,
            128, 0, 30);

    public static final AbilityFactory DODGE_DAMAGE = registerAbility("luck_dodge",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_LUCK_DODGE,
                            (ignored) -> "luck_dodge")
                    .canFlip().enabledOnAcquire(),
            248, 0, 0);

    public static final AbilityFactory LUCK_TREND = registerAbility("lucky_trend",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_LUCK_TREND,
                            (ignored) -> "lucky_trend")
                    .enabledOnAcquire(),
            32, 0, 0);

    public static final AbilityFactory MINER_LIGHT = registerAbility("miner_light",
            MinerLightAbility::new, 56, 0, 5);

    public static final AbilityFactory MINING_SPEED = registerAbility("mining",
            sequenceLevel -> (new MiningSpeedAbility(sequenceLevel)).canFlip().enabledOnAcquire(),
            32, 0, 0).hasSecondaryFunction();

    public static final AbilityFactory MINER_BONE_MEAL = registerAbility("w_bone_meal",
            BoneMealAbility::new, 200, 0, 10);


    public static final AbilityFactory WATER_AFFINITY = registerAbility("water_affinity",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.TYRANT_WATER_AFFINITY,
                            (ignored) -> "water_affinity")
                    .canFlip().withThreshold(0.15f).enabledOnAcquire().withCost(level -> sequenceLevel < 8 ? 15 : 5),
            32, 1, 10);

    public static final AbilityFactory AIR_BULLET = registerAbility("air_bullet",
            AirBulletAbility::new, 56, 2, 10);

    public static final AbilityFactory DOOR_OPENING = registerAbility("door_opening",
            DoorOpeningAbility::new, 80, 2, 20);

    public static final AbilityFactory PAPER_FIGURINE_SUBSTITUTE = registerAbility("figurine_substitute",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.MYSTERY_FIGURINE,
                            (ignored) -> "figurine_substitute")
                    .canFlip().enabledOnAcquire().withCost(level -> 30 + 10*(9-level)),
            224, 2, 0);

    public static final AbilityFactory INVISIBILITY = registerAbility("invisibility",
            (Integer sequenceLevel) -> TimedPassiveAbility.createTimed(sequenceLevel, BeyonderEffects.MYSTERY_INVISIBLE,
                            (ignored) -> "invisibility", level -> 2*40*((9-level)*10 + 5))
                    .canFlip().withThreshold(0.15f),
            128, 2, 0);

    public static final AbilityFactory LEAP = registerAbility("leap",
            LeapAbility::new, 200, 2, 15);

    public static final AbilityFactory PANACEA = registerAbility("panacea",
            PanaceaAbility::new, 272, 2, 15);

    public static final AbilityFactory PUSH = registerAbility("push_pull",
            PushAbility::new, 296, 2, 40).hasSecondaryFunction();

    public static final AbilityFactory EXTENDED_REACH = registerAbility("extended_reach",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.MYSTERY_REACH,
                            (ignored) -> "extended_reach")
                    .canFlip().enabledOnAcquire(),
            104, 2, 0);

    public static final AbilityFactory MYSTERY_REGEN = registerAbility("spirituality_regen",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.MYSTERY_REGEN,
                            (ignored) -> "spirituality_regen")
                    .canFlip().enabledOnAcquire(),
            32, 2, 0);

    public static final AbilityFactory STEP_UP = registerAbility("step_up",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.MYSTERY_STEP_UP,
                            (ignored) -> "step_up")
                    .canFlip().enabledOnAcquire(),
            152, 2, 0);

    public static final AbilityFactory CANCEL_FALL_DAMAGE = registerAbility("negate_fall",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.MYSTERY_FALL_NEGATE,
                            (ignored) -> "negate_fall")
                    .enabledOnAcquire(),
            32, 2, 0);

    private static AbilityFactory registerAbility(String ablId, Function<Integer, Ability> constructor, int posY, int pathwayId, int minSpirToActivate){
        if(ABILITIES.containsKey(ablId)){
            throw new RuntimeException("Error: Tried to register an ability with an already existing ID");
        }
        AbilityFactory factory = new AbilityFactory(posY, pathwayId, minSpirToActivate, ablId, constructor);
        ABILITIES.put(ablId, factory);
        return factory;
    }

    public static AbilityFactory registerAbility(ResourceLocation iconLocation, String ablId, Function<Integer, Ability> constructor, int posY, int pathwayId, int minSpirToActivate){
        if(ABILITIES.containsKey(ablId)){
            throw new RuntimeException("Error: Tried to register an ability with an already existing ID");
        }
        AbilityFactory factory = new AbilityFactory(iconLocation, posY, pathwayId, minSpirToActivate, ablId, constructor);
        ABILITIES.put(ablId, factory);
        return factory;
    }

    public static AbilityFactory getAbilityById(String abl_id){
        return ABILITIES.get(abl_id);
    }

    public static Ability getAbilityById(String abl_id, int sequenceLevel){
        return ABILITIES.get(abl_id).create(sequenceLevel);
    }

    public static AbilityInfo getInfo(String abilityId, float cooldown, boolean enabled, String descId){
        return ABILITIES.get(abilityId).getInfo(cooldown, enabled, descId, abilityId);
    }

    /**
     * same as the other getInfo, but you can specify the pathwayId (as single digit).
     * mainly applies to abilities that dont change at all between pathways like Cogitation,
     * so the question of what pathway it belongs to depends on the ability instance itself, hence why you can pass it as argument here
     * @param abilityId
     * @param cooldown
     * @param enabled
     * @param descId
     * @param pathwayId
     * @return
     */
    public static AbilityInfo getInfo(String abilityId, float cooldown, boolean enabled, String descId, int pathwayId){
        return ABILITIES.get(abilityId).getInfo(cooldown, enabled, descId, abilityId, pathwayId);
    }
}
