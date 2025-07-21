package net.dinomine.potioneer.config;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PotioneerCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

//    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOM_FORMULAS;
//    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INGREDIENTS;
    public static final ForgeConfigSpec.DoubleValue MAXIMUM_INTRINSIC_ACTING_MULTIPLIER;
    public static final ForgeConfigSpec.ConfigValue<Boolean> DO_INTRINSIC_ACTING_MULTIPLIERS;
    public static final ForgeConfigSpec.DoubleValue UNIVERSAL_ACTING_MULTIPLIER;
    public static final ForgeConfigSpec.DoubleValue MAXIMUM_PASSIVE_ACTING_LIMIT;
    public static final ForgeConfigSpec.DoubleValue MINIMUM_PASSIVE_ACTING_LIMIT;
    public static final ForgeConfigSpec.IntValue SECONDS_TO_MAX_SPIRITUALITY;

    public static final ForgeConfigSpec.EnumValue<CharacteristicDropCriteria> CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE;

    public enum CharacteristicDropCriteria{
        ALWAYS,
        LOW_SANITY,
        NEVER
    }

    static{
        BUILDER.push("Configs for Potioneer");

        DO_INTRINSIC_ACTING_MULTIPLIERS = BUILDER.comment("Should each player have a random " +
                        "intrinsic multiplier for how much acting progress they get per action?")
                .define("intrinsic_acting_multipliers", true);

        MAXIMUM_INTRINSIC_ACTING_MULTIPLIER = BUILDER.comment("What is the maximum random intrinsic multiplier a player can have?" +
                        " Values smaller than 1 will make everyone progress much slower.")
                .defineInRange("maximum_intrinsic_multiplier", 1.3d, 0, Integer.MAX_VALUE);

        MAXIMUM_PASSIVE_ACTING_LIMIT = BUILDER.comment("What should be the maximum amount of acting progress" +
                        "a player can passively get per sequence without doing any special actions?" +
                        "\nEvery time someone advances a sequence, they get a random limit for their passive acting up to this value." +
                        "\nWithout doing anything, they will get this much percentage progress towards this sequence's acting progress. it does not carry over once they advance." +
                        "\n0 will disable it, 1 could allow people to digest a potion without doing anything")
                .defineInRange("passive_acting_limit", 0.6d, 0, 1);

        MINIMUM_PASSIVE_ACTING_LIMIT = BUILDER.comment("Guarantees that every player has at least this much passive acting limit." +
                        "If it is bigger than the maximum, they will flip, aka this becomes the new maximum, and the maximum becomes the minimum.")
                .defineInRange("minimum_passive_acting", 0d, 0, 1);

        UNIVERSAL_ACTING_MULTIPLIER = BUILDER.comment("This value will be multiplied by any amount of acting someone gets per action, " +
                        "\n0 will disable digestion gained through acting, and only affects active acting progress (so passive like described above will not be affected)")
                .defineInRange("universal_acting_progress_modifier", 1d, 0, Integer.MAX_VALUE);

        CHARACTERISTIC_DROP_CRITERIA_ENUM_VALUE = BUILDER.comment("What are the criteria for dropping a characteristic on death?")
                        .defineEnum("char_drop_criteria", CharacteristicDropCriteria.LOW_SANITY);

        SECONDS_TO_MAX_SPIRITUALITY = BUILDER.comment("How many seconds does it take for someone to regenerate their spirituality from 0 to full?" +
                "\nNote: this also affects how fast sanity regenerates, but now how fast it drops")
                .defineInRange("seconds_to_full", 15*60, 1, Integer.MAX_VALUE);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
