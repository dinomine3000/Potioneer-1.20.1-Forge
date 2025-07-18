package net.dinomine.potioneer.config;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotioneerFormulaConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> OVERWRITE_FORMULAS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> FORMULAS_TO_OVERWRITE;

    static{
        BUILDER.push("Formula configs for Potioneer");

        //define your configs
        OVERWRITE_FORMULAS = BUILDER.comment("Should the formulas be overwritten by this config?")
                        .define("Overwrite formulas?", false);

        FORMULAS_TO_OVERWRITE = BUILDER.comment("Put here the id of items to be used for random formulas")
                        .defineList("fuckyou", Arrays.asList("fuck", "you"), obj -> true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
