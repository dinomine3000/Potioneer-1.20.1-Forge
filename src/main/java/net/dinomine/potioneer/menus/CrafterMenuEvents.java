package net.dinomine.potioneer.menus;

import net.dinomine.potioneer.Potioneer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

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
