package net.dinomine.potioneer.item;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.item.custom.*;
import net.dinomine.potioneer.item.custom.BeyonderPotion.BeyonderPotionItem;
import net.dinomine.potioneer.item.custom.cane.CaneItem;
import net.dinomine.potioneer.item.custom.coin.CoinItem;
import net.minecraft.world.item.*;
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
            () -> new CaneItem(new Item.Properties().stacksTo(1).fireResistant().defaultDurability(256)));


    /*ublic static final RegistryObject<Item> METAL_ROD = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().stacksTo(1).durability(20)));*/

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
