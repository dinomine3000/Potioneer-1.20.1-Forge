package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvent;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvents;
import net.dinomine.potioneer.event.LuckEventCastEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ActingEvents {
    @SubscribeEvent
    public static void onBlockMined(BlockEvent.BreakEvent event){
         event.getPlayer().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
             cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.MINER_ACTING_INC, 9);
             if(cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_SILK.getEffectId()) || cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_FORTUNE.getEffectId())){
                 cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.APPRAISER_ACTING_MINING, 8);
             }
         });
    }

    @SubscribeEvent
    public static void onLuckEventCast(LuckEventCastEvent.Post event){
        LuckEvent luck = event.getLuckEvent();
        LuckEvent.Magnitude magnitude = LuckEvents.getMagnitudeOfEvent(luck);
        if(LuckEvent.isPositive(magnitude)){
            event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.LUCK_ACTING_EVENT, 6);
            });
        }
    }
}
