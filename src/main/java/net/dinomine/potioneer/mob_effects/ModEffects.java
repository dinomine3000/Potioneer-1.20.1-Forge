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

    public static final RegistryObject<MobEffect> AOJ_INFLUENCE = MOB_EFFECTS.register("aoj_influence",
            () -> new AoJInfluenceEffect(MobEffectCategory.HARMFUL, 0x000dbc));

    public static final RegistryObject<MobEffect> MIST_EFFECT = MOB_EFFECTS.register("mist_effect",
            () -> new MistEffect(MobEffectCategory.NEUTRAL, 0x000dFc));

    public static final RegistryObject<MobEffect> BLEED_EFFECT = MOB_EFFECTS.register("bleed_effect",
            () -> new ModMobEffect(MobEffectCategory.NEUTRAL, 0x9e2121));

    public static final RegistryObject<MobEffect> PLAGUE_EFFECT = MOB_EFFECTS.register("plague_effect",
            () -> new ModMobEffect(MobEffectCategory.HARMFUL, 0x03a00c));

    public static final RegistryObject<MobEffect> LIGHT_BUFF = MOB_EFFECTS.register("light_buff",
            () -> new ModMobEffect(MobEffectCategory.BENEFICIAL, 0xff6524));

    public static void register(IEventBus eventBus){
        MOB_EFFECTS.register(eventBus);
    }
}
