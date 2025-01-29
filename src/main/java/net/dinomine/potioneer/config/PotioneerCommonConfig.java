package net.dinomine.potioneer.config;

import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PotioneerCommonConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOM_FORMULAS;
    public static final ForgeConfigSpec.ConfigValue<List<? extends String>> INGREDIENTS;

    static{
        BUILDER.push("Configs for Potioneer");

        //define your configs
        RANDOM_FORMULAS = BUILDER.comment("Should the mod randomize formulas from now on?")
                        .define("Random Formulas", false);
        ArrayList<String> ings = new ArrayList<>();
        ings.add(Items.REDSTONE.toString());
        ings.add(Items.BLAZE_POWDER.toString());
        ings.add(Items.BLAZE_ROD.toString());
        ings.add(Items.IRON_INGOT.toString());
        ings.add(Items.DIAMOND.toString());
        ings.add(Items.WHEAT_SEEDS.toString());
        ings.add(Items.PUMPKIN_SEEDS.toString());
        ings.add(Items.PUMPKIN.toString());
        ings.add(Items.APPLE.toString());
        ings.add(Items.SPIDER_EYE.toString());
        ings.add(Items.SCUTE.toString());
        ings.add(Items.RABBIT_HIDE.toString());
        ings.add(Items.FEATHER.toString());
        ings.add(Items.ENDER_PEARL.toString());
        ings.add(ModItems.SAPPHIRE.getId().toString());
        ings.add(ModItems.PECAN_LEAF.getId().toString());
        ings.add(ModItems.PECAN_SHELL.getId().toString());
        ings.add(ModItems.WANDERING_CACTUS_PRICK.getId().toString());
        ings.add(ModItems.SOLSEER.getId().toString());

//        //cactus sap vial
//        CompoundTag tag = new CompoundTag();
//        CompoundTag potionInfo = new CompoundTag();
//        potionInfo.putInt("amount", 1);
//        potionInfo.putString("name", "cactus_sap");
//        potionInfo.putInt("color", 65280);
//        tag.put("potion_info", potionInfo);
//
//        ings.add(ModItems.VIAL.getId().toString()
//         + tag);

//        CompoundTag tag1 = new CompoundTag();
//        tag1.


        INGREDIENTS = BUILDER.comment("Put here the id of items to be used for random formulas")
                .defineList("ingredient_entry", ings,
                        entry -> true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
