package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class PlayerBeyonderManager {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player){
            if(!event.getObject().getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
                event.addCapability(new ResourceLocation(Potioneer.MOD_ID, "properties"), new BeyonderStatsProvider());
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        if(event.isWasDeath()){
            event.getOriginal().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(oldStore -> {
                event.getOriginal().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.side == LogicalSide.SERVER){
            event.player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( stats -> {
                stats.onTick(event.player);
            });
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.syncSequenceData(player);
            });
        }
    }


    @SubscribeEvent
    public static void mine(PlayerEvent.BreakSpeed breakSpeed){
        breakSpeed.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
            stats.getMiningSpeed(breakSpeed);
        });
    }
}
