package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotioneerAbilityConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.IntValue BET_COOLDOWN;
    public static final ForgeConfigSpec.IntValue BLOCK_AP_COOLDOWN;
    public static final ForgeConfigSpec.IntValue BLOCK_AP_RANGE_PER_LEVEL;
    public static final ForgeConfigSpec.IntValue CONJURE_PICKAXE_COST;
    public static final ForgeConfigSpec.IntValue CONJURE_PICKAXE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FATE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue FATE_COST;
    public static final ForgeConfigSpec.IntValue GAMBLING_COOLDOWN;
    public static final ForgeConfigSpec.IntValue GAMBLING_COST;

    public static final ForgeConfigSpec.DoubleValue PATIENCE_TIME_LIMIT;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_TARGET_ALLIES;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_EFFECT_STACKS;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_ABILITY_CAST_COOLDOWN;


    // Tyrant Pathway - General / Amplification
    public static final ForgeConfigSpec.IntValue AMPLIFICATION_DURATION;
    public static final ForgeConfigSpec.IntValue AMPLIFICATION_COST;

    // Tyrant Pathway - Arrest
    public static final ForgeConfigSpec.IntValue ARREST_MANUAL_COST;
    public static final ForgeConfigSpec.IntValue ARREST_COST;
    public static final ForgeConfigSpec.IntValue ARREST_SAP;
    public static final ForgeConfigSpec.IntValue AOJ_COOLDOWN;

    // Tyrant Pathway - Calamity
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_RAIN;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_RAIN;
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_THUNDER;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_THUNDER;
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_LEAP;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_LEAP;
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_WIND;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_WIND;
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_ASTEROID;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_ASTEROID;
    public static final ForgeConfigSpec.IntValue CALAMITY_COOLDOWN_LUCK;
    public static final ForgeConfigSpec.IntValue CALAMITY_COST_LUCK;

    // Tyrant Pathway - Water Spell
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_CONJURE;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_ABSORB;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_DROWNING;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_WATER_TRAP;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_WATER_JET;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COST_HEALING;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COOLDOWN_CONSUME;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COOLDOWN_HEALING;
    public static final ForgeConfigSpec.IntValue WATER_SPELL_COOLDOWN_WATER_TRAP;

    //contract
    public static final ForgeConfigSpec.BooleanValue TYRANT_CAN_DO_CONTRACTS_TO_NON_ALLIES;
    public static final ForgeConfigSpec.DoubleValue CONTRACT_SPIRITUALITY_THRESHOLD;
    public static final ForgeConfigSpec.IntValue CONTRACT_HEALTH_THRESHOLD;
    public static final ForgeConfigSpec.IntValue CONTRACT_DAMAGE_BUFF;
    public static final ForgeConfigSpec.DoubleValue CONTRACT_REGENERATION_BUFF;
    public static final ForgeConfigSpec.DoubleValue CONTRACT_STAMINA_BUFF;
    public static final ForgeConfigSpec.IntValue CONTRACT_HEALTH_BUFF;
    public static final ForgeConfigSpec.IntValue CONTRACT_COST;

    //prohibition
    public static final ForgeConfigSpec.IntValue PROHIBITION_RADIUS;
    public static final ForgeConfigSpec.IntValue PROHIBITION_ABILITY_WINDOW;
    public static final ForgeConfigSpec.IntValue PROHIBITION_COOLDOWN;
    public static final ForgeConfigSpec.IntValue PROHIBITION_GENERAL_DURATION;
    public static final ForgeConfigSpec.IntValue PROHIBITION_ABILITY_DURATION;
    public static final ForgeConfigSpec.IntValue PROHIBITION_COST;
    public static final ForgeConfigSpec.BooleanValue PROHIBITION_AFFECTS_SELF;

    // Other Abilities
    public static final ForgeConfigSpec.IntValue EXILE_COST;
    public static final ForgeConfigSpec.IntValue EXILE_DURATION;
    public static final ForgeConfigSpec.IntValue EXILE_COOLDOWN;
    public static final ForgeConfigSpec.IntValue MIST_BLINK_COST;
    public static final ForgeConfigSpec.IntValue MIST_ANCHOR_BLINK_COST;
    public static final ForgeConfigSpec.IntValue AOJ_RADIUS;
    public static final ForgeConfigSpec.BooleanValue AOJ_MOB_GRIEFING;
    public static final ForgeConfigSpec.BooleanValue AOJ_PLAYER_GRIEFING;
    public static final ForgeConfigSpec.BooleanValue UNIVERSAL_OCEAN_ORDER;
    public static final ForgeConfigSpec.IntValue AURA_RADIUS;
    public static final ForgeConfigSpec.IntValue SENSE_OF_ORDER_RADIUS;
    public static final ForgeConfigSpec.DoubleValue AURA_MISCAST_CHANCE;
    public static final ForgeConfigSpec.DoubleValue BRIBE_CANCEL_CHANCE;
    public static final ForgeConfigSpec.IntValue BRIBE_MISCAST_RADIUS;
    public static final ForgeConfigSpec.IntValue BRIBE_DURATION;
    public static final ForgeConfigSpec.DoubleValue BRIBE_DAMAGE_MULTIPLIER;



    static {
        BUILDER.push("Potioneer");

        BUILDER.push("wheel_of_fortune_pathway");

        BUILDER.push("misc");
        BET_COOLDOWN = BUILDER.comment("Cooldown for the Bet Ability, in ticks.")
                .defineInRange("bet_cooldown", 20*10, 0, Integer.MAX_VALUE);

        BLOCK_AP_COOLDOWN = BUILDER.comment("Cooldown for the Block Appraisal Ability, in ticks.")
                .defineInRange("block_ap_cooldown", 20*15, 0, Integer.MAX_VALUE);

        BLOCK_AP_RANGE_PER_LEVEL = BUILDER.comment("How much the Block Appraisal Ability range increases with each sequence level.")
                .defineInRange("block_ap_range", 5, 0, Integer.MAX_VALUE);

        CONJURE_PICKAXE_COOLDOWN = BUILDER.comment("Cooldown for the Conjure Pickaxe Ability, in ticks.")
                .defineInRange("pickaxe_cooldown", 20*3, 0, Integer.MAX_VALUE);

        CONJURE_PICKAXE_COST = BUILDER.comment("Cost for recalling a conjured pickaxe.")
                .defineInRange("pickaxe_cost", 45, 0, Integer.MAX_VALUE);

        FATE_COOLDOWN = BUILDER.comment("Cooldown for the Luck Event/Fate Ability, in ticks.")
                .defineInRange("fate_cooldown", 20*60, 0, Integer.MAX_VALUE);

        FATE_COST = BUILDER.comment("Cost for the Luck Event/Fate Ability.")
                .defineInRange("fate_cost", 50, 0, Integer.MAX_VALUE);

        GAMBLING_COOLDOWN = BUILDER.comment("Cooldown for the Gambling Ability, in ticks.")
                .defineInRange("gambling_cooldown", 20*90, 0, Integer.MAX_VALUE);

        GAMBLING_COST = BUILDER.comment("Cost for the Gambling Ability.")
                .defineInRange("gambling_cost", 40, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        BUILDER.push("advanced");
        PATIENCE_TIME_LIMIT = BUILDER.comment(
                        "The Patience ability of the Wheel of Fortune pathway will aim to grant you luck up to N luck if you have less than that. " +
                        "\nThe growth rate is balanced such that, starting with 0 luck, after a certain amount of time, you reach a luck limit for your sequence, after which it takes much longer to get luck." +
                        "\nBy default, this value is 1, which corresponds to 20 minutes, or 1 minecraft day, to go from 0 luck to (at sequence level 7) 250 luck. at sequence level 6, itll take 20 minutes to go from 0 to 325 luck." +
                        "\nThey can still get luck after this limit, but it'll grow much slower." +
                        "\nThis value here will multiply by that time limit - values bigger than 1 will increase the time it takes to get luck, while values between 0 and 1 will decrease it." +
                        "\nSetting this to 2 means it'll take 2 minecraft days to reach that limit." +
                        "\nFor more details on how the actual patience effect calculates luck, and how this value affects it, check out this desmos graph: https://www.desmos.com/calculator/3uoitj78qi")
                .defineInRange("patience_time_multiplier", 1d, 0.05d, 30d);

        COOLDOWN_TARGET_ALLIES = BUILDER.comment("The Cooldown ability of the wheel of fortune. Should it target allies?" +
                        "\nIf set to True, whether or not an ally has an ability put on cooldown depends on their luck." +
                        "\nIf set to False, allies are completely exempt from being put on cooldown." +
                        "\nNote: The caster always runs that chance.")
                .define("cooldown_targets_allies", false);

        COOLDOWN_EFFECT_STACKS = BUILDER.comment("The Cooldown ability of the Wheel of Fortune Pathway, should it stack?" +
                        "\nIf set to false, then for a time after someone has their abilities put on cooldown, they can't have abilities put on cooldown again (cooldown for cooldowns lol)" +
                        "\nIf set to true, it'll happen as many times as the ability/effect is cast (includes charms)" +
                        "\nThere are exceptions - if person A puts person B on cooldown, and person B was already put on cooldown by someone of a lower level, person A will override that and put more abilities on cooldown." +
                        "\nAnd vice-versa, if person A were of a lower level than whoever put person B's abilities on cooldown, their attack will never succeed." +
                        "\nThis setting applies more if person A is the same sequence level as whoever put person B's abilities on cooldown." +
                        "\nAlso, this only applies to 'defensive cooldowns'. To have the ability cast apply a cooldown, check the next setting.")
                .define("cooldown_ability_stacks", true);

        COOLDOWN_ABILITY_CAST_COOLDOWN = BUILDER.comment("The Cooldown ability of the Wheel of Fortune pathway, should its cast have a cooldown on its targets?" +
                        "\nIf set to false, the ability will always disable abilities of everyone hit." +
                        "\nIf set to true, the cast will share a per-victim cooldown like described above - neither defensive/payback nor cast will put abilities on cooldown unless its of a higher level than the original cast." +
                        "\nI know this is hard to understand, I can't explain it well either. There's also the part where the caster is just immune to casts of this ability from beyonders of a lower level." +
                        "\nBasically, imagine the caster could cast the ability multiple times a second. Should each of those casts put abilities on cooldown or just the first one?")
                .define("cooldown_ability_cooldowns", false);
        BUILDER.pop();
        BUILDER.pop();

        BUILDER.push("tyrant_pathway");

        // Amplification
        AMPLIFICATION_DURATION = BUILDER.comment("How long, in ticks, should a Tyrant's Amplification last?")
                .defineInRange("amplification_duration", 20 * 60, 0, Integer.MAX_VALUE);

        AMPLIFICATION_COST = BUILDER.comment("How much spirituality should amplification/weakening cost?")
                .defineInRange("amplification_cost", 100, 0, Integer.MAX_VALUE);

        // Arrest Ability
        BUILDER.push("arrest");
        ARREST_MANUAL_COST = BUILDER.comment("How much spirituality should the AoE cast of Arrest cost?")
                .defineInRange("arrest_aoe_cost", 75, 0, Integer.MAX_VALUE);

        ARREST_COST = BUILDER.comment("How much spirituality should Arrest cost?")
                .defineInRange("arrest_cost", 20, 0, Integer.MAX_VALUE);

        ARREST_SAP = BUILDER.comment("How much spirituality should Arrest sap on cast?")
                .defineInRange("arrest_sap", 50, 0, Integer.MAX_VALUE);

        BUILDER.pop();

        // Calamity Ability
        BUILDER.push("calamity");
        CALAMITY_COOLDOWN_RAIN = BUILDER.comment("Cooldown, in ticks, for Calamity: Rain.")
                .defineInRange("rain_cooldown", 20 * 5, 0, Integer.MAX_VALUE);

        CALAMITY_COST_RAIN = BUILDER.comment("Spirituality cost for Calamity: Rain.")
                .defineInRange("rain_cost", 50, 0, Integer.MAX_VALUE);

        CALAMITY_COOLDOWN_THUNDER = BUILDER.comment("Cooldown, in ticks, for Calamity: Thunder.")
                .defineInRange("thunder_cooldown", 20 * 10, 0, Integer.MAX_VALUE);

        CALAMITY_COST_THUNDER = BUILDER.comment("Spirituality cost for Calamity: Thunder.")
                .defineInRange("thunder_cost", 75, 0, Integer.MAX_VALUE);

        CALAMITY_COOLDOWN_LEAP = BUILDER.comment("Cooldown, in ticks, for Calamity: Air Leap.")
                .defineInRange("leap_cooldown", 20 * 5, 0, Integer.MAX_VALUE);

        CALAMITY_COST_LEAP = BUILDER.comment("Spirituality cost for Calamity: Air Leap.")
                .defineInRange("leap_cost", 25, 0, Integer.MAX_VALUE);

        CALAMITY_COOLDOWN_WIND = BUILDER.comment("Cooldown, in ticks, for Calamity: Wind Shear.")
                .defineInRange("wind_cooldown", 20 * 10, 0, Integer.MAX_VALUE);

        CALAMITY_COST_WIND = BUILDER.comment("Spirituality cost for Calamity: Wind Shear.")
                .defineInRange("wind_cost", 50, 0, Integer.MAX_VALUE);

        CALAMITY_COOLDOWN_LUCK = BUILDER.comment("Cooldown, in ticks, for Calamity: Bad Luck.")
                .defineInRange("luck_cooldown", 20 * 7, 0, Integer.MAX_VALUE);

        CALAMITY_COST_LUCK = BUILDER.comment("Spirituality cost for Calamity: Bad Luck.")
                .defineInRange("luck_cost", 100, 0, Integer.MAX_VALUE);

        CALAMITY_COOLDOWN_ASTEROID = BUILDER.comment("Cooldown, in ticks, for Calamity: Asteroid.")
                .defineInRange("asteroid_cooldown", 20 * 15, 0, Integer.MAX_VALUE);

        CALAMITY_COST_ASTEROID = BUILDER.comment("Spirituality cost for Calamity: Asteroid.")
                .defineInRange("asteroid_cost", 150, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        // Water Spell Ability
        BUILDER.push("water_spell");
        WATER_SPELL_COST_CONJURE = BUILDER.comment("Spirituality cost to conjure water.")
                .defineInRange("conjure_cost", 5, 0, Integer.MAX_VALUE);

        WATER_SPELL_COST_ABSORB = BUILDER.comment("Spirituality restored/cost per block when consuming water.")
                .defineInRange("absorb_cost", 2, 0, Integer.MAX_VALUE);

        WATER_SPELL_COST_DROWNING = BUILDER.comment("Spirituality cost for Drowning.")
                .defineInRange("drowning_cost", 30, 0, Integer.MAX_VALUE);

        WATER_SPELL_COST_WATER_TRAP = BUILDER.comment("Spirituality cost for placing a Water Trap.")
                .defineInRange("water_trap_cost", 30, 0, Integer.MAX_VALUE);

        WATER_SPELL_COST_WATER_JET = BUILDER.comment("Spirituality cost for Water Jet.")
                .defineInRange("water_jet_cost", 10, 0, Integer.MAX_VALUE);

        WATER_SPELL_COST_HEALING = BUILDER.comment("Spirituality cost for Healing.")
                .defineInRange("healing_cost", 30, 0, Integer.MAX_VALUE);

        WATER_SPELL_COOLDOWN_CONSUME = BUILDER.comment("Cooldown, in ticks, for consuming water.")
                .defineInRange("consume_cooldown", 50, 0, Integer.MAX_VALUE);

        WATER_SPELL_COOLDOWN_HEALING = BUILDER.comment("Cooldown, in ticks, for Healing.")
                .defineInRange("healing_cooldown", 20 * 10, 0, Integer.MAX_VALUE);

        WATER_SPELL_COOLDOWN_WATER_TRAP = BUILDER.comment("Cooldown, in ticks, for placing a Water Trap.")
                .defineInRange("water_trap_cooldown", 20 * 5, 0, Integer.MAX_VALUE);
        BUILDER.pop();

        BUILDER.push("contract_settings");
        TYRANT_CAN_DO_CONTRACTS_TO_NON_ALLIES = BUILDER
                .comment("Should a tyrant beyonder be able to apply a contract to non-players?" +
                        "\nIf set to true, they can apply a contract to any animal or non-ally player (basically, excludes mobs), which will be automatically signed if its not for a player." +
                        "\nIf set to false, they can only do so with allies.")
                .define("contract_non_allies", true);
        CONTRACT_SPIRITUALITY_THRESHOLD = BUILDER
                .comment("Spirituality percentage threshold for contract conditions.")
                .defineInRange("spirituality_threshold", 0.5d, 0.0d, 1.0d);

        CONTRACT_HEALTH_THRESHOLD = BUILDER
                .comment("Health threshold value for contract conditions.")
                .defineInRange("health_threshold", 10, 0, Integer.MAX_VALUE);

        CONTRACT_DAMAGE_BUFF = BUILDER
                .comment("Bonus damage granted by contract rewards.")
                .defineInRange("damage_buff", 6, 0, Integer.MAX_VALUE);

        CONTRACT_REGENERATION_BUFF = BUILDER
                .comment("Regeneration amount granted by contract rewards.")
                .defineInRange("regeneration_buff", 0.5d, 0.0d, Double.MAX_VALUE);

        CONTRACT_STAMINA_BUFF = BUILDER
                .comment("Stamina amount provided by contract rewards.")
                .defineInRange("stamina_buff", 1.0d, 0.0d, Double.MAX_VALUE);

        CONTRACT_HEALTH_BUFF = BUILDER
                .comment("Max health granted by contract rewards.")
                .defineInRange("health_buff", 10, 0, Integer.MAX_VALUE);

        CONTRACT_COST = BUILDER.comment("Spirituality cost for Contract.")
                .defineInRange("contract_cost", 150, 0, Integer.MAX_VALUE);



        PROHIBITION_RADIUS = BUILDER.comment("Radius for the prohibition ability")
                .defineInRange("prohibition_radius", 16, 0, Integer.MAX_VALUE);

        PROHIBITION_ABILITY_WINDOW = BUILDER.comment("Time window for the prohibition ability to disable abilities." +
                        "\nWhen cast and applied to other entities, the first ability they cast within this time span (in ticks) will be disabled for everyone" +
                        "\nIn other words, they can also wait out this time for the ability to do nothing.")
                .defineInRange("prohibition_window", 20*15, 0, Integer.MAX_VALUE);

        PROHIBITION_COOLDOWN = BUILDER.comment("Cooldown, in ticks, for the prohibition ability")
                .defineInRange("prohibition_cooldown", 20*15, 0, Integer.MAX_VALUE);

        PROHIBITION_GENERAL_DURATION = BUILDER.comment("Duration, in ticks, for general prohibitions (flying, teleporting, etc...)")
                .defineInRange("prohibition_duration_general", 20*10, 0, Integer.MAX_VALUE);

        PROHIBITION_ABILITY_DURATION = BUILDER.comment("Duration, in ticks, for ability prohibitions")
                .defineInRange("prohibition_duration_ability", 20*10, 0, Integer.MAX_VALUE);

        PROHIBITION_COST = BUILDER.comment("Cost for the prohibition ability")
                .defineInRange("prohibition_cost", 150, 0, Integer.MAX_VALUE);

        PROHIBITION_AFFECTS_SELF = BUILDER.comment("Should the prohibition ability prohibit an ability cast by its caster?" +
                        "\nIn other words, if a player casts Ability Prohibition, should the next ability they themselves be disabled? Or should this only apply to other entities?")
                .define("prohibition_affects_caster", true);


        BUILDER.pop(); // Pop contract_settings

        // Miscellaneous Abilities
        BUILDER.push("misc_abilities");

        EXILE_COST = BUILDER.comment("Spirituality cost for Exile.")
                .defineInRange("exile_cost", 80, 0, Integer.MAX_VALUE);

        EXILE_COOLDOWN = BUILDER.comment("Cooldown, in ticks, for Exile.")
                .defineInRange("exile_cooldown", 20 * 10, 0, Integer.MAX_VALUE);

        EXILE_DURATION = BUILDER.comment("Duration, in ticks, for Exile.")
                .defineInRange("exile_duration", 20 * 30, 0, Integer.MAX_VALUE);

        MIST_BLINK_COST = BUILDER.comment("Spirituality cost for Mist Blinking.")
                .defineInRange("mist_blink_cost", 25, 0, Integer.MAX_VALUE);

        MIST_ANCHOR_BLINK_COST = BUILDER.comment("Spirituality cost for Anchored Mist Blinking.")
                .defineInRange("mist_blink_anchored_cost", 10, 0, Integer.MAX_VALUE);

        AOJ_COOLDOWN = BUILDER.comment("Cooldown, in ticks, for Area of Jurisdiction. Scales with sequence based on this value.")
                .defineInRange("aoj_cooldown", 30, 0, Integer.MAX_VALUE);

        AOJ_RADIUS = BUILDER.comment("Radius around the player to apply Area of Jurisdiction effect to non-allies.")
                .defineInRange("aoj_radius", 32, 0, Integer.MAX_VALUE);

        AOJ_MOB_GRIEFING = BUILDER.comment("Should the area of jurisdiction prevent mob griefing?")
                .define("aoj_mob_griefing", true);

        AOJ_PLAYER_GRIEFING = BUILDER.comment("Should the area of jurisdiction prevent non-ally player griefing?")
                .define("aoj_player_griefing", false);

        AURA_RADIUS = BUILDER.comment("Radius around the player to apply Aura effect to non-allies.")
                .defineInRange("aura_radius", 16, 0, Integer.MAX_VALUE);

        SENSE_OF_ORDER_RADIUS = BUILDER.comment("Radius around the player to apply Glowing effect from Sense of Order.")
                .defineInRange("soo_radius", 16, 0, Integer.MAX_VALUE);

        AURA_MISCAST_CHANCE = BUILDER.comment("Chance for aura-stricken enemies to SUCCESSFULY CAST abilities. Is affected by their luck")
                .defineInRange("aura_chance", 0.5f, 0f, 1f);

        UNIVERSAL_OCEAN_ORDER = BUILDER.comment("Should the Ocean Order ability work on any aggressive entity or only underwater ones?" +
                        "\nSetting this to true will make Swimmers not aggro any mobs by default, False will only work with mobs that are considered 'aquatic' (see 'underwater_mobs' below).")
                .define("universal_ocean_order", false);

        BRIBE_CANCEL_CHANCE = BUILDER.comment("Chance for entities affected by Bribe - Disorder to CAST abilities." +
                        "\nIn other words, one minus this value is the chance they don't cast abilities while affected by Bribe - Disorder")
                .defineInRange("bribe_chance", 1/3f, 0f, 1f);

        BRIBE_MISCAST_RADIUS = BUILDER.comment("Radius to test for Bribe - Disorder miscasts.")
                .defineInRange("bribe_radius", 10, 0, Integer.MAX_VALUE);

        BRIBE_DURATION = BUILDER.comment("Duration for the Bribe effect (on entities that have been bribed, not on the briber)")
                .defineInRange("bribe_duration", 20*30, 0, Integer.MAX_VALUE);

        BRIBE_DAMAGE_MULTIPLIER = BUILDER.comment("Damage Multiplier for victims of Bribe - Weakening")
                .defineInRange("bribe_multiplier", 0.5f, 0f, 1f);

        BUILDER.pop();


        BUILDER.pop(); // Pop tyrant_pathway
        BUILDER.pop(); // Pop Potioneer

        SPEC = BUILDER.build();
    }
}