package net.dinomine.potioneer.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class PotioneerClientConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    static{
        BUILDER.push("Configs for Potioneer");

        //define your configs

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
