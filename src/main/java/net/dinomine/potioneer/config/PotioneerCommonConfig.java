package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PotioneerCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue DO_APTITUDE_PATHWAYS;
    public static final ForgeConfigSpec.DoubleValue APTITUDE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue UNIVERSAL_ACTING_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_ACTING_LIMIT;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_ACTING_RATE;
    public static final ForgeConfigSpec.BooleanValue PASSIVELY_DIGEST_ALL_CHARACTERISTICS;
    public static final ForgeConfigSpec.IntValue SECONDS_TO_MAX_SPIRITUALITY;
    public static final ForgeConfigSpec.IntValue MIN_SEQUENCE_TO_SWITCH_PATHWAYS;
    public static final ForgeConfigSpec.IntValue ARTIFACT_CONVERSION_CHANCE;
    public static final ForgeConfigSpec.IntValue ARTIFACT_CONVERSION_COOLDOWN;
    public static final ForgeConfigSpec.ConfigValue<List<String>> INTERCHANGEABLE_PATHWAYS;
    public static final ForgeConfigSpec.BooleanValue PUBLIC_GROUPS;
    public static final ForgeConfigSpec.BooleanValue ALLOW_CHANGING_PATHWAYS;
    public static final ForgeConfigSpec.DoubleValue CHANCE_TO_MAKE_ARTIFACT_ON_DEATH;
    public static final ForgeConfigSpec.DoubleValue UNIVERSAL_MAX_SPIRITUALITY_MULTIPLIER;
    public static final ForgeConfigSpec.BooleanValue CONSUME_PAGE_ON_USE;
    public static final ForgeConfigSpec.IntValue PRAYER_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue LOSE_PAGES_ON_DROP_SEQUENCE;
    public static final ForgeConfigSpec.DoubleValue PATIENCE_TIME_LIMIT;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_TARGET_ALLIES;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_EFFECT_STACKS;
    public static final ForgeConfigSpec.BooleanValue COOLDOWN_ABILITY_CAST_COOLDOWN;

    public static final ForgeConfigSpec.EnumValue<CharacteristicDropCriteria> CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE;
    public static final ForgeConfigSpec.BooleanValue DROP_ALL_CHARACTERISTICS;

    public enum CharacteristicDropCriteria{
        ALWAYS,
        LOW_SANITY,
        NEVER
    }

    static{
        BUILDER.push("Configs for Potioneer");

        UNIVERSAL_MAX_SPIRITUALITY_MULTIPLIER = BUILDER.comment("Here you can customize the maximum spirituality that anyone has." +
                        "\nIn effect, this serves to adjust the cost of abilities. If you set this to 2, everyone will have double the spirituality, making abilities half the cost.")
                .defineInRange("universal_max_spirituality_multiplier", 1, 0.2, Integer.MAX_VALUE);

        DO_APTITUDE_PATHWAYS = BUILDER.comment("Should players have a hidden pathway that is their aptitude?" +
                "\nThey get more lucky getting formulas for this pathway, and digest its characteristics faster")
                .define("intrinsic_aptitudes", true);

        APTITUDE_MULTIPLIER = BUILDER.comment("If the above is true, what should be the intrinsic acting multiplier a player gets?")
                .defineInRange("aptitude_multiplier", 1.3d, 0, Integer.MAX_VALUE);

        PASSIVE_ACTING_LIMIT = BUILDER.comment("How much should a characteristic be digested by doing nothing?" +
                        "\n0 will disable passive acting, 1 will have all players eventually digest their characteristics")
                .defineInRange("passive_acting_limit", 0.5d, 0, 1);

        PASSIVE_ACTING_RATE = BUILDER.comment("How long should a characteristic take to passively digest up to its limit, in seconds?" +
                        "\nDefault is 1 hour to reach the limit")
                .defineInRange("passive_acting_period", 3600d, 1, Integer.MAX_VALUE);

        PASSIVELY_DIGEST_ALL_CHARACTERISTICS = BUILDER.comment("Should players passively digest all characteristics, as opposed to just their current one?" +
                        "\ntrue will make them digest every characteristic up to their passive acting limit, false will make this only happen to their current characteristic" +
                        "\n(this characteristic is their highest level characteristics, and if there are more than 1 such characteristics, this refers to their last consumed one.")
                .define("passive_digest_all", false);

        UNIVERSAL_ACTING_MULTIPLIER = BUILDER.comment("This value will be multiplied by any amount of acting someone gets per action, " +
                        "\n0 will disable digestion gained through acting, and only affects active acting progress (so passive like described above will not be affected)")
                .defineInRange("universal_acting_progress_modifier", 1d, 0, Integer.MAX_VALUE);

        CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE = BUILDER.comment("What are the criteria for dropping a characteristic on death?" +
                        "\nOf note, if someone consumes a characteristic, but the result is that their maximum sanity is too low to live, they will always drop their latest characteristic, even if this is set to never.")
                .defineEnum("char_drop_criteria", CharacteristicDropCriteria.LOW_SANITY);

        DROP_ALL_CHARACTERISTICS = BUILDER.comment("When someone drops a characteristics, should they drop all they have, or just their most recent highest level one?" +
                        "\nTrue will make them drop everything, false will make them drop their highest sequence characteristic only, and if there are more than one such characteristics, it will drop the most recent one.")
                .define("drop_all_characteristics", false);

        ALLOW_CHANGING_PATHWAYS = BUILDER.comment("Should players be able to change pathways completely? This is not related to being a dual-pathway beyonder or switching at sequence level X." +
                "\nSetting this to true means that people can drop a sequence level at level 9, becoming beyonderless and allowing them to drink any other pathway potion." +
                "\nSetting this to false means that you can never drop the first characteristic you consumed, meaning you can never change pathways if you don't use commands" +
                "\nIf \"min_level_to_switch\" is not -1, people can still switch pathways by consuming another potion, regardless of the value of this setting!" +
                "\nAnd of note, if you set \"drop_all_characteristics\" to drop all characteristics, this will prevent that. When attempting to drop all characteristics, if this value is true, the player will keep their oldest characteristic")
                .define("can_change_pathways", false);

        SECONDS_TO_MAX_SPIRITUALITY = BUILDER.comment("How many seconds does it take for someone to regenerate their spirituality from 0 to full?" +
                        "\nNote: this also affects how fast sanity regenerates, but not how fast it drops")
                .defineInRange("seconds_to_full", 15*60, 1, Integer.MAX_VALUE);

        CHANCE_TO_MAKE_ARTIFACT_ON_DEATH = BUILDER.comment("What is the chance, when someone dies and they drop their characteristic, that this characteristic" +
                        "fuses with one of their items. " +
                        "\nIf this chance passes (not dependent on the players luck) then it'll look for a valid item in the inventory." +
                        "\nIf they drop more than 1 characteristic, this chance will be applied to each one individually.")
                .defineInRange("chance_to_make_artifact", 0.25, 0, 1);

        MIN_SEQUENCE_TO_SWITCH_PATHWAYS = BUILDER.comment("What is the minimum sequence level to switch pathways?" +
                        "\nWhen someone tries to consume a potion of a different neighboring pathways, it must be at least this level to not make the minigame harder." +
                        "\nThis means someone could switch pathways as a sequence 7 if they consume a potion of this sequence level (though it'll be nigh impossible due to jumping sequence levels)" +
                        "\nIf the option \"can_change_pathways\" is false, that only applies to switching pathways by becoming beyonderless, not to this case. so if you truly don't want people to switch pathways ever, set this to -1." +
                        "\n10 means anyone can switch to a neighboring pathway at any level, -1 means they never can.")
                .defineInRange("min_level_to_switch", 4, -1, 10);

        ARTIFACT_CONVERSION_CHANCE = BUILDER.comment("Every tick, each characteristic in the player's inventory has a 1 in N chance to try to generate an artifact based on the player's inventory." +
                        "\nHere you define that N. For instance, if N = 200, it means that every tick, a characteristic has a 1/200 chance of creating an artifact. Setting N to a negative value makes these conversions not happen.")
                .defineInRange("artifact_conversion_chance", 2000, -1, Integer.MAX_VALUE);

        ARTIFACT_CONVERSION_COOLDOWN = BUILDER.comment("To prevent farming if \"artifact_conversion_chance\" is too high, you can set a cooldown here, in seconds, for artifact conversion." +
                        "\nValues like 20 will be interpreted as 20 seconds, aka 400 ticks.")
                .defineInRange("artifact_conversion_cooldown", 20*60, 0, Integer.MAX_VALUE);

        PUBLIC_GROUPS = BUILDER.comment("Should every player be able to see every group in the server or just admins?\n"
                        + "True means everyone can see every group and their players, false will mean they can only see groups they're in.\n"
                        + "Note that this doesnt put them in the group - they'll still need the password to get in.")
                .define("public_groups", false);

        INTERCHANGEABLE_PATHWAYS = BUILDER.comment("What pathways are interchangeable? Pathways are defined using their numeric id, and each entry in the list corresponds to a pathway group." +
                "\nIf more than 1 entry contain the same id, those entries will be discarded." +
                "\nPathways not present here will not be interchangeable." +
                "\nThe default format for a pathway group is just the pathway IDs separated by a hyphen (-)" +
                "\nThe IDs for the 5 default pathways are: Miner - 0, Swimmer - 1, Trickster - 2, Warrior - 3, Crafter - 4")
                        .define("pathway_groups", new ArrayList<>(List.of("1-3", "2-4")));

        CONSUME_PAGE_ON_USE = BUILDER.comment("When reading a knowledge page and adding it to the beyonder book, should the page be consumed?")
                .define("consume_page_on_use", false);

        PRAYER_COOLDOWN = BUILDER.comment("How long, in minutes, must a player wait before being able to increase their reputation with a god again?" +
                        "\nBy default its 18 minutes, which means if they pray to a god, then for the next 18 minutes praying wont increase the reputation with any god.")
                .defineInRange("prayer_cooldown_minutes", 18, 1, Integer.MAX_VALUE);

        LOSE_PAGES_ON_DROP_SEQUENCE = BUILDER.comment("Suppose someone advanced to Seer and gained mysticism knowledge. This knowledge was gained through their characteristic, of course." +
                        "\nIn Lord of the Mysteries, this works nice and well, but here you can drop levels or even lose all your characteristics and still live." +
                        "\nThe question is, then, should someone in this situation lose that knowledge? If someone gains knowledge (pages for their beyonder book) and then loses that characteristic,\n" +
                        "should they keep those pages or lose them?" +
                        "\nJust a warning, if someone gains pages while this is set to 'false', they wont lose them even if you later set this to 'true' unless you use a command to fully reset their pages.")
                .define("lose_pages_on_drop_sequence", false);

        PATIENCE_TIME_LIMIT = BUILDER.comment("\n\n------Ability Configs-----\n" +
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
        SPEC = BUILDER.build();
    }
}
