package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerSyncHotbarMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.navigation.WaterBoundPathNavigation;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.animal.frog.Frog;
import net.minecraft.world.entity.animal.frog.FrogAi;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.LoomMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConduitBlock;
import net.minecraft.world.level.block.FrogspawnBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.pathfinder.WalkNodeEvaluator;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber
public class PlayerBeyonderManager {

    private static final Logger log = LoggerFactory.getLogger(PlayerBeyonderManager.class);

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
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
                    new PlayerSyncHotbarMessage(newStore.getAbilitiesManager().clientHotbar));
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
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_INVIS)){
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

    //this one still plays the hurt animation even if you cancel the event = takes place when an entity truly is receiving damage
    @SubscribeEvent
    public static void onAttack(LivingDamageEvent event){
        if(event.getSource().getEntity() != null){
            if(event.getSource().getEntity().level().isClientSide()) return;
            event.getSource().getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getEffectsManager().onAttack(event, cap);
            });
        }
    }

//    @SubscribeEvent
//    public static void onEntityHurt(LivingHurtEvent event) {
//        if (event.getSource().is(DamageTypes.FALL)) {
//            event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
//                if (cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FALL)) {
//                    event.setCanceled(true);
//                }
//            });
//        }
//    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE)){
                event.setDamageMultiplier(0);
            }
            else if(event.getDistance() > 5 && cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FALL)){
                if(cap.getSpirituality() > 50){
                    event.setDamageMultiplier(
                            (cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.MYSTERY_FALL).getSequenceLevel())/16f
                    );
                    cap.requestActiveSpiritualityCost(50);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event){
        if(event.getPlayer().isCreative() || event.getPlayer().isSpectator()) return;
        event.getPlayer().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            int i = 1;
            ItemStack pick = event.getPlayer().getMainHandItem().copy();
            boolean fortune = cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE);
            boolean silk = cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_SILK_TOUCH);

            if(fortune){
                int lvl = (9 - cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE).getSequenceLevel())/2;
                while(lvl > 1){
                    lvl--;
                    i++;
                }
                if(lvl < event.getLevel().getRandom().nextFloat()){
                    i++;
                }
            }
            if (silk) {
                CompoundTag enchTag = new CompoundTag();
                ListTag list = new ListTag();
                CompoundTag silkTouch = new CompoundTag();
                silkTouch.putString("id", "minecraft:silk_touch");
                silkTouch.putShort("lvl", (short) 1);
                list.add(silkTouch);
                enchTag.put("Enchantments", list);
                pick.setTag(enchTag);
            }

            if (fortune || silk) {
                System.out.println("Applied at least one of the effects");
                event.getLevel().removeBlock(event.getPos(), false);
//                event.getLevel().destroyBlock(event.getPos(), false, event.getPlayer());
                while (i-- > 0) {
                    event.getState().getBlock().playerDestroy(event.getPlayer().level(), event.getPlayer(), event.getPos(),
                            event.getState(), event.getLevel().getBlockEntity(event.getPos()), pick);
                }
            }
        });
    }

    @SubscribeEvent
    public static void onLightningStrike(EntityStruckByLightningEvent event){
        System.out.println("lightning struck");
        System.out.println(event.getEntity());
    }


    @SubscribeEvent
    public static void mine(PlayerEvent.BreakSpeed breakSpeed){
        breakSpeed.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
            stats.getBeyonderStats().getMiningSpeed(breakSpeed);
        });
    }
}
