package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.RulePylonAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.*;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.event.*;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

import static net.dinomine.potioneer.config.PotioneerAbilityConfig.BRIBE_CANCEL_CHANCE;
import static net.dinomine.potioneer.config.PotioneerAbilityConfig.BRIBE_MISCAST_RADIUS;

@Mod.EventBusSubscriber
public class ServerEventsTyrant {


    private static final int AOJ_CHECK_INTERVAL = 20*5;
    @SubscribeEvent
    public static void livingTick(LivingEvent.LivingTickEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(event.getEntity().tickCount%AOJ_CHECK_INTERVAL != event.getEntity().getId()%AOJ_CHECK_INTERVAL) return;
        RulePylonBlockEntity be = DimensionChunkSavedData.getRulingPylon((ServerLevel) event.getEntity().level(), event.getEntity().getOnPos());
        if(be == null) return;
        event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(!cap.getAbilitiesManager().hasAbility(Abilities.AOJ.getAblId())) return;
            boolean flag = AreaOfJurisdictionAbility.getCentersOfEnforcer(event.getEntity(), event.getEntity().level().dimension()).stream().anyMatch(
                    center -> be.getClaimedChunks().stream().anyMatch(chunkPos -> chunkPos.equals(new ChunkPos(center))));
            if(flag) ruleBroken(RulePylonAbility.Rule.AOJ, event.getEntity());
        });
        if(event.getEntity().isSprinting()) ruleBroken(RulePylonAbility.Rule.SPRINT, event.getEntity());
        if(event.getEntity().getMainHandItem().is(ModTags.Items.WEAPON_PROFICIENCY)) ruleBroken(RulePylonAbility.Rule.WEAPONS, event.getEntity());
    }
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        /*if (event.phase == TickEvent.Phase.START) {
            Player player = event.player;
            GeneralProhibitionEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_GENERAL_PROHIBITION.getEffectId(), player);
            if(eff == null) return;
            if(eff.type.equalsIgnoreCase("sprinting") && player.isSprinting())
                player.setSprinting(false);
        }*/
    }

    //called to propose item pickup. can be cancelled
    public static void itemTryPickupEvent(EntityItemPickupEvent event){}

    //called after item was picked up
    @SubscribeEvent
    public static void itemPickedUpEvent(PlayerEvent.ItemPickupEvent event){
        Entity owner = event.getOriginalEntity().getOwner();
        if(!(owner instanceof LivingEntity livingEntity)) return;
        BribeSourceEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_BRIBE.getEffectId(), livingEntity);
        if(eff == null) return;
        event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().addOrReplaceEffect(eff.createRecipientEffect(owner.getUUID()), cap, event.getEntity());
        });
    }

    //called when item is dropped
    public static void itemTossEvent(ItemTossEvent event){}

    @SubscribeEvent
    public static void onEntityJump(LivingEvent.LivingJumpEvent event){
        ruleBroken(RulePylonAbility.Rule.JUMP, event.getEntity());
    }

    public static void ruleBroken(RulePylonAbility.Rule rule, Entity ruleBreaker){
        if(ruleBreaker == null) return;
        ruleBroken(rule, ruleBreaker, ruleBreaker.getOnPos());
    }
    public static void ruleBroken(RulePylonAbility.Rule rule, Entity ruleBreaker, BlockPos testPos){
        if(ruleBreaker.level().isClientSide()) return;
        RulePylonBlockEntity be = DimensionChunkSavedData.getRulingPylon((ServerLevel) ruleBreaker.level(), testPos);
        if(be != null && ruleBreaker instanceof LivingEntity lRuleBreaker) be.brokeRule(rule, lRuleBreaker);
    }

    @SubscribeEvent
    public static void livingDestroyBlock(LivingDestroyBlockEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(
                (event.getEntity() instanceof Player && !PotioneerAbilityConfig.AOJ_PLAYER_GRIEFING.get())
            || (!(event.getEntity() instanceof Player) && !PotioneerAbilityConfig.AOJ_MOB_GRIEFING.get())
        ) {
            for(Player player: event.getEntity().level().players()){
                if(AreaOfJurisdictionAbility.isPosInAOJ(event.getPos(), player, event.getEntity().level().dimension())){
                    event.setCanceled(event.isCancelable());
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
        //block break rule
        ruleBroken(RulePylonAbility.Rule.BLOCK_BREAK, event.getEntity(), event.getPos());
    }

    @SubscribeEvent
    public static void mobDestroy(EntityMobGriefingEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(!PotioneerAbilityConfig.AOJ_MOB_GRIEFING.get()) return;
        for(Player player: event.getEntity().level().players()){
            if(AreaOfJurisdictionAbility.isEntityInAOJ(event.getEntity(), player)){
                event.setResult(Event.Result.DENY);
                return;
            }
        }
        ruleBroken(RulePylonAbility.Rule.BLOCK_BREAK, event.getEntity(), event.getEntity().getOnPos());
    }


    @SubscribeEvent
    public static void onAbilityLost(AbilityPossessionEvent.Lost event){
        if(event.getEntity().level().isClientSide()) return;
        Ability abl = event.getAbility();
        if(event.getAbility().isDownside()) return;
        event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
            AmplificationEffect amplificationEffect = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());

            if(weakening != null) weakening.abilityRemoved(abl.getInstanceId());
            if(amplificationEffect != null) amplificationEffect.abilityRemoved(abl.getInstanceId());
        });
    }

    @SubscribeEvent
    public static void onArtifactLost(ArtifactPossessionEvent.Lost event){
        if(event.getEntity().level().isClientSide()) return;
        List<Ability> abilities = new ArrayList<>(event.getArtifact().getAbilities());
        if(abilities.isEmpty()) return;
        event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
            AmplificationEffect amplificationEffect = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());

            if(weakening != null) weakening.artifactRemoved(abilities);
            if(amplificationEffect != null) amplificationEffect.artifactRemoved(abilities);
        });
    }

    @SubscribeEvent
    public static void onLuckChange(LuckChangeEvent event){
        if(event.isNatural()) return;
        ruleBroken(RulePylonAbility.Rule.LUCK, event.getEntity());
    }

    @SubscribeEvent
    public static void onLuckCastEvent(LuckEventCastEvent.Pre event){
        GeneralProhibitionEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_GENERAL_PROHIBITION.getEffectId(), event.getEntity());
        if(eff == null) return;
        if(eff.type.equalsIgnoreCase("fate")) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onLuckTriggerEvent(LuckEventCastEvent.TriggeredPre event){
        GeneralProhibitionEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_GENERAL_PROHIBITION.getEffectId(), event.getEntity());
        if(eff == null) return;
        if(eff.type.equalsIgnoreCase("fate")) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event){
        ruleBroken(RulePylonAbility.Rule.BLOCK_PLACE, event.getEntity(), event.getPos());
    }

    //called after calculation has gone through.
    @SubscribeEvent
    public static void onEntityTakeDamage(LivingDamageEvent event){
        if(event.getSource().is(PotioneerDamage.STRIKE) || event.getSource().is(PotioneerDamage.LAW)) return;
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else return;

        ruleBroken(RulePylonAbility.Rule.HURT, attacker, attacker.getOnPos());
    }


    @SubscribeEvent
    public static void onAfterAbilityCast(AbilityCastEvent.Post event){
        if(event.getEntity().level().isClientSide()) return;
        if(event.getAbility().isDownside()) return;
        //rule broken
        ruleBroken(RulePylonAbility.Rule.BEYONDER, event.getEntity(), event.getEntity().getOnPos());
    }
    //specifically to prevent ability casts from happening if you have the prohibition effect
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void abilityCancelCheck(AbilityCastEvent.Pre event){
        if(event.getEntity().level().isClientSide()) return;
        if(!event.isCanceled()) prohibitionCheck(event);
        if(!event.isCanceled()) bribeCheck(event);
    }

    private static void prohibitionCheck(AbilityCastEvent.Pre event){
        Ability abl = event.getAbility();
        AbilityProhibitionEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_ABILITY_PROHIBITION.getEffectId(), event.getEntity());
        if(eff == null) return;
        if(eff.onAbilityCast(event.getEntity(), abl.getAbilityId())) event.setCanceled(true);
    }

    private static void bribeCheck(AbilityCastEvent.Pre event){
        BribeRecipientEffect bribe = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_BRIBE_RECIPIENT.getEffectId(), event.getEntity());
        if(bribe == null) return;
        Entity tribunal = bribe.getTribunal((ServerLevel) event.getEntity().level());
        if(tribunal == null) return;
        BeyonderCapability cap = event.getEntity().getCapability(CapProvider.BEYONDER_STATS).resolve().get();
        if(tribunal.distanceTo(event.getEntity()) < BRIBE_MISCAST_RADIUS.get() &&
                !cap.getLuckManager().passesLuckCheck(BRIBE_CANCEL_CHANCE.get().floatValue(), 5, 5, event.getEntity().getRandom())) event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onBeforeAbilityCast(AbilityCastEvent.Pre event){
        if(event.getEntity().level().isClientSide()) return;
        Ability abl = event.getAbility();
        if(event.getAbility().isDownside()) return;
        if(event.getAbility().getAbilityKey().isArtifactKey()) ruleBroken(RulePylonAbility.Rule.ARTIFACT, event.getEntity(), event.getEntity().getOnPos());
        event.getEntity().getCapability(CapProvider.BEYONDER_STATS).ifPresent(cap -> {
            //amplification
            WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
            if(weakening != null) weakening.tryWeaken(abl, cap, event.getEntity());
            AmplificationEffect amplificationEffect = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());
            if(amplificationEffect != null) amplificationEffect.tryAmplify(abl, cap, event.getEntity());

            //contract
            ContractedEffect contract = (ContractedEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_CONTRACT.getEffectId());
            if(contract != null){
                contract.testAbilityCast(event.getAbility(), cap, event.getEntity());
            }

            //aura cancel
            AuraRecipientEffect aura = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.TYRANT_AURA_RECIPIENT.getEffectId(), event.getEntity());
            if(aura != null && aura.isOrBetter(6)){
                if(!cap.getLuckManager().passesLuckCheck(PotioneerAbilityConfig.AURA_MISCAST_CHANCE.get().floatValue(), 0, 0, event.getEntity().getRandom())){
                    event.setCanceled(true);
                    cap.getAbilitiesManager().putAbilityOnCooldown(event.getAbility().getAbilityKey(), 20*5, event.getEntity());
                    event.getEntity().level().playSound(null, event.getEntity().getOnPos(), ModSounds.FAIL_CAST.get(), SoundSource.PLAYERS, 1, 1);
                }
            }
        });
    }
}
