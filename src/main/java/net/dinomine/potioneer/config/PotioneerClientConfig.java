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
    public static final ForgeConfigSpec.ConfigValue<Boolean> POTION_DIGESTED_MESSAGE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> HOTBAR_ABILITY_CASES_WITH_OUTLINE;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ALTERNATIVE_DISABLED_SYMBOL;

    // --- Layout Configs ---
    public static final ForgeConfigSpec.DoubleValue LEFT_X;
    public static final ForgeConfigSpec.DoubleValue LEFT_Y;
    public static final ForgeConfigSpec.DoubleValue LEFT_WIDTH;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RENDER_LEFT;

    public static final ForgeConfigSpec.DoubleValue RIGHT_X;
    public static final ForgeConfigSpec.DoubleValue RIGHT_Y;
    public static final ForgeConfigSpec.DoubleValue RIGHT_WIDTH;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RENDER_RIGHT;

    public static final ForgeConfigSpec.DoubleValue CENTER_X;
    public static final ForgeConfigSpec.DoubleValue CENTER_Y;
    public static final ForgeConfigSpec.DoubleValue CENTER_WIDTH;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RENDER_CENTER;

    public enum HOTBAR_POS {
        LEFT,
        TOP,
        RIGHT
    }

    static {
        BUILDER.push("Configs for Potioneer");

        ORB_ON_RIGHT = BUILDER.comment("Where should the spirituality orb render?")
                .define("Render on the right", true);

        ORB_OFFSET = BUILDER.comment("Offset of the orb from its left margin, between 0-1")
                .defineInRange("Orb Offset Percentage", 0.75, 0.0, 1.0);

        ORB_SCALE = BUILDER.comment("Orb scale in integers between 1 and 10")
                .defineInRange("Orb Scale", 1, 1, 10);

        HOTBAR_POSITION = BUILDER.comment("Where should the ability hotbar render?")
                .defineEnum("Hotbar render position", HOTBAR_POS.TOP, HOTBAR_POS.LEFT, HOTBAR_POS.RIGHT, HOTBAR_POS.TOP);

        HOTBAR_SCALE = BUILDER.comment("Hotbar Scale, in decimal, between 1 and 5")
                .defineInRange("Hotbar Scale", 1d, 1d, 5d);

        POTION_DIGESTED_MESSAGE = BUILDER.comment("Should you get a message of fully digested potion every time you log in?")
                .define("Potion Digested Message on Join", false);

        HOTBAR_ABILITY_CASES_WITH_OUTLINE = BUILDER.comment("Hotbar outline configuration")
                .define("Hotbar Outlines", false);

        ALTERNATIVE_DISABLED_SYMBOL = BUILDER.comment("Whether or not to use the alternative disabled symbol")
                .define("Alternative Disabled Symbol", false);

        BUILDER.push("HUD Layout Settings");
        BUILDER.comment("HUD settings allow you to control 3 different \"lanes\" of the HUDs used by the mod." +
                "\nEach HUD lane or group can be seen as a queue. When an ability-related HUD wants to render, itll render from top to bottom to fill out the lane." +
                "\nYou can define here the position and sizes of each lanes:" +
                "\nX and Y positions are percentages of total screen width and height, respectively, to anchor the HUD lane's top-left corner." +
                "\nWidth is a percentage of total screen width to define how wide each HUD will be. Their heights depend on the width available, so they'll scale automatically." +
                "\nFinally, you can enable or disable specific lanes. HUDs that belong to a disabled lane will fallback to another enabled one if one exists.");

        LEFT_X = BUILDER.comment("Left panel X position percentage (0.0 to 1.0)")
                .defineInRange("Left X", 0.02, 0.0, 1.0);
        LEFT_Y = BUILDER.comment("Left panel Y position percentage (0.0 to 1.0)")
                .defineInRange("Left Y", 0.1, 0.0, 1.0);
        LEFT_WIDTH = BUILDER.comment("Left panel width percentage (0.0 to 1.0)")
                .defineInRange("Left Width", 0.30, 0.0, 1.0);
        RENDER_LEFT = BUILDER.comment("Render left panel")
                .define("Render Left", true);

        RIGHT_X = BUILDER.comment("Right panel X position percentage (0.0 to 1.0)")
                .defineInRange("Right X", 0.68, 0.0, 1.0);
        RIGHT_Y = BUILDER.comment("Right panel Y position percentage (0.0 to 1.0)")
                .defineInRange("Right Y", 0.1, 0.0, 1.0);
        RIGHT_WIDTH = BUILDER.comment("Right panel width percentage (0.0 to 1.0)")
                .defineInRange("Right Width", 0.3, 0.0, 1.0);
        RENDER_RIGHT = BUILDER.comment("Render right panel")
                .define("Render Right", true);

        CENTER_X = BUILDER.comment("Center panel X position percentage (0.0 to 1.0)")
                .defineInRange("Center X", 0.35, 0.0, 1.0);
        CENTER_Y = BUILDER.comment("Center panel Y position percentage (0.0 to 1.0)")
                .defineInRange("Center Y", 0.1, 0.0, 1.0);
        CENTER_WIDTH = BUILDER.comment("Center panel width percentage (0.0 to 1.0)")
                .defineInRange("Center Width", 0.3, 0.0, 1.0);
        RENDER_CENTER = BUILDER.comment("Render center panel")
                .define("Render Center", true);

        BUILDER.pop(); // Pop HUD Layout Settings

        BUILDER.pop(); // Pop Configs for Potioneer
        SPEC = BUILDER.build();
    }
}