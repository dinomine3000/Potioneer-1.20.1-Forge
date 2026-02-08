package net.dinomine.potioneer.block;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.block.custom.*;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
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


    public static final RegistryObject<Block> SOLSEER_TORCH =
            registerOnlyBlock("solseer_torch",
                    () -> new TorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH), ParticleTypes.SMALL_FLAME));

    public static final RegistryObject<Block> SOLSEER_WALL_TORCH =
            registerOnlyBlock("solseer_wall_torch",
                    () -> new WallTorchBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH), ParticleTypes.SMALL_FLAME));

    public static final RegistryObject<Block> FAKE_WATER =
            registerOnlyBlock("fake_water",
                    () -> new FakeWaterBlock(BlockBehaviour.Properties.of()));

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

    public static final RegistryObject<Block> MINER_LIGHT = BLOCKS.register("miner_light",
            () -> new MinerLightSourceBlock(
                    BlockBehaviour.Properties.of().instabreak().noCollission()
                            .lightLevel((p_50755_) -> 14).sound(SoundType.STONE).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> WATER_TRAP_BLOCK = BLOCKS.register("water_trap",
            () -> new WaterTrapBlock(
                    BlockBehaviour.Properties.of().noCollission().friction(-1).destroyTime(10)
                            .sound(SoundType.LILY_PAD).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> PRIEST_LIGHT = BLOCKS.register("priest_light",
            () -> new PriestLightSourceBlock(
                    BlockBehaviour.Properties.of().instabreak().noCollission()
                            .lightLevel((p_50755_) -> 14).sound(SoundType.GLASS).pushReaction(PushReaction.DESTROY)));

    public static final RegistryObject<Block> MUTATED_MUSHROOM = registerBlock("wheat_mushroom",
            () -> new MutatedMushroom(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    public static final RegistryObject<Block> STAR_FLOWER_BLOCK = registerBlock("star_flower_block",
            () -> new StarFlowerBlock(() -> MobEffects.NIGHT_VISION, 5, BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM)));

    public static final RegistryObject<Block> RITUAL_ALTAR = registerBlock("ritual_altar",
            () -> new RitualAltarBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    .mapColor(MapColor.COLOR_RED)
                    .strength(2.0F).noOcclusion()));

    public static final RegistryObject<Block> RITUAL_INK = registerBlock("ritual_ink",
            () -> new RitualInk(BlockBehaviour.Properties.copy(Blocks.REDSTONE_WIRE)
                    .mapColor(MapColor.COLOR_BLACK)
                    .instabreak().noOcclusion()));

    public static final RegistryObject<Block> RITUAL_PEDESTAL = registerBlock("ritual_pedestal",
            () -> new RitualPedestal(BlockBehaviour.Properties.copy(Blocks.DIORITE)
                    .mapColor(MapColor.QUARTZ)));

    public static final RegistryObject<Block> SCRIPTURE_STAND = registerBlock("scripture_stand",
            () -> new ScriptureStandBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS)
                    .mapColor(MapColor.WOOD).noOcclusion()));

        public static final RegistryObject<Block> SPIRIT_FRUIT_CROP = BLOCKS.register("spirit_fruit",
            () -> new SpiritFruitCropBlock(BlockBehaviour.Properties.copy(Blocks.WHEAT).noOcclusion().noCollission()));

    private static <T extends Block> RegistryObject<T> registerOnlyBlock(String name, Supplier<T> block){
        return BLOCKS.register(name, block);
    }


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
