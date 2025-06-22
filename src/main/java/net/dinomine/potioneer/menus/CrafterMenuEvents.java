package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.entities.custom.PecanEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@Mod.EventBusSubscriber(modid = Potioneer.MOD_ID)
public class CrafterMenuEvents {

    @SubscribeEvent
    public static void onCrafterCraft(PlayerEvent.ItemCraftedEvent event){
        if(event.getEntity() instanceof ServerPlayer player && !player.level().isClientSide()){
            if(event.getEntity().containerMenu instanceof CrafterMenu menu){
                menu.consumeFuelIfAvailable(player, event.getCrafting());
            }
        }
    }
}
