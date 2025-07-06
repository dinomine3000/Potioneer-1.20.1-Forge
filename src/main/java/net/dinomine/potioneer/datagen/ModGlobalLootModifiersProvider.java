package net.dinomine.potioneer.datagen;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.loot.AddItemModifier;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;

public class ModGlobalLootModifiersProvider extends GlobalLootModifierProvider {

    private static final float formulaChance = 0.8f;
    private static final float coinChance = 0.4f;
    public ModGlobalLootModifiersProvider(PackOutput output) {
        super(output, Potioneer.MOD_ID);
    }

    @Override
    protected void start() {

        add("coin_from_archeology_pyramid", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_pyramid")).build(),
                LootItemRandomChanceCondition.randomChance(coinChance).build()}, ModItems.COIN_ITEM.get()));

        add("coin_from_archeology_well", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_well")).build(),
                LootItemRandomChanceCondition.randomChance(coinChance).build()}, ModItems.COIN_ITEM.get()));

        add("coin_from_archeology_cold", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/ocean_ruin_cold")).build(),
                LootItemRandomChanceCondition.randomChance(coinChance).build()}, ModItems.COIN_ITEM.get()));

        add("coin_from_archeology_warm", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/ocean_ruin_warm")).build(),
                LootItemRandomChanceCondition.randomChance(coinChance).build()}, ModItems.COIN_ITEM.get()));

        add("formula_from_archeology_pyramid", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_pyramid")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build()}, ModItems.FORMULA.get()));

        add("formula_from_archeology_well", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/desert_well")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build()}, ModItems.FORMULA.get()));

        add("formula_from_archeology_cold", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/ocean_ruin_cold")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build()}, ModItems.FORMULA.get()));

        add("formula_from_archeology_warm", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("archaeology/ocean_ruin_warm")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build()}, ModItems.FORMULA.get()));


        add("formula_from_mineshaft", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/abandoned_mineshaft")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_shipwreck", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/shipwreck_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build()
        }, ModItems.FORMULA.get()));

        add("formula_from_dungeon", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/simple_dungeon")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_mansion", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/woodland_mansion")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_temple", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/jungle_temple")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_pyramid", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/desert_pyramid")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_ancient_city", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/ancient_city")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_village_temple", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/village/village_temple")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_village_blacksmith", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/village/village_weaponsmith")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_village_toolsmith", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/village/village_toolsmith")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_pillager", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/pillager_outpost")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_portal", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/ruined_portal")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

        add("formula_from_buried", new AddItemModifier(new LootItemCondition[] {
                new LootTableIdCondition.Builder(new ResourceLocation("chests/buried_treasure")).build(),
                LootItemRandomChanceCondition.randomChance(formulaChance).build() }, ModItems.FORMULA.get()));

//        add("pine_cone_from_grass", new AddItemModifier(new LootItemCondition[] {
//                LootItemBlockStatePropertyCondition.hasBlockStateProperties(Blocks.GRASS).build(),
//                LootItemRandomChanceCondition.randomChance(0.35f).build()}, ModItems.PINE_CONE.get()));
//
//        add("pine_cone_from_creeper", new AddItemModifier(new LootItemCondition[] {
//                new LootTableIdCondition.Builder(new ResourceLocation("entities/creeper")).build() }, ModItems.PINE_CONE.get()));
//
//        add("metal_detector_from_jungle_temples", new AddItemModifier(new LootItemCondition[] {
//                new LootTableIdCondition.Builder(new ResourceLocation("chests/jungle_temple")).build() }, ModItems.METAL_DETECTOR.get()));
//

    }
}
