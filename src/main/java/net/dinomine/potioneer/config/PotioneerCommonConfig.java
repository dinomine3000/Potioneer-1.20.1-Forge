package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PotioneerCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

//    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOM_FORMULAS;
//    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INGREDIENTS;
    public static final ForgeConfigSpec.BooleanValue DO_APTITUDE_PATHWAYS;
    public static final ForgeConfigSpec.DoubleValue APTITUDE_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue UNIVERSAL_ACTING_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_ACTING_LIMIT;
    public static final ForgeConfigSpec.DoubleValue PASSIVE_ACTING_RATE;
    public static final ForgeConfigSpec.BooleanValue PASSIVELY_DIGEST_ALL_CHARACTERISTICS;
    public static final ForgeConfigSpec.IntValue SECONDS_TO_MAX_SPIRITUALITY;
    public static final ForgeConfigSpec.IntValue MIN_SEQUENCE_TO_SWITCH_PATHWAYS;
    public static final ForgeConfigSpec.ConfigValue<List<String>> INTERCHANGEABLE_PATHWAYS;
    public static final ForgeConfigSpec.BooleanValue PUBLIC_GROUPS;
    public static final ForgeConfigSpec.BooleanValue ALLOW_CHANGING_PATHWAYS;
    public static final ForgeConfigSpec.DoubleValue CHANCE_TO_MAKE_ARTIFACT_ON_DEATH;

    public static final ForgeConfigSpec.EnumValue<CharacteristicDropCriteria> CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE;
    public static final ForgeConfigSpec.BooleanValue DROP_ALL_CHARACTERISTICS;

    public enum CharacteristicDropCriteria{
        ALWAYS,
        LOW_SANITY,
        NEVER
    }

    static{
        BUILDER.push("Configs for Potioneer");

        DO_APTITUDE_PATHWAYS = BUILDER.comment("Should players have a hidden pathway that is their aptitude?" +
                "They get more lucky getting formulas for this pathway, and digest its characteristics faster")
                .define("intrinsic_aptitudes", true);

        APTITUDE_MULTIPLIER = BUILDER.comment("If the above is true, what should be the intrinsic acting multiplier a player gets?")
                .defineInRange("aptitude_multiplier", 1.3d, 0, Integer.MAX_VALUE);

        PASSIVE_ACTING_LIMIT = BUILDER.comment("How much should a characteristic be digested by doing nothing?" +
                        "0 will disable passive acting, 1 will have all players eventually digest their characteristics")
                .defineInRange("passive_acting_limit", 0.5d, 0, 1);

        PASSIVE_ACTING_RATE = BUILDER.comment("How long should a characteristic take to passively digest up to its limit, in seconds? Default is 1 hour to reach the limit")
                .defineInRange("passive_acting_period", 3600d, 1, Integer.MAX_VALUE);

        PASSIVELY_DIGEST_ALL_CHARACTERISTICS = BUILDER.comment("Should players passively digest all characteristics, as opposed to just their current one?" +
                        "true will make them digest every characteristic up to their passive acting limit, false will make this only happen to their current characteristic" +
                        "(this characteristic is their highest level characteristics, and if there are more than 1 such characteristics, this refers to their last consumed one.")
                .define("passive_digest_all", false);

        UNIVERSAL_ACTING_MULTIPLIER = BUILDER.comment("This value will be multiplied by any amount of acting someone gets per action, " +
                        "\n0 will disable digestion gained through acting, and only affects active acting progress (so passive like described above will not be affected)")
                .defineInRange("universal_acting_progress_modifier", 1d, 0, Integer.MAX_VALUE);

        CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE = BUILDER.comment("What are the criteria for dropping a characteristic on death?")
                .defineEnum("char_drop_criteria", CharacteristicDropCriteria.LOW_SANITY);

        DROP_ALL_CHARACTERISTICS = BUILDER.comment("When someone drops a characteristics, should they drop all they have, or just their most recent highest level one?" +
                        "\nThis will let them change pathways, regardless of the next config." +
                        "\nTrue will make them drop everything, false will make them drop their highest sequence characteristic only, and if there are more than one such characteristics, it will drop the most recent one.")
                .define("drop_all_characteristics", false);

        ALLOW_CHANGING_PATHWAYS = BUILDER.comment("Should players be able to change pathways completely? This is not related to being a dual-pathway beyonder or switching at sequence level X." +
                "\nSetting this to true means that people can drop a sequence level at level 9, becoming beyonderless and allowing them to drink any other pathway potion." +
                "\nSetting this to false means that you can never drop the first characteristic you consumed, meaning you can never change pathways if you don't use commands" +
                "\nIf \"min_level_to_switch\" is not -1, people can still switch pathways by consuming another potion, regardless of the value of this setting!")
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

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
