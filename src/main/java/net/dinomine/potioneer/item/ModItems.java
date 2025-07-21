package net.dinomine.potioneer.item;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.item.custom.BeyonderPotion.BeyonderPotionItem;
import net.dinomine.potioneer.item.custom.*;
import net.dinomine.potioneer.item.custom.DeathKnell.DeathKnellItem;
import net.dinomine.potioneer.item.custom.cane.CaneItem;
import net.dinomine.potioneer.item.custom.coin.CoinItem;
import net.dinomine.potioneer.item.custom.leymanosTravels.LeymanosTravels;
import net.dinomine.potioneer.item.custom.scepter.cane.ScepterItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.Tiers;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Potioneer.MOD_ID);

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PECAN_SHELL = ITEMS.register("pecan_shell",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PECAN_LEAF = ITEMS.register("pecan_leaf",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> SOLSEER = ITEMS.register("solseer",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PARAGON_FUEL = ITEMS.register("paragon_cake",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> WANDERING_CACTUS_PRICK = ITEMS.register("wandering_cactus_prick",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PECAN_SPAWN_EGG = ITEMS.register("pecan_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.PECAN, 0xC68D83, 0x387F65, new Item.Properties()));

    public static final RegistryObject<Item> CHRYON_SPAWN_EGG = ITEMS.register("chryon_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CHRYON, 0xA5f8ff, 0x66AFD4, new Item.Properties()));

    public static final RegistryObject<Item> WANDERING_CACTUS_SPAWN_EGG = ITEMS.register("wandering_cactus_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.WANDERING_CACTUS, 0x43ff77 , 0xe7ffcb , new Item.Properties()));

    public static final RegistryObject<Item> DEMONIC_WOLF_SPAWN_EGG = ITEMS.register("demonic_wolf_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DEMONIC_WOLF, 0x4a0015  , 0xff0047  , new Item.Properties()));

    public static final RegistryObject<Item> MINER_PICKAXE = ITEMS.register("conjured_pickaxe",
            () -> new ConjuredPickaxeItem(new Item.Properties().setNoRepair().durability(10)));

    public static final RegistryObject<Item> GHOST_FISHING_ROD = ITEMS.register("ghostly_fishing_rod",
            () -> new GhastlyFishingRod(new Item.Properties().rarity(Rarity.COMMON)));


    public static final RegistryObject<Item> VIAL = ITEMS.register("vial", () -> new Vial(new Item.Properties()));
    public static final RegistryObject<Item> FLASK = ITEMS.register("flask", () -> new Flask(new Item.Properties()));
    public static final RegistryObject<Item> FORMULA = ITEMS.register("formula", () -> new FormulaItem(new Item.Properties()));

    public static final RegistryObject<Item> BEYONDER_POTION = ITEMS.register("beyonder_potion",
            () -> new BeyonderPotionItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> COIN_ITEM = ITEMS.register("coin",
            () -> new CoinItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> GOLDEN_DROP = ITEMS.register("golden_drop",
            () -> new Item(new Item.Properties().rarity(Rarity.RARE).stacksTo(16).fireResistant()));

    public static final RegistryObject<Item> DIVINATION_ROD = ITEMS.register("cane",
            () -> new CaneItem(new Item.Properties().stacksTo(1).defaultDurability(256)));

    public static final RegistryObject<Item> FIRE_SWORD = ITEMS.register("fire_sword",
            () -> new FireSword(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.UNCOMMON)));

    public static final RegistryObject<Item> VOODOO_DOLL = ITEMS.register("straw_doll",
            () -> new VoodooDollItem(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)));

    public static final RegistryObject<Item> CHARACTERISTIC = ITEMS.register("beyonder_characteristic",
            () -> new CharacteristicItem(new Item.Properties().stacksTo(1).rarity(Rarity.EPIC).fireResistant()));

    public static final RegistryObject<Item> CHRYON_SNOW = ITEMS.register("chryon_flake",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> FROZEN_SWORD = ITEMS.register("frozen_sword",
            () -> new SwordItem(Tiers.DIAMOND, 1, -2.4F, new Item.Properties()));

    public static final RegistryObject<Item> CHRYON_CORE = ITEMS.register("chryon_core",
            () -> new Item(new Item.Properties().stacksTo(1).craftRemainder(CHRYON_SNOW.get())));

    public static final RegistryObject<Item> DEVILS_TAIL = ITEMS.register("devils_tail",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> DEMONIC_FANG = ITEMS.register("demonic_fang",
            () -> new Item(new Item.Properties()));

//    public static final RegistryObject<Item> STAR_FLOWER = ITEMS.register("star_flower",
//            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> POLISHED_DIAMOND = ITEMS.register("polished_diamond",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ROOTS = ITEMS.register("roots",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RING = ITEMS.register("ring",
            () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> ASTEROID_DEBUG = ITEMS.register("asteroid_debug",
            () -> new AsteroidDebugItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> PROBABILITY_DICE = ITEMS.register("probability_dice",
            () -> new Item(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> KALVETUA_SCEPTER = ITEMS.register("sea_god_scepter",
            () -> new ScepterItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC).defaultDurability(512)));

    public static final RegistryObject<Item> DEATH_KNELL = ITEMS.register("death_knell",
            () -> new DeathKnellItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> UNSHADOWED_CRUCIFIX = ITEMS.register("unshadowed_crucifix",
            () -> new UnshadowedCrucifixItem(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> LEYMANOS_TRAVELS = ITEMS.register("leymanos_travels",
            () -> new LeymanosTravels(new Item.Properties().stacksTo(1).fireResistant().rarity(Rarity.EPIC)));

    public static final RegistryObject<Item> RITUAL_DAGGER = ITEMS.register("ritualistic_dagger",
            () -> new SwordItem(Tiers.IRON, 1, 5, new Item.Properties().defaultDurability(64).setNoRepair()));

    public static final RegistryObject<Item> INK_BOTTLE = ITEMS.register("ink_bottle",
            () -> new InkBottleItem(new Item.Properties().defaultDurability(50)));


    /*ublic static final RegistryObject<Item> METAL_ROD = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().stacksTo(1).durability(20)));*/

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
