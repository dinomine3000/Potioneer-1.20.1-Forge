package net.dinomine.potioneer.event;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderArrowGravitateEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderZeroDamageBlockEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderZeroDamageEffect;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerNameSyncMessage;
import net.dinomine.potioneer.network.messages.SequenceSTCSyncRequest;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.dinomine.potioneer.util.ModTags;
import net.dinomine.potioneer.util.ParticleMaker;
import net.dinomine.potioneer.util.misc.DivinationResult;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.GameProfileCache;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
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
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
            BeyonderEffect zeroEffect = cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_ZERO_DAMAGE.getEffectId());
            if(zeroEffect != null){
                int level = zeroEffect.getSequenceLevel();
                if(level < 7 || ItemStack.matches(event.getEntity().getMainHandItem(), event.getStack())){
                    if(level < 8 || cap.getLuckManager().passesLuckCheck(0.35f, 0, 0, event.getEntity().getRandom())){
                        ((BeyonderZeroDamageEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_ZERO_DAMAGE.getEffectId())).playSound(event.getEntity());
                        event.setCanceled(true);
                    }
                }
            }
        });
    }

    @SubscribeEvent
    public static void livingBreathEvent(LivingBreatheEvent event){
        LivingEntity entity = event.getEntity();
        entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_DROWNING.getEffectId())){
                int sequenceLevel = cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_DROWNING.getEffectId()).getSequenceLevel();
                int multiplier = 1 + (int)((9-sequenceLevel)/2f);
                event.setCanBreathe(false);
                event.setConsumeAirAmount(event.getConsumeAirAmount()*multiplier);
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
            if(cap.getAbilitiesManager().hasAbility(Abilities.TYRANT_DIVINATION.getAblId())){
                chance += (10 - cap.getAbilitiesManager().getSequenceLevelOfAbility(Abilities.TYRANT_DIVINATION.getAblId()))/10f;
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

    @SubscribeEvent
    public static void livingVisibilityEvent(LivingEvent.LivingVisibilityEvent event){
        if(event.getEntity() instanceof Zombie) event.modifyVisibility(0.2);
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
                if(event.getEntity().getLastAttacker() == null || !event.getEntity().getLastAttacker().is(player)){
                    if(cap.getAbilitiesManager().hasAbility(Abilities.OCEAN_ORDER.getAblId()) && event.getEntity().getType().is(ModTags.Entities.OCEAN_ORDER_MOBS)){
                        event.setCanceled(true);
                        return;
                    }
                    if(cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_AURA.getEffectId()) && event.getEntity().getMaxHealth() < player.getHealth() && AreaOfJurisdictionAbility.isTargetUnderInfluenceOfEnforcer(event.getEntity(), player)){
                        event.setCanceled(true);
                        return;
                    }
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


    @SubscribeEvent
    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event){
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.onRespawn();
        });
    }

    //called to confirm the hit.
    //of the 3, its the first to be called, and the one that can be used to negate damage
    @SubscribeEvent
    public static void onDamageProposed(LivingAttackEvent event){
        if(event.getSource().getEntity() != null){
            //FOR THE ATTACKER
            //runs this code in the context of an entity attacking another
            if(event.getSource().getEntity().level().isClientSide()) return;
            event.getSource().getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getEffectsManager().onAttackProposal(event, cap);
            });
        }
    }

    //called inbetween the other two, its used to calculate the damage dealt
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


    //the last one to be called, once the hit has been confirmed and adjusted for. damage should not change in this event.
    @SubscribeEvent
    public static void onEntityTakeDamage(LivingDamageEvent event){
        if(event.getEntity() != null){
            //if(player.level().isClientSide()) return;
            LivingEntity entity = event.getEntity();
            entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                if(event.getSource().is(PotioneerDamage.Tags.MENTAL)){
                    cap.changeSanity(-event.getAmount()/2f);
                }
                cap.getEffectsManager().onTakeDamage(event, cap);
            });
        }
    }

    @SubscribeEvent
    public static void onLuckEventCastConfirmed(LuckEventCastEvent.Post event){
        LivingEntity target = event.getEntity();
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getAbilitiesManager().hasAbility(Abilities.FATE.getAblId())){
                target.sendSystemMessage(event.getLuckEvent().getForecast());
            }
        });
    }

    @SubscribeEvent
    public static void onArrowLoose(ArrowLooseEvent event){
        if(event.getEntity().level().isClientSide) return;
        Player player = event.getEntity();
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(event.getCharge() < 5) return;
            Optional<LivingEntity> optional = AbilityFunctionHelper.getTargetEntity(player, 32, 3, false);
            if(optional.isEmpty()) return;
            LivingEntity target = optional.get();
            if(target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().isPresent()){
                LivingEntityBeyonderCapability tarCap = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
                if(tarCap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_LUCK_EFFECT.getEffectId())) return;
            }
            BeyonderArrowGravitateEffect eff = (BeyonderArrowGravitateEffect) BeyonderEffects.WHEEL_ARROW.createInstance(9, 0, 10, true);
            eff.setValues(target, BowItem.getPowerForTime(event.getCharge()) * 3);
            cap.getEffectsManager().addEffectNoCheck(eff, cap, player);
        });
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
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        if (event.getEntity().level() != null && event.getEntity().level().isClientSide()) return;
        if (!(event.getEntity() instanceof ServerPlayer player)) return;
        if(player.isCreative() || player.isSpectator()) return;

        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent( cap -> {
            BeyonderZeroDamageEffect zeroEff = (BeyonderZeroDamageEffect) cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_ZERO_DAMAGE.getEffectId());
            if(zeroEff == null || zeroEff.getSequenceLevel() > 6 || !zeroEff.doBlocks()) return;
            int level = zeroEff.getSequenceLevel();
            //TODO make the chance dependent on level maybe?
            if(!cap.getLuckManager().passesLuckCheck(0.00005f, 50, 0, player.getRandom())) return;
            ItemStack blockStack = new ItemStack(event.getPlacedBlock().getBlock());
            BeyonderZeroDamageBlockEffect eff = (BeyonderZeroDamageBlockEffect) BeyonderEffects.WHEEL_ZERO_BLOCK.createInstance(9, 0, 1, true);
            eff.withItem(blockStack.copyWithCount(1));
            cap.getEffectsManager().addEffectNoCheck(eff, cap, player);
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
        Player player = event.getPlayer();
        if(player.isCreative() || player.isSpectator()) return;
        if(player.level().isClientSide()) return;
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            int i = 1;
            ItemStack pick = player.getMainHandItem().copy();
            if(pick.isEmpty()) pick = new ItemStack(Items.COMPASS);
            BeyonderEffect fortuneEff = cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_FORTUNE.getEffectId());
            boolean fortune = fortuneEff != null;
            boolean silk = cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_SILK.getEffectId());

            if(fortune && event.getState().is(Tags.Blocks.ORES)){
                float lvl = 1 + (10 - fortuneEff.getSequenceLevel())/2f;
                if(fortuneEff.getSequenceLevel() < 7){
                    lvl += cap.getLuckManager().getRandomNumber(0, 3, true, player.getRandom());
                }
                while(lvl >= 1){
                    lvl--;
                    i++;
                }
                if(cap.getLuckManager().passesLuckCheck(lvl, 0, 0, player.getRandom())){
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
//                event.getLevel().destroyBlock(event.getPos(), false, player);
                while (i-- > 0) {
                    event.getState().getBlock().playerDestroy(player.level(), player, event.getPos(),
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
