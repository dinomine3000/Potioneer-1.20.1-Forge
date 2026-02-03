package net.dinomine.potioneer.entities;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Potioneer.MOD_ID);

    public static final RegistryObject<EntityType<ChryonEntity>> CHRYON =
            ENTITY_TYPES.register("chryon",
                    () -> EntityType.Builder.of(ChryonEntity::new, MobCategory.MONSTER)
                            .sized(1.5f, 1.75f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "chryon").toString()));


    public static final RegistryObject<EntityType<PecanEntity>> PECAN =
            ENTITY_TYPES.register("pecan",
                    () -> EntityType.Builder.of(PecanEntity::new, MobCategory.MONSTER)
                            .sized(1f, 1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "pecan").toString()));

    public static final RegistryObject<EntityType<WanderingCactusEntity>> WANDERING_CACTUS =
            ENTITY_TYPES.register("wandering_cactus",
                    () -> EntityType.Builder.of(WanderingCactusEntity::new, MobCategory.CREATURE)
                            .sized(1f, 2f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "wandering_cactus").toString()));

    public static final RegistryObject<EntityType<DemonicWolfEntity>> DEMONIC_WOLF =
            ENTITY_TYPES.register("demonic_wolf",
                    () -> EntityType.Builder.of(DemonicWolfEntity::new, MobCategory.MONSTER)
                            .sized(1f, 2f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "demonic_wolf").toString()));


    public static final RegistryObject<EntityType<CharacteristicEntity>> CHARACTERISTIC =
            ENTITY_TYPES.register("beyonder_characteristic",
                    () -> EntityType.Builder.of(new EntityType.EntityFactory<CharacteristicEntity>() {
                                @Override
                                public CharacteristicEntity create(EntityType<CharacteristicEntity> entityType, Level level) {
                                    return new CharacteristicEntity(entityType, level);
                                }
                            }, MobCategory.MISC)
                            .sized(0.3f, 0.2f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "beyonder_characteristic").toString()));

    public static final RegistryObject<EntityType<DivinationRodEntity>> DIVINATION_ROD =
            ENTITY_TYPES.register("divination_rod",
                    () -> EntityType.Builder.of((EntityType.EntityFactory<DivinationRodEntity>)
                                    (entityType, level) -> new DivinationRodEntity(entityType, level), MobCategory.MISC)
                            .sized(0.2f, 1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "divination_rod").toString()));

    public static final RegistryObject<EntityType<AsteroidEntity>> ASTEROID =
            ENTITY_TYPES.register("asteroid",
                    () -> EntityType.Builder.of((EntityType.EntityFactory<AsteroidEntity>) (entityType, level) ->
                                    new AsteroidEntity(entityType, level), MobCategory.MISC)
                            .sized(1f, 1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "asteroid").toString()));

    public static final RegistryObject<EntityType<SeaGodScepterEntity>> SEA_GOD_SCEPTER =
            ENTITY_TYPES.register("sea_god_scepter",
                    () -> EntityType.Builder.of(new EntityType.EntityFactory<SeaGodScepterEntity>() {
                                @Override
                                public SeaGodScepterEntity create(EntityType<SeaGodScepterEntity> entityType, Level level) {
                                    return new SeaGodScepterEntity(entityType, level);
                                }
                            }, MobCategory.MISC)
                            .sized(0.2f, 1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "sea_god_scepter").toString()));

    public static final RegistryObject<EntityType<CharmEntity>> CHARM_ENTITY =
            ENTITY_TYPES.register("charm_entity",
                    () -> EntityType.Builder.of(CharmEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "charm_entity").toString()));

    public static final RegistryObject<EntityType<DiceEffectEntity>> DICE_EFFECT_ENTITY =
            ENTITY_TYPES.register("dice_effect_entity",
                    () -> EntityType.Builder.of(DiceEffectEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "dice_effect_entity").toString()));

    public static final RegistryObject<EntityType<SlotMachineEntity>> SLOT_MACHINE_ENTITY =
            ENTITY_TYPES.register("slot_machine_entity",
                    () -> EntityType.Builder.of(SlotMachineEntity::new, MobCategory.MISC)
                            .sized(0.4f, 0.1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "slot_machine_entity").toString()));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
