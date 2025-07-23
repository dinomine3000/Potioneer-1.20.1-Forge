package net.dinomine.potioneer.particle;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.particle.custom.IncenseSmokeParticle;
import net.dinomine.potioneer.particle.custom.IncenseSmokeParticleOptions;
import net.dinomine.potioneer.particle.custom.IncenseSmokeParticleType;
import net.dinomine.potioneer.particle.custom.PotionCauldronParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Potioneer.MOD_ID);

    public static final RegistryObject<SimpleParticleType> POTION_CAULDRON_PARTICLES =
            PARTICLE_TYPES.register("potion_cauldron_particles",
                    () -> new SimpleParticleType(true));

    public static final RegistryObject<ParticleType<IncenseSmokeParticleOptions>> INCENSE_PARTICLES =
            PARTICLE_TYPES.register("incense_smoke", IncenseSmokeParticleType::new);

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }


    @SubscribeEvent
    public static void registerFactories(RegisterParticleProvidersEvent evt) {
        evt.registerSpriteSet(ModParticles.POTION_CAULDRON_PARTICLES.get(), PotionCauldronParticles.Provider::new);
//        evt.registerSpriteSet(ModParticles.INCENSE_PARTICLES.get(),spriteSet -> new IncenseSmokeParticle.IncenseSmokeParticleProvider(spriteSet, 0xFFAA66CC));
        Minecraft.getInstance().particleEngine.register(
                ModParticles.INCENSE_PARTICLES.get(),
                IncenseSmokeParticle.IncenseSmokeParticleProvider::new
        );
        //Minecraft.getInstance().particleEngine.register(POTION_CAULDRON_PARTICLES.get(), PotionCauldronParticles.Provider::new);
    }

}
