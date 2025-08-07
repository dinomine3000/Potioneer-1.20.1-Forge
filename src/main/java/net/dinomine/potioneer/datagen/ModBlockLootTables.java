package net.dinomine.potioneer.datagen;

import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.SpiritFruitCropBlock;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;
import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {
    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {
        this.dropSelf(ModBlocks.SAPPHIRE_BLOCK.get());
        this.dropSelf(ModBlocks.RAW_SAPPHIRE_BLOCK.get());
        this.dropSelf(ModBlocks.RITUAL_PEDESTAL.get());
        this.dropSelf(ModBlocks.RITUAL_ALTAR.get());
        this.dropSelf(ModBlocks.POTION_CAULDRON.get());


        LootItemCondition.Builder lootitemcondition$builder = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.SPIRIT_FRUIT_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(SpiritFruitCropBlock.AGE, 5));

        this.add(ModBlocks.SPIRIT_FRUIT_CROP.get(),
                block -> createCropDrops(ModBlocks.SPIRIT_FRUIT_CROP.get(), ModItems.SPIRIT_FRUIT.get(),
                        ModItems.SPIRIT_FRUIT_SEEDS.get(), lootitemcondition$builder)
                        .withPool(LootPool.lootPool()
                                .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.SPIRIT_FRUIT_CROP.get())
                                        .setProperties(StatePropertiesPredicate.Builder.properties().hasProperty(BlockStateProperties.AGE_5, 5)))
                                .add(LootItem.lootTableItem(ModItems.ROOTS.get())
                                        .when(LootItemRandomChanceCondition.randomChance(0.1f))
                                )
                                .setRolls(ConstantValue.exactly(1))
                        )
        );

    }

    protected LootTable.Builder createCopperLikeOreDrops(Block pBlock, Item item) {
        return createSilkTouchDispatchTable(pBlock,
                this.applyExplosionDecay(pBlock,
                        LootItem.lootTableItem(item)
                                .apply(SetItemCountFunction.setCount(UniformGenerator.between(2.0F, 5.0F)))
                                .apply(ApplyBonusCount.addOreBonusCount(Enchantments.BLOCK_FORTUNE))));
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return List.of(
                ModBlocks.SAPPHIRE_BLOCK.get(),
                ModBlocks.RAW_SAPPHIRE_BLOCK.get(),
                ModBlocks.RITUAL_PEDESTAL.get(),
                ModBlocks.RITUAL_ALTAR.get(),
                ModBlocks.POTION_CAULDRON.get(),
                ModBlocks.SPIRIT_FRUIT_CROP.get()
        );
    }
}