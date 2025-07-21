package net.dinomine.potioneer.config;

import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class PotioneerRitualsConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<String> WOF_INCENSE;
    public static final ForgeConfigSpec.ConfigValue<List<String>> WOF_INGREDIENTS;

    static{
        BUILDER.push("Configs for Potioneer");

        WOF_INCENSE = BUILDER.comment("Whats the incense id to identify the Wheel of Fortune sequence 0?")
                .define("wheel_of_fortune_incense", "iberic_lince");
        ArrayList<String> wof_ings = new ArrayList<>();
        wof_ings.add(Items.RABBIT_FOOT.toString());
        WOF_INGREDIENTS = BUILDER.comment("If no incense is provided, what items can identify the default Wheel of Fortune?")
                .define("wheel_of_fortune_items", wof_ings);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
