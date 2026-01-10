package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModAttributes {
    public static final DeferredRegister<Attribute> REGISTRY = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, Potioneer.MOD_ID);

    RegistryObject<Attribute> LUCK_CHANCE_ATT = REGISTRY.register("luck_chance", () -> new RangedAttribute("beyonder.potioneer.luck_chance", 1, 0, Integer.MAX_VALUE));
}
