package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;

@Mod.EventBusSubscriber
public class PlayerBeyonderManager {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof LivingEntity){
            if(!event.getObject().getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
                event.addCapability(new ResourceLocation(Potioneer.MOD_ID, "properties"), new BeyonderStatsProvider((LivingEntity) event.getObject()));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerDie(LivingDeathEvent event){
        if(event.getEntity() instanceof Player player && !player.level().getGameRules().getBoolean(GameRules.RULE_KEEPINVENTORY)){
            Inventory inv = player.getInventory();
            int size = inv.getContainerSize();
            for (int i = 0; i < size; i++) {
                if(inv.getItem(i).is(ModItems.MINER_PICKAXE.get())){
                    inv.getItem(i).setCount(0);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerRespawn(PlayerEvent.PlayerRespawnEvent e) {
        if(e.getEntity().level().isClientSide()) return;
        e.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) e.getEntity()),
                    new PlayerAdvanceMessage(cap.getPathwayId(), false));
        });
    }

    @SubscribeEvent
    public static void onPlayerCloned(PlayerEvent.Clone event){
        if(event.getOriginal().level().isClientSide()) return;
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(newStore -> {
            event.getOriginal().reviveCaps();
            event.getOriginal().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(oldStore -> {
                newStore.copyFrom(oldStore, event.getEntity());
            });
            event.getOriginal().invalidateCaps();
        });
    }


    @SubscribeEvent
    public static void onPlayerSleep(PlayerWakeUpEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(EntityBeyonderManager::onPlayerSleep);
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event){
        event.player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( stats -> {
            stats.onTick(event.player, event.side == LogicalSide.SERVER);
        });
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event){
        if(!(event.getEntity() instanceof Player)){
            event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
                 stats.onTick(event.getEntity(), true);
            });
        }
    }


    @SubscribeEvent
    public static void onWorldLoad(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player){
            if(player.level().isClientSide()){
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
                    PacketHandler.INSTANCE.sendToServer(new SequenceSTCSyncRequest());
                });
            } else {

            }
        }
    }

    @SubscribeEvent
    public static void livingTarget(LivingChangeTargetEvent event){
        if(event.getNewTarget() instanceof Player player){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN)){
                    event.setNewTarget(null);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onCraft(PlayerEvent.ItemCraftedEvent event){
        if(event.getEntity().level().isClientSide()) return;
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().onCraft(event);
        });
    }

    @SubscribeEvent
    public static void onAttack(LivingDamageEvent event){
        if(event.getSource().getEntity() != null){
            if(event.getSource().getEntity().level().isClientSide()) return;
            event.getSource().getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getEffectsManager().onAttack(event, cap);
            });
        }
    }

    @SubscribeEvent
    public static void lightningStrike(EntityStruckByLightningEvent event){
    }

    @SubscribeEvent
    public static void mine(PlayerEvent.BreakSpeed breakSpeed){
        breakSpeed.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
            stats.getBeyonderStats().getMiningSpeed(breakSpeed);
        });
    }
}
