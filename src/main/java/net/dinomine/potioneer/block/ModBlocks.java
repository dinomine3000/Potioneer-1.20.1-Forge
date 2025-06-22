package net.dinomine.potioneer.block;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.custom.MinerLightSourceBlock;
import net.dinomine.potioneer.block.custom.MutatedMushroom;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.block.custom.PotionCauldronBlock;
import net.dinomine.potioneer.block.custom.SoundBlock;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, Potioneer.MOD_ID);


    public static final RegistryObject<Block> SAPPHIRE_BLOCK =
            registerBlock("sapphire_block",
                    () -> new Block(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK).sound(SoundType.AMETHYST).requiresCorrectToolForDrops()
                            .strength(2.0F)));

    public static final RegistryObject<Block> RAW_SAPPHIRE_BLOCK =
            registerBlock("raw_sapphire_block",
                    () -> new Block(BlockBehaviour.Properties.copy(Blocks.RAW_IRON_BLOCK).sound(SoundType.AMETHYST)));

    public static final RegistryObject<Block> SAPPHIRE_ORE = registerBlock("sapphire_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.STONE)
                    .strength(2f).requiresCorrectToolForDrops(), UniformInt.of(1, 2)));

    public static final RegistryObject<Block> DEEPSLATE_SAPPHIRE_ORE = registerBlock("deepslate_sapphire_ore",
            () -> new DropExperienceBlock(BlockBehaviour.Properties.copy(Blocks.DEEPSLATE)
                    .strength(5f).requiresCorrectToolForDrops(), UniformInt.of(2, 3)));

    public static final RegistryObject<Block> POTION_CAULDRON= registerBlock("potion_cauldron",
            () -> new PotionCauldronBlock(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK)
                    .mapColor(MapColor.STONE).requiresCorrectToolForDrops()
                    .strength(2.0F).noOcclusion()));

    public static final RegistryObject<Block> MINER_LIGHT = registerBlock("miner_light",
            () -> new MinerLightSourceBlock(
                    BlockBehaviour.Properties.copy(Blocks.ACACIA_FENCE).instabreak().noCollission()
                            .lightLevel((p_50755_) -> 14).sound(SoundType.STONE).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> MUTATED_MUSHROOM = registerBlock("wheat_mushroom",
            () -> new MutatedMushroom(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
    }
}
