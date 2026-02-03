package net.dinomine.potioneer.sound;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Potioneer.MOD_ID);

    public static final RegistryObject<SoundEvent> ADVANCEMENT_CLICK = registerSoundEvents("click");
    public static final RegistryObject<SoundEvent> COIN = registerSoundEvents("coin");
    public static final RegistryObject<SoundEvent> WATER_PRISON = registerSoundEvents("water_prison");
    public static final RegistryObject<SoundEvent> GUN_SHOOT = registerSoundEvents("gun_shoot");
    public static final RegistryObject<SoundEvent> GUN_RELOAD = registerSoundEvents("gun_reload");
    public static final RegistryObject<SoundEvent> GUN_CLOTH = registerSoundEvents("gun_cloth");
    public static final RegistryObject<SoundEvent> UNLUCK = registerSoundEvents("unluck_effect");
    public static final RegistryObject<SoundEvent> LUCK = registerSoundEvents("luck_effect");
    public static final RegistryObject<SoundEvent> WHOOOOSH = registerSoundEvents("whoooosh");
    public static final RegistryObject<SoundEvent> ARROW_MISS = registerSoundEvents("arrow_miss");
    public static final RegistryObject<SoundEvent> CRIT = registerSoundEvents("crit");



    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(Potioneer.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
