package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBus {
    @SubscribeEvent
    public static void entityAttributeEvent(EntityAttributeCreationEvent event){
        event.put(ModEntities.CHRYON.get(), ChryonEntity.setAttributes());
        event.put(ModEntities.PECAN.get(), PecanEntity.setAttributes());
    }
}
