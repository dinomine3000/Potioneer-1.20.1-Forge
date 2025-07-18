package net.dinomine.potioneer.compat;

import com.lowdragmc.lowdraglib.jei.JEIPlugin;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.menus.CrafterAnvilMenu;
import net.dinomine.potioneer.menus.CrafterMenu;
import net.dinomine.potioneer.menus.ModMenuTypes;
import net.minecraft.resources.ResourceLocation;

@JeiPlugin
public class JEIPotioneerPlugin implements IModPlugin {

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Potioneer.MOD_ID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(CrafterMenu.class, ModMenuTypes.CRAFTER_MENU.get(), RecipeTypes.CRAFTING, 1, 9, 10, 36);
        registration.addRecipeTransferHandler(CrafterAnvilMenu.class, ModMenuTypes.CRAFTER_ANVIL_MENU.get(), RecipeTypes.ANVIL, 0, 2, 3, 36);
    }
}
