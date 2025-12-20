package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerCharacteristicManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PlayerActingEvents {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        if(event.side.isClient()) return;
        Player player = event.player;
        Level level = event.player.level();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PlayerCharacteristicManager acting = cap.getCharacteristicManager();
            if(player.isSwimming() && player.getDeltaMovement().lengthSqr() > 0.0001f){
                //progress swimmer so it reaches 100% in 10 minutes of swimming
                acting.progressActing(1/(20*60*10d), 19);
            }
            if(level.isRainingAt(player.getOnPos())){
                //progress tempest so it reaches 100% in 60 minutes of rain
                acting.progressActing(1/(20*60*60d), 17);
            }
        });
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(!(event.getEntity() instanceof Player player)) return;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getCharacteristicManager().progressActing(0.001d, 17);
        });
    }

    @SubscribeEvent
    public static void onPlayerCraftEvent(PlayerEvent.ItemCraftedEvent event){
        if(event.getEntity().level().isClientSide()) return;
        Player player = event.getEntity();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PlayerCharacteristicManager acting = cap.getCharacteristicManager();
            acting.progressActing(0.001d, 49);
        });
    }

    @SubscribeEvent
    public static void onPlayerBreakBlockEvent(BlockEvent.BreakEvent event){
        if(event.getPlayer().level().isClientSide()) return;
        Player player = event.getPlayer();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PlayerCharacteristicManager acting = cap.getCharacteristicManager();
            //needs to mine stone or ores, at y < 60, 320 times
            if(player.getBlockY() < 40
                    && (event.getState().is(Tags.Blocks.STONE) || event.getState().is(Tags.Blocks.ORES)))
                acting.progressActing(1/320d, 9);
        });
    }
}
