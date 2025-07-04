package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENU_TYPES =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, Potioneer.MOD_ID);

    public static final RegistryObject<MenuType<CrafterMenu>> CRAFTER_MENU = MENU_TYPES.register("crafter_menu",
            () -> IForgeMenuType.create(CrafterMenu::new));
    public static final RegistryObject<MenuType<CrafterAnvilMenu>> CRAFTER_ANVIL_MENU = MENU_TYPES.register("crafter_anvil_menu",
            () -> IForgeMenuType.create(CrafterAnvilMenu::new));

}
