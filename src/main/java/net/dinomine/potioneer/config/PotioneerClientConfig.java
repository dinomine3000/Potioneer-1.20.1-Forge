package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotioneerClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ORB_ON_RIGHT;
    public static final ForgeConfigSpec.EnumValue<HOTBAR_POS> HOTBAR_POSITION;
    public static final ForgeConfigSpec.DoubleValue ORB_OFFSET;
    public static final ForgeConfigSpec.ConfigValue<Integer> ORB_SCALE;
    public static final ForgeConfigSpec.DoubleValue HOTBAR_SCALE;

    public enum HOTBAR_POS{
        LEFT,
        TOP,
        RIGHT
    }

    static{
        BUILDER.push("Configs for Potioneer");

        //define your configs
        ORB_ON_RIGHT = BUILDER.comment("Where should the spirituality orb render?")
                .define("Render on the right", true);

        ORB_OFFSET = BUILDER.comment("Offset of the orb from its margin, between 0-1" +
                        "\nThe orb will be this percantage of half your screen width away from the respective margin" +
                        "\nie.: percent of 0 means it will be touching the game border, 1 will mean its smack down the middle")
                .defineInRange("Orb Offset Percentage", 0.2, 0.0, 1.0);

        ORB_SCALE = BUILDER.comment("Orb scale in integers between 1 and 10")
                .defineInRange("Orb Scale", 1, 1, 10);

        HOTBAR_POSITION = BUILDER.comment("Where should the ability hotbar render?\nPossible values: TOP, LEFT, RIGHT")
                .defineEnum("Hotbar render position", HOTBAR_POS.TOP, HOTBAR_POS.LEFT, HOTBAR_POS.RIGHT, HOTBAR_POS.TOP);

        HOTBAR_SCALE = BUILDER.comment("Hotbar Scale, in float, between 0 and 10")
                .defineInRange("Hotbar Scale", 1d, 1d, 5d);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
