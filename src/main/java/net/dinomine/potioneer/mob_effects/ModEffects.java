package net.dinomine.potioneer.mob_effects;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS
            = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Potioneer.MOD_ID);

    public static final RegistryObject<MobEffect> WATER_PRISON = MOB_EFFECTS.register("water_prison",
            () -> new WaterPrisonEffect(MobEffectCategory.HARMFUL, 0x000dbc));

    public static final RegistryObject<MobEffect> BLEED_EFFECT = MOB_EFFECTS.register("bleed_effect",
            () -> new BleedEffect(MobEffectCategory.HARMFUL, 0x9e2121));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
