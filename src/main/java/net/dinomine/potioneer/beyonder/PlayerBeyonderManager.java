package net.dinomine.potioneer.beyonder;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
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
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().onPlayerDie(event, cap);
        });
//        if(event.getEntity() instanceof Player player){
//            Inventory inv = player.getInventory();
//            int size = inv.getContainerSize();
//            for (int i = 0; i < size; i++) {
//                if(inv.getItem(i).is(ModItems.MINER_PICKAXE.get())){
//                    inv.getItem(i).setCount(0);
//                } else {
//                    MysticismHelper.updateOrApplyMysticismTag(inv.getItem(i), 10, player);
//                }
//            }
//        }
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
//            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) event.getEntity()),
//                    new PlayerSyncHotbarMessage(newStore.getAbilitiesManager().clientHotbar, newStore.getAbilitiesManager().quickAbility));
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
            cap.getEffectsManager().onCraft(event, cap);
        });
    }

    //this one still plays the hurt animation even if you cancel the event = takes place when an entity truly is receiving damage
    //deals with the true damage that is applied
    @SubscribeEvent
    public static void onEntityTakeDamage(LivingDamageEvent event){
        if(event.getEntity() != null && event.getEntity() instanceof Player player){
            //if(player.level().isClientSide()) return;
            LivingEntity entity = event.getEntity();
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getEffectsManager().onTakeDamage(event, cap);
            });
        }
    }



    //called before damage is calculated -> deals with raw damage before reduction
    @SubscribeEvent
    public static void onEntityHurt(LivingHurtEvent event) {
        if(event.getSource().getEntity() != null){
            //FOR THE ATTACKER
            //runs this code in the context of an entity attacking another
            if(event.getSource().getEntity().level().isClientSide()) return;
            event.getSource().getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getEffectsManager().onAttackDamageCalculation(event, cap);
            });
        }
    }

    @SubscribeEvent
    public static void onExperienceChange(PlayerXpEvent.XpChange event){
//        System.out.println(event.getAmount());
//        System.out.println(Math.max(event.getAmount(), event.getAmount()/2));
//        System.out.println(event.getEntity());
//        event.setAmount(Math.max(event.getAmount(), event.getAmount()/2));
    }

    @SubscribeEvent
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event){
        if(event.getEntity() instanceof LivingEntity livingEntity){
            livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION)){
                    livingEntity.extinguishFire();
                }
            });
        }
    }

    @SubscribeEvent
    public static void onExperienceChange(PlayerXpEvent.LevelChange event){
        //event.setLevels(Math.max(event.getLevels(), (int) Math.floor(event.getLevels() / 2f)));
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( cap -> {
           if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.PARAGON_XP_REDUCE)){
               BeyonderEffect eff = cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.PARAGON_XP_REDUCE);
               event.setLevels(Math.max(event.getLevels(),
                       (int) Math.floor((2 * event.getLevels()) /(float)(10 - eff.getSequenceLevel()))
                       ));
               cap.requestActiveSpiritualityCost(eff.getCost());
           }
        });
    }


    @SubscribeEvent
    public static void onFall(LivingFallEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE)){
                event.setDamageMultiplier(0);
                cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE,
                        cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE).getSequenceLevel(),
                        cap, event.getEntity());
            }
            else if(event.getDistance() > 5 && cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.MYSTERY_FALL)){
                float cost = cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.MYSTERY_FALL).getCost();
                if(cap.getSpirituality() > cost){
                    event.setDamageMultiplier(
                            (cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.MYSTERY_FALL).getSequenceLevel())/16f
                    );
                    cap.requestActiveSpiritualityCost(cost);
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

            if(fortune && event.getState().is(Tags.Blocks.ORES)){
                int lvl = (10 - cap.getEffectsManager().getEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE).getSequenceLevel())/2;
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
