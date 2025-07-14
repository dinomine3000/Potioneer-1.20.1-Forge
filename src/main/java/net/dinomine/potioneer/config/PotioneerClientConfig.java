package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotioneerClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ORB_ON_RIGHT;
    public static final ForgeConfigSpec.ConfigValue<String> HOTBAR_POSITION;

    public enum HOTBAR_POS{
        TOP,
        LEFT,
        RIGHT
    }

    static{
        BUILDER.push("Configs for Potioneer");

        //define your configs
        ORB_ON_RIGHT = BUILDER.comment("Where should the spirituality orb render?")
                .define("Render on the right", true);

        HOTBAR_POSITION = BUILDER.comment("Where should the ability hotbar render?\nPossible values: TOP, LEFT, RIGHT")
                .define("Hotbar render position", "TOP");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
