package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.abilities.misc.*;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.paragon.*;
import net.dinomine.potioneer.beyonder.abilities.redpriest.*;
import net.dinomine.potioneer.beyonder.abilities.tyrant.*;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;
import net.dinomine.potioneer.beyonder.downsides.DummyDownside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.function.Function;

public class Abilities {
    private static final HashMap<String, AbilityFactory> ABILITIES = new HashMap<>();

    public static final AbilityFactory COGITATION = registerAbility("cogitation",
            (Integer pathwayId) -> (new CogitationAbility(pathwayId)).canFlip(),
            12, 0, 0);

    public static final AbilityFactory DUMMY_DOWNSIDE = registerAbility("d_dummy",
            DummyDownside::new, 0, 0, 0);

    // -------------------------- WHEEL OF FORTUNE ---------------------------------------------------


    public static final AbilityFactory LUCK_BOOST = registerAbility("luck_boost",
            LuckBoostAbility::new,
            4, 0, 30);

    public static final AbilityFactory LUCK_TREND = registerAbility("lucky_trend",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_LUCK_TREND,
                            (ignored) -> "lucky_trend")
                    .enabledOnAcquire(),
            10, 0, 0);

    //retweaked
    public static final AbilityFactory CONJURE_PICKAXE = registerAbility("pick",
            ConjurePickaxeAbility::new,
            2, 0, 10).hasSecondaryFunction().active();

    //retweaked
    public static final AbilityFactory MINING_SPEED = registerAbility("mining",
            sequenceLevel -> (new MiningSpeedAbility(sequenceLevel)).canFlip().enabledOnAcquire(),
            0, 0, 0).hasSecondaryFunction();

    //retweaked
    public static final AbilityFactory ZERO_DAMAGE = registerAbility("zero_damage",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_ZERO_DAMAGE,
                            (level) -> "zero_damage_" + (level > 7 ? "1" : (level > 6 ? "2" : "3")))
                    .enabledOnAcquire(),
            8, 0, 0);

    //retweaked
    public static final AbilityFactory VOID_VISION = registerAbility("void_vision",
            (Integer sequenceLevel) -> MobEffectPassiveAbility.createAbility(sequenceLevel, MobEffects.NIGHT_VISION, ignored -> "void_vision")
                    .withAmplifier(1).withPassiveCost(2).withThreshold(5), 9, 0, 5).passive().active();

    //retweaked
    public static final AbilityFactory WHEEL_KNOWLEDGE = registerAbility("wheel_knowledge",
            WheelKnowledgeAbility::new, 13, 0, 0).passive();

    //retweaked
    public static final AbilityFactory MINER_LIGHT = registerAbility("miner_light",
            MinerLightAbility::new, 1, 0, level -> 5 + 2*(9-level)).active();

    //retweaked
    public static final AbilityFactory FORTUNE_ABILITY = registerAbility("fortune",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_FORTUNE,
                            (ignored) -> "fortune")
                    .canFlip().withThreshold(0.1f),
            6, 0, level -> 5);

    //retweaked
    public static final AbilityFactory SILK_TOUCH_ABILITY = registerAbility("silk",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_SILK,
                            (ignored) -> "silk")
                    .canFlip().withThreshold(0.1f),
            5, 0, 5);

    //retweaked
    public static final AbilityFactory CALAMITY_INCREASE = registerAbility("calamity",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_CALAMITY,
                            (level) -> level < 8 ? "calamity_2" : "calamity")
                    .enabledOnAcquire(),
            4, 0, 0);

    //retweaked
    public static final AbilityFactory BLOCK_APPRAISAL = registerAbility("block_appraisal",
            BlockAppraisalAbility::new,
            15, 0, level -> 10 + 10*(9-level)).hasSecondaryFunction();

    //retweaked
    public static final AbilityFactory APPRAISAL = registerAbility("appraisal",
            AppraisalAbility::new, 14, 0, 0);

    //retweaked
    public static final AbilityFactory TARGET_APPRAISAL = registerAbility("target_appraisal",
            EntityAppraisalAbility::new,
            16, 0, 5).hasSecondaryFunction();

    //retweaked
    public static final AbilityFactory PATIENCE = registerAbility("patience",
            (PatienceAbility::new),
            11, 0, 0).hasSecondaryFunction(true).passive().active();

    //retweaked
    public static final AbilityFactory VELOCITY = registerAbility("velocity",
            sequenceLevel -> (new VelocityAbility(sequenceLevel)).enabledOnAcquire().withThreshold(0.1f),
            10, 0, 3).hasSecondaryFunction();

    //retweaked
    public static final AbilityFactory MINER_BONE_MEAL = registerAbility("w_bone_meal",
            BoneMealAbility::new, 7, 0, level-> 2*(10-level)).active();

    //retweaked
    public static final AbilityFactory FORCE_COOLDOWN_ABILITY = registerAbility("aoe_cooldown",
            CooldownAbility::new, 11, 0, level-> 10 + 10*(9-level)).passive().active().hasSecondaryFunction();

    //retweaked
    public static final AbilityFactory GAMBLING = registerAbility("gambling",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_GAMBLING, ignored -> "gambling").canFlip().enabledOnAcquire(),
            1, 0, 0);

    public static final AbilityFactory DODGE_DAMAGE = registerAbility("luck_dodge",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.WHEEL_LUCK_DODGE,
                            (ignored) -> "luck_dodge")
                    .canFlip().enabledOnAcquire(),
            8, 0, 0);

    // -------------------------- TYRANT ---------------------------------------------------
    public static final AbilityFactory WATER_AFFINITY = registerAbility("water_affinity",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.TYRANT_WATER_AFFINITY,
                            (ignored) -> "water_affinity")
                    .canFlip().withThreshold(0.15f).enabledOnAcquire().withCost(level -> sequenceLevel < 8 ? 15 : 5),
            0, 1, 10);

    public static final AbilityFactory TYRANT_DIVINATION = registerAbility("divination",
            (Integer level) -> (new DivinationAbility(level)).enabledOnAcquire(),
            56, 1, 0).hasSecondaryFunction();

    public static final AbilityFactory TYRANT_ELECTRIFICATION = registerAbility("electrification",
            (Integer sequenceLevel) -> (PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.TYRANT_ELECTRIFICATION,
                    ignored -> "electrification"))
                    .enabledOnAcquire().canFlip().withThreshold(20),
            272, 1, 20);

    public static final AbilityFactory TYRANT_RAIN = registerAbility("summon_rain",
            RainCreateAbility::new, 200, 1, 70);

    public static final AbilityFactory TYRANT_LEAP = registerAbility("rain_leap",
            RainLeapAbility::new, 176, 1, 25);

    public static final AbilityFactory TYRANT_THUNDER = registerAbility("summon_thunder",
            ThunderCreateAbility::new, 224, 1, 160);

    public static final AbilityFactory TYRANT_LIGHTNING_STRIKE = registerAbility("thunder_strike",
            ThunderStrikeAbility::new, 248, 1, 50);

    public static final AbilityFactory TYRANT_CREATE_WATER = registerAbility("water_create",
            WaterCreateAbility::new, 104, 1, 1).hasSecondaryFunction();

    public static final AbilityFactory TYRANT_WATER_PRISON = registerAbility("water_prison",
            WaterPrisonAbility::new, 152, 1, 40);

    public static final AbilityFactory TYRANT_REMOVE_WATER = registerAbility("water_sponge",
            WaterRemoveAbility::new, 128, 1, 5);

    public static final AbilityFactory TYRANT_WATER_TRAP = registerAbility("water_trap",
            WaterTrapAbility::new, 80, 1, 80);

    // -------------------------- MYSTERY ---------------------------------------------------
    public static final AbilityFactory AIR_BULLET = registerAbility("air_bullet",
            AirBulletAbility::new, 1, 2, i -> 60 + 10*(9-i));

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

    // -------------------------- RED PRIEST ---------------------------------------------------

    public static final AbilityFactory FIRE_SWORD = registerAbility("fire_sword",
            ConjureFireSwordAbility::new, 56, 3, 25);

//        this.info = new AbilityInfo(83, 104, "Fire Guard", 30 + sequence, 1, this.getMaxCooldown(), "fire_aura");
    public static final AbilityFactory FIRE_AURA = registerAbility("fire_aura",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_FIRE_AURA,
                            (ignored) -> "fire_aura")
                    .canFlip().withThreshold(0.05f).withCost(ignored -> 1),
            104, 3, 0);

    public static final AbilityFactory FIRE_BALL = registerAbility("fire_ball",
            FireBallAbility::new, 128, 3, 20);

//        this.info = new AbilityInfo(83, 80, "Fire Dance", 30 + sequence, 5, this.getMaxCooldown(), "fire_buff");
    public static final AbilityFactory FIRE_BUFF = registerAbility("fire_buff",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_FIRE_BUFF,
                            (ignored) -> "fire_buff")
                    .enabledOnAcquire().canFlip().withThreshold(5).withCost(ignored -> 5),
            80, 3, 0);

    public static final AbilityFactory HEALING = registerAbility("heal",
            HealAbility::new, 152, 3, 20);

    public static final AbilityFactory LIGHT_BUFF = registerAbility("light_buff",
            LightBuffAbility::new, 224, 3, 5);

    public static final AbilityFactory MELT_ABILITY = registerAbility("melt",
            MeltAbility::new, 80, 3, 20);

    public static final AbilityFactory PRIEST_LIGHT = registerAbility("priest_light",
            PriestLightAbility::new, 200, 3, 10);

//        this.info = new AbilityInfo(83, 176, "Purification", 30 + sequence, 5, this.getMaxCooldown(), "purification");
    public static final AbilityFactory PURIFICATION = registerAbility("purification",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_PURIFICATION,
                            (ignored) -> "purification")
                    .enabledOnAcquire().canFlip().withThreshold(5).withCost(ignored -> 5),
            176, 3, 0);

//        this.info = new AbilityInfo(83, 32, "Weapon Proficiency", 30 + sequence, 0, this.getMaxCooldown(), "weapons_master");
    public static final AbilityFactory WEAPON_PROFICIENCY = registerAbility("weapon_proficiency",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.RED_PROFICIENCY,
                            (ignored) -> "weapon_proficiency")
                    .enabledOnAcquire().canFlip(),
            32, 3, 0);

    // -------------------------- PARAGON ---------------------------------------------------

    public static final AbilityFactory ANVIL_GUI = registerAbility("anvil_gui",
            AnvilGuiAbility::new, 104, 4, 10);

    public static final AbilityFactory CONJURER_CONTAINER = registerAbility("conjure_container",
            ConjurerContainerAbility::new, 176, 4, 0);

    public static final AbilityFactory CRAFTING_GUI = registerAbility("crafting_gui",
            CraftingGuiAbility::new, 32, 4, 10);

    public static final AbilityFactory ENDER_CHEST = registerAbility("ender_chest",
            EnderChestAbility::new, 152, 4, 50);

    public static final AbilityFactory FUEL_CREATE = registerAbility("fuel",
            FuelAbility::new, 80, 4, 0);

    public static final AbilityFactory CRAFTER_BONE_MEAL = registerAbility("p_bone_meal",
            ParagonBoneMealAbility::new, 128, 4, 2);

    public static final AbilityFactory REMOVE_ENCHANTMENT = registerAbility("disenchant",
            RemoveEnchantmentAbility::new, 224, 4, 50);

//        this.info = new AbilityInfo(109, 32, "Crafting Spirituality", 40 + sequence, 0, this.getMaxCooldown(), "craft");
    public static final AbilityFactory CRAFTING_SPIRITUALITY = registerAbility("crafting_spirituality",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.PARAGON_CRAFTING,
                            (ignored) -> "craft")
                    .enabledOnAcquire().canFlip(),
            32, 4, 0);

//        this.info = new AbilityInfo(109, 200, "Xp Cost Reduction", 40 + sequence, 20, this.getMaxCooldown(), "xp_reduce");
    public static final AbilityFactory XP_COST_REDUCE = registerAbility("xp_reduce",
            (Integer sequenceLevel) -> PassiveAbility.createAbility(sequenceLevel, BeyonderEffects.PARAGON_XP,
                            (ignored) -> "xp_reduce")
                    .enabledOnAcquire().canFlip(),
            200, 4, 0);

    public static final AbilityFactory DURABILITY_REGEN = registerAbility("durability_regen",
            DurabilityRegenAbility::new, 56, 4, 3);

    private static AbilityFactory registerAbility(String ablId, Function<Integer, Ability> constructor, int posY, int pathwayId, int minSpirToActivate){
        if(ABILITIES.containsKey(ablId)){
            throw new RuntimeException("Error: Tried to register an ability with an already existing ID");
        }
        AbilityFactory factory = new AbilityFactory(posY, pathwayId, ignored -> minSpirToActivate, ablId, constructor);
        ABILITIES.put(ablId, factory);
        return factory;
    }
    private static AbilityFactory registerAbility(String ablId, Function<Integer, Ability> constructor, int posY, int pathwayId, Function<Integer, Integer> costFunction){
        if(ABILITIES.containsKey(ablId)){
            throw new RuntimeException("Error: Tried to register an ability with an already existing ID");
        }
        AbilityFactory factory = new AbilityFactory(posY, pathwayId, costFunction, ablId, constructor);
        ABILITIES.put(ablId, factory);
        return factory;
    }

    public static AbilityFactory registerAbility(ResourceLocation iconLocation, String ablId, Function<Integer, Ability> constructor, int posY, int pathwayId, int minSpirToActivate){
        if(ABILITIES.containsKey(ablId)){
            throw new RuntimeException("Error: Tried to register an ability with an already existing ID");
        }
        AbilityFactory factory = new AbilityFactory(iconLocation, posY, pathwayId, ignored -> minSpirToActivate, ablId, constructor);
        ABILITIES.put(ablId, factory);
        return factory;
    }

    public static AbilityFactory getAbilityFactory(String abl_id){
        return ABILITIES.get(abl_id);
    }
    public static AbilityFactory getAbilityFactory(AbilityKey key){
        return getAbilityFactory(key.getAbilityId());
    }

    public static Ability getAbilityInstance(String abl_id, int sequenceLevel){
        return ABILITIES.get(abl_id).create(sequenceLevel);
    }

    public static Ability getAbilityInstanceByKey(AbilityKey key){
        return getAbilityInstance(key.getAbilityId(), key.getSequenceLevel());
    }

    public static AbilityInfo getInfo(String abilityId, int cooldown, int maxCd, boolean enabled, String descId, AbilityKey key){
        return ABILITIES.get(abilityId).getInfo(cooldown, maxCd, enabled, descId, abilityId).withKey(key);
    }

    /**
     * same as the other getInfo, but you can specify the pathwaySequenceId (as single digit).
     * mainly applies to abilities that dont change at all between pathways like Cogitation,
     * so the question of what pathway it belongs to depends on the ability instance itself, hence why you can pass it as argument here
     * @param abilityId
     * @param cooldown
     * @param enabled
     * @param descId
     * @param pathwayId
     * @return
     */
    public static AbilityInfo getInfo(String abilityId, int cooldown, int maxCd, boolean enabled, String descId, AbilityKey key, int pathwayId){
        return ABILITIES.get(abilityId).getInfo(cooldown, maxCd, enabled, descId, abilityId, pathwayId).withKey(key);
    }
}
