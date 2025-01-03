package net.dinomine.potioneer.registry;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;

public class DamageTypesRegistry {

    public static final ResourceKey<DamageType> CHRYON_PIERCE = ResourceKey.create(Registries.DAMAGE_TYPE, new ResourceLocation(Potioneer.MOD_ID, "pierce"));


    public static void bootstrap(BootstapContext<DamageType> context) {
        context.register(CHRYON_PIERCE, new DamageType("pierce", 0.1F));
    }
}
