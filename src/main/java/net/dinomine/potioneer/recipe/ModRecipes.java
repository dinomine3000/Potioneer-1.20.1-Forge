package net.dinomine.potioneer.recipe;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Potioneer.MOD_ID);

    public static final RegistryObject<RecipeSerializer<PotionCauldronRecipe>> POTION_CAULDRON_SERIALIZER =
            SERIALIZERS.register("potion_cauldron_brew", () -> PotionCauldronRecipe.Serializer.INSTANCE);

    public static final RegistryObject<RecipeSerializer<VialFlaskRecipe>> VIAL_FLASK_SERIALIZER =
            SERIALIZERS.register("vial_flask_craft", () -> VialFlaskRecipe.Serializer.INSTANCE);

    public static void register(IEventBus eventBus){
        SERIALIZERS.register(eventBus);
    }
}
