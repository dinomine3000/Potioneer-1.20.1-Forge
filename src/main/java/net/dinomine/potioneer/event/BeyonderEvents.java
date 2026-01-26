package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderZeroDamageEffect;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerNameSyncMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.util.misc.DivinationResult;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerXpEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

@Mod.EventBusSubscriber
public class BeyonderEvents {

    @SubscribeEvent
    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event){
        if(event.getObject() instanceof LivingEntity){
            if(!event.getObject().getCapability(BeyonderStatsProvider.BEYONDER_STATS).isPresent()){
                event.addCapability(new ResourceLocation(Potioneer.MOD_ID, "properties"), new BeyonderStatsProvider((LivingEntity) event.getObject()));
            }
        }
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event){
        if(!event.getEntity().level().isClientSide()){
            if(MysticalItemHelper.isWorkingArtifact(event.getItemStack())){
                event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                    cap.getAbilitiesManager().castArtifactAbility(MysticalItemHelper.getArtifactIdFromItem(event.getItemStack()), cap, event.getEntity());
                });
            }
        }
    }

    @SubscribeEvent
    public static void abilityCastPre(AbilityCastEvent.Pre event){
    }

    @SubscribeEvent
    public static void abilityCastPost(AbilityCastEvent.Post event){
    }

    @SubscribeEvent
    public static void onItemHurt(DurabilityHurtEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(ItemStack.matches(event.getEntity().getMainHandItem(), event.getStack()) && cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_ZERO_DAMAGE.getEffectId())){
                if(cap.getLuckManager().passesLuckCheck(0.35f, 0, 0, event.getEntity().getRandom())){
                    ((BeyonderZeroDamageEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_ZERO_DAMAGE.getEffectId())).playSound(event.getEntity());
                    event.setCanceled(true);
                }
            }
        });
    }

    @SubscribeEvent
    public static void itemDroppedEvent(ItemTossEvent event){
        if(MysticalItemHelper.isCharacteristic(event.getEntity().getItem())){
            event.getEntity().setInvulnerable(true);
            event.getEntity().setUnlimitedLifetime();
        }
    }

    @SubscribeEvent
    public static void onPlayerDie(LivingDeathEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.onPlayerDie(event);
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
    public static void onPlayerChat(ServerChatEvent event){
        if(event.getPlayer() != null && !event.getPlayer().level().isClientSide()){
            List<String> matchedTrueNames = new ArrayList<>();
            for(BeyonderPathway pathway: Pathways.getAllPathways()){
                Deity deity = pathway.getDefaultDeity();
                if(deity == null) continue;
                String raw = event.getRawText();
                if(raw.toLowerCase().contains(deity.getTrueName().toLowerCase())){
                    event.getPlayer().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                        deity.onTrueNameSpoken(event.getPlayer(), cap);
                    });
                    matchedTrueNames.add(deity.getTrueName());
                } else if(deity.matchPrayer(event.getRawText())){
                    event.getPlayer().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                        if(cap.changeReputation(pathway.getId(), 1, event.getPlayer().level())){
                           cap.putPrayerCooldown(event.getPlayer().level());
                           event.getPlayer().sendSystemMessage(Component.translatableWithFallback("reputation.potioneer.prayer", "You feel a presence acknowledge your prayer..."));
                        }
                    });
                }
            }
            event.setMessage(obfuscateTrueNames(event.getMessage(), matchedTrueNames));
        }
    }

    private static Component obfuscateTrueNames(Component originalMessage, List<String> matchedNames) {
        // if no names matched, return the original component
        if (matchedNames == null || matchedNames.isEmpty()) {
            return originalMessage;
        }

        String raw = originalMessage.getString(); // get plain text
        String lowerRaw = raw.toLowerCase();

        MutableComponent newMessage = Component.empty();
        int index = 0;

        while (index < raw.length()) {
            int closestMatchIndex = -1;
            String matchedTrueName = null;

            // find the next nearest matched name
            for (String trueName : matchedNames) {
                int matchIndex = lowerRaw.indexOf(trueName.toLowerCase(), index);

                if (matchIndex != -1 &&
                        (closestMatchIndex == -1 || matchIndex < closestMatchIndex)) {
                    closestMatchIndex = matchIndex;
                    matchedTrueName = trueName;
                }
            }

            // no more matches
            if (closestMatchIndex == -1) {
                newMessage.append(Component.literal(raw.substring(index)));
                break;
            }

            // append text before the match
            newMessage.append(Component.literal(raw.substring(index, closestMatchIndex)));

            // append obfuscated match
            newMessage.append(
                    Component.literal(
                            raw.substring(closestMatchIndex, closestMatchIndex + matchedTrueName.length())
                    ).withStyle(ChatFormatting.OBFUSCATED)
            );

            index = closestMatchIndex + matchedTrueName.length();
        }

        return newMessage;
    }

    @SubscribeEvent
    public static void onPlayerSleep(PlayerWakeUpEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(event.getEntity().level().isClientSide()) return;
            if(!event.updateLevel()) cap.onPlayerSleep();

            DivinationResult result = null;
            long id = event.getEntity().getUUID().getLeastSignificantBits()&0xFF + 100*(Math.floorDiv(event.getEntity().level().getDayTime(), 24000L));
            long seed = stringToLong(String.valueOf(id));
            RandomSource random = RandomSource.create(seed);
            float chance = 0.1f;
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.MISC_DIVINATION.getEffectId())){
                chance += (10 - cap.getEffectsManager().getEffect(BeyonderEffects.MISC_DIVINATION.getEffectId()).getSequenceLevel())/10f;
            }
            if(random.nextFloat() <= chance){
                ItemStack stack = ItemStack.EMPTY;
                for(ItemStack item: event.getEntity().getInventory().items){
                    if(item.is(ModItems.FORMULA.get())){
                        stack = item.copy();
                        break;
                    }
                    if(item.is(ModItems.BEYONDER_POTION.get())){
                        stack = item.copy();
                        break;
                    }
                    if(item.is(ModItems.CHARACTERISTIC.get())){
                        stack = item.copy();
                        break;
                    }
                }
                result = MysticismHelper.doDivination(stack, event.getEntity(), cap.getPathwaySequenceId(), random);
            }
            if(result == null || result.clue().isEmpty()) return;
            event.getEntity().sendSystemMessage(Component.translatable("message.potioneer.dream_clue", Component.translatable(result.clue())));
        });

    }

    private static long stringToLong(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes());

            ByteBuffer buffer = ByteBuffer.wrap(hash);
            return buffer.getLong();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 not available", e);
        }
    }

    @SubscribeEvent
    public static void playerFinishInteraction(LivingEntityUseItemEvent.Finish event){
        if(event.getItem().isEdible()){
            event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.onFoodEat(event.getItem(), event.getEntity());
            });
        }
    }

    @SubscribeEvent
    public static void onEntityTick(LivingEvent.LivingTickEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.onTick(event.getEntity(), !event.getEntity().level().isClientSide());
        });
    }

    @SubscribeEvent
    public static void onWorldLoad(EntityJoinLevelEvent event){
        if(event.getEntity() instanceof Player player){
            if(player.level().isClientSide()){
                player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
                    PacketHandler.sendMessageCTS(new SequenceSTCSyncRequest());
                });
            } else {
                Map<UUID, GameProfileCache.GameProfileInfo> profileMap = player.level().getServer().getProfileCache().profilesByUUID;
                Map<UUID, String> nameMap = new HashMap<>();
                for(UUID id: profileMap.keySet()){
                    nameMap.put(id, profileMap.get(id).getProfile().getName());
                }
                for(Player player1: event.getLevel().players()){
                    if(player1.is(player)) continue;
                    PacketHandler.sendMessageSTC(new PlayerNameSyncMessage(nameMap), player1);
                }
            }
        }
    }

    @SubscribeEvent
    public static void livingTarget(LivingChangeTargetEvent event){
        if(event.getNewTarget() instanceof Player player){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_INVISIBLE.getEffectId())){
                    event.setCanceled(true);
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
    public static void onEntityStruckByLightning(EntityStruckByLightningEvent event){
        //cancel this to negate the damage
        if(event.getEntity() instanceof LivingEntity livingEntity){
            livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_ELECTRIFICATION.getEffectId())){
                    livingEntity.extinguishFire();
                    cap.requestActiveSpiritualityCost(50);
                }
            });
        }
    }

    @SubscribeEvent
    public static void onExperienceChange(PlayerXpEvent.LevelChange event){
        //event.setLevels(Math.max(event.getLevels(), (int) Math.floor(event.getLevels() / 2f)));
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( cap -> {
           if(cap.getEffectsManager().hasEffect(BeyonderEffects.PARAGON_XP.getEffectId())){
               BeyonderEffect eff = cap.getEffectsManager().getEffect(BeyonderEffects.PARAGON_XP.getEffectId());
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
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_FALL_NEGATE.getEffectId())){
                event.setDamageMultiplier(0);
//                cap.getEffectsManager().removeEffect(BeyonderEffects.MYSTERY_FALL_NEGATE.getEffectId());
            }
        });
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event){
        if(event.getPlayer().isCreative() || event.getPlayer().isSpectator()) return;
        if(event.getPlayer().level().isClientSide()) return;
        event.getPlayer().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            int i = 1;
            ItemStack pick = event.getPlayer().getMainHandItem().copy();
            if(pick.isEmpty()) pick = new ItemStack(Items.COMPASS);
            boolean fortune = cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_FORTUNE.getEffectId());
            boolean silk = cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_SILK.getEffectId());

            if(fortune && event.getState().is(Tags.Blocks.ORES)){
                float lvl = 1 + (10 - cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_FORTUNE.getEffectId()).getSequenceLevel())/2f;
                while(lvl >= 1){
                    lvl--;
                    i++;
                }
                if(event.getLevel().getRandom().nextFloat() < lvl){
                    i++;
                }
            }
            if (silk) {
                pick.enchant(Enchantments.SILK_TOUCH, 1);
            }

            if (fortune || silk) {
                cap.getCharacteristicManager().progressActing(1/280d, 8);
//                System.out.println("Applied at least one of the effects");
                event.setCanceled(true);
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
    public static void mine(PlayerEvent.BreakSpeed event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(stats -> {
            stats.getBeyonderStats().getMiningSpeed(event);
        });
    }

}
