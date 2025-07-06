package net.dinomine.potioneer.entities;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.DivinationRodEntity;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.VillagerTradingManager;
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

    public static final RegistryObject<EntityType<DivinationRodEntity>> DIVINATION_ROD =
            ENTITY_TYPES.register("divination_rod",
                    () -> EntityType.Builder.of(new EntityType.EntityFactory<DivinationRodEntity>() {
                                @Override
                                public DivinationRodEntity create(EntityType<DivinationRodEntity> entityType, Level level) {
                                    return new DivinationRodEntity(entityType, level);
                                }
                            }, MobCategory.MISC)
                            .sized(1f, 1f)
                            .build(new ResourceLocation(Potioneer.MOD_ID, "divination_rod").toString()));

    public static void register(IEventBus eventBus){
        ENTITY_TYPES.register(eventBus);
    }
}
