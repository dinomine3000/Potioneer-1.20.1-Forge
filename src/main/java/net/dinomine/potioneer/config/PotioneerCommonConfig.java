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

    static{
        BUILDER.push("Configs for Potioneer");

        //define your configs
//        RANDOM_FORMULAS = BUILDER.comment("Should the mod randomize formulas from now on?")
//                        .define("Random Formulas", false);
//        ArrayList<String> ings = new ArrayList<>();
//        ings.add(Items.REDSTONE.toString());
//        ings.add(Items.BLAZE_POWDER.toString());
//        ings.add(Items.BLAZE_ROD.toString());
//        ings.add(Items.IRON_INGOT.toString());
//        ings.add(Items.DIAMOND.toString());
//        ings.add(Items.WHEAT_SEEDS.toString());
//        ings.add(Items.PUMPKIN_SEEDS.toString());
//        ings.add(Items.PUMPKIN.toString());
//        ings.add(Items.APPLE.toString());
//        ings.add(Items.SPIDER_EYE.toString());
//        ings.add(Items.SCUTE.toString());
//        ings.add(Items.RABBIT_HIDE.toString());
//        ings.add(Items.FEATHER.toString());
//        ings.add(Items.ENDER_PEARL.toString());
//        ings.add(ModItems.SAPPHIRE.getId().toString());
//        ings.add(ModItems.PECAN_LEAF.getId().toString());
//        ings.add(ModItems.PECAN_SHELL.getId().toString());
//        ings.add(ModItems.WANDERING_CACTUS_PRICK.getId().toString());
//        ings.add(ModItems.SOLSEER.getId().toString());
//
////        //cactus sap vial
////        CompoundTag tag = new CompoundTag();
////        CompoundTag potionInfo = new CompoundTag();
////        potionInfo.putInt("amount", 1);
////        potionInfo.putString("name", "cactus_sap");
////        potionInfo.putInt("color", 65280);
////        tag.put("potion_info", potionInfo);
////
////        ings.add(ModItems.VIAL.getId().toString()
////         + tag);
//
////        CompoundTag tag1 = new CompoundTag();
////        tag1.
//
//
//        INGREDIENTS = BUILDER.comment("Put here the id of items to be used for random formulas")
//                .defineList("ingredient_entry", ings,
//                        entry -> true);

        DO_INTRINSIC_ACTING_MULTIPLIERS = BUILDER.comment("Should each player have a random " +
                        "intrinsic multiplier for how much acting progress they get per action?")
                .define("intrinsic_acting_multipliers", true);

        MAXIMUM_INTRINSIC_ACTING_MULTIPLIER = BUILDER.comment("What is the maximum random intrinsic multiplier a player can have?" +
                        "0 will make some people progress much slower.")
                .defineInRange("maximum_intrinsic_multiplier", 1.3d, 0, 10000);

        MAXIMUM_PASSIVE_ACTING_LIMIT = BUILDER.comment("What should be the maximum amount of acting progress" +
                        "a player can passively get per sequence without doing any special actions?" +
                        "\nEvery time someone advances a sequence, they get a random limit for their passive acting up to this value." +
                        "\nWithout doing anything, they will get this much percentage progress towards this sequence's acting progress. it does not carry over once they advance." +
                        "\n0 will disable it, 1 could allow people to digest a potion without doing anything")
                .defineInRange("passive_acting_limit", 0.6d, 0, 1);

        MINIMUM_PASSIVE_ACTING_LIMIT = BUILDER.comment("Guarantees that every player has at least this much passive acting limit." +
                        "If it is bigger than the maximum, they will flip.")
                .defineInRange("minimum_passive_acting", 0d, 0, 1);

        UNIVERSAL_ACTING_MULTIPLIER = BUILDER.comment("This value will be multiplied by any amount of acting someone gets per action, " +
                        "\n0 will disable digestion gained through acting, and only affects active acting progress (so passive like described above will not be affected)")
                .defineInRange("universal_acting_progress_modifier", 1d, 0, 10000);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
