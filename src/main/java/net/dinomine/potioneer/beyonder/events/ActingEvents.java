package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.TyrantPathway;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvent;
import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvents;
import net.dinomine.potioneer.event.LuckEventCastEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

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
    public static void onEntityTakeDamage(LivingDamageEvent event){
        Entity ent = event.getSource().getEntity();
        if(ent instanceof LivingEntity livingEntity){
            if(AreaOfJurisdictionAbility.isEntityInAOJ(livingEntity, livingEntity))
                CapProvider.beyonder(livingEntity).ifPresent(cap -> cap.getCharacteristicManager().progressActing(TyrantPathway.ENFORCER_ACTING_DAMAGE, 17));
        }
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

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event){
        Player player = event.player;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(player);
        if(optCap.isEmpty()) return;
        BeyonderCapability cap = optCap.get();
        boolean isScaley = cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_SCALES);
        if(player.isInWater() && player.isSwimming()) cap.getCharacteristicManager().progressActing((isScaley ? 2 : 1) * TyrantPathway.SWIMMER_ACTING, 19);
    }
}
