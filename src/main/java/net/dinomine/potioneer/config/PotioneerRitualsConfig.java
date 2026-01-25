package net.dinomine.potioneer.config;

import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PotioneerRitualsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.IntValue SPIRIT_AMOUNT;
    public static final ForgeConfigSpec.ConfigValue<List<String>> RANDOM_INGREDIENTS;
    public static final ForgeConfigSpec.ConfigValue<String> WOF_INCENSE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> WOF_INGREDIENTS;
    public static final ForgeConfigSpec.ConfigValue<String> TYRANT_INCENSE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> TYRANT_INGREDIENTS;

    static{
        BUILDER.push("Ritual configs for Potioneer");


        SPIRIT_AMOUNT = BUILDER.comment("How many random spirits should be generated per world?")
                .defineInRange("spirit_amount", 5, 1, 25);

        ArrayList<String> possibleItems = new ArrayList<>();
        possibleItems.add(Items.BLAZE_POWDER.getDescriptionId());
        possibleItems.add(Items.GLISTERING_MELON_SLICE.getDescriptionId());
        possibleItems.add(Items.APPLE.getDescriptionId());
        possibleItems.add(Items.IRON_NUGGET.getDescriptionId());
        possibleItems.add(Items.GOLD_INGOT.getDescriptionId());
        possibleItems.add(Items.RABBIT_FOOT.getDescriptionId());
        possibleItems.add(Items.INK_SAC.getDescriptionId());
        possibleItems.add(Items.AMETHYST_SHARD.getDescriptionId());
        possibleItems.add(Items.EGG.getDescriptionId());
        possibleItems.add(Items.DIAMOND.getDescriptionId());
        possibleItems.add(Items.BONE.getDescriptionId());
        RANDOM_INGREDIENTS = BUILDER.comment("When generating spirits, what items can be considered for them?")
                .define("random_items", possibleItems);

        WOF_INCENSE = BUILDER.comment("Whats the incense id to identify the Wheel of Fortune sequence 0?")
                .define("wheel_of_fortune_incense", "chryon_essence");
        ArrayList<String> wof_ings = new ArrayList<>();
        wof_ings.add(Items.RABBIT_FOOT.toString());
        WOF_INGREDIENTS = BUILDER.comment("If no incense is provided, what items can identify the default Wheel of Fortune?")
                .define("wheel_of_fortune_items", wof_ings);

        TYRANT_INCENSE = BUILDER.comment("Whats the incense id to identify the Tyrant sequence 0?")
                .define("tyrant_incense", "chryon_essence");
        ArrayList<String> tyr_ings = new ArrayList<>();
        wof_ings.add(Items.PRISMARINE.toString());
        wof_ings.add(Items.COD.toString());
        wof_ings.add(Items.WATER_BUCKET.toString());
        TYRANT_INGREDIENTS = BUILDER.comment("If no incense is provided, what items can identify the default Tyrant?")
                .define("tyrant_items", tyr_ings);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
