package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.DemonicWolfEntity;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.dinomine.potioneer.entities.custom.WanderingCactusEntity;
import net.dinomine.potioneer.worldgen.ModConfiguredFeatures;
import net.dinomine.potioneer.worldgen.ModPlacedFeatures;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus {

    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event){
        event.enqueueWork(() -> {
            SpawnPlacements.register(ModEntities.CHRYON.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, ChryonEntity::canSpawn);
            SpawnPlacements.register(ModEntities.PECAN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, PecanEntity::canSpawn);
            SpawnPlacements.register(ModEntities.WANDERING_CACTUS.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, WanderingCactusEntity::canSpawn);
            //SpawnPlacements.register(ModEntities.DEMONIC_WOLF.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, DemonicWolfEntity::canSpawn);
        });
    }

    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event){
        event.put(ModEntities.CHRYON.get(), ChryonEntity.setAttributes());
        event.put(ModEntities.WANDERING_CACTUS.get(), WanderingCactusEntity.setAttributes());
        event.put(ModEntities.PECAN.get(), PecanEntity.setAttributes());
        event.put(ModEntities.DEMONIC_WOLF.get(), DemonicWolfEntity.setAttributes());
    }
}
