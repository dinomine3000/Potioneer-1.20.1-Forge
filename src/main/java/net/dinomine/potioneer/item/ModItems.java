package net.dinomine.potioneer.item;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.item.custom.MetalDetectorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Potioneer.MOD_ID);

    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> RAW_SAPPHIRE = ITEMS.register("raw_sapphire",
            () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> METAL_ROD = ITEMS.register("metal_detector",
            () -> new MetalDetectorItem(new Item.Properties().stacksTo(1).durability(20)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
