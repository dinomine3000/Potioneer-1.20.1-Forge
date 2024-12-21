package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.pathways.Beyonder;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.minecraft.client.telemetry.events.WorldLoadEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerBeyonderManager {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof Player player){
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
        event.player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( stats -> {
            stats.onTick(event.player);
            if(event.side == LogicalSide.SERVER){

                /*PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.player),
                        new PlayerAdvanceMessage(stats.getPathwayId()));*/
            }
        });
    }


    @SubscribeEvent
    public static void onWorldLoad(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player && player.level().isClientSide()){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
                PacketHandler.INSTANCE.sendToServer(new SequenceSTCSyncRequest());
            });
        }
    }

    @SubscribeEvent
    public static void worldLoad(LevelEvent.Load event){
        Beyonder.init();
    }


    @SubscribeEvent
    public static void mine(PlayerEvent.BreakSpeed breakSpeed){
        breakSpeed.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
            stats.getMiningSpeed(breakSpeed);
        });
    }
}
