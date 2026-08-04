package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraRecipientEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.ContractedEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.event.AbilityCastEvent;
import net.dinomine.potioneer.event.AbilityPossessionEvent;
import net.dinomine.potioneer.event.ArtifactPossessionEvent;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber
public class ServerEventsTyrant {

    @SubscribeEvent
    public static void livingDestroyBlock(LivingDestroyBlockEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(
                (event.getEntity() instanceof Player && !PotioneerAbilityConfig.AOJ_PLAYER_GRIEFING.get())
            || (!(event.getEntity() instanceof Player) && !PotioneerAbilityConfig.AOJ_MOB_GRIEFING.get())
        ) {
            for(Player player: event.getEntity().level().players()){
                if(AreaOfJurisdictionAbility.isPosInAOJ(event.getPos(), player)){
                    event.setCanceled(event.isCancelable());
                    event.setResult(Event.Result.DENY);
                    return;
                }
            }
        }
    }

    @SubscribeEvent
    public static void mobDestroy(EntityMobGriefingEvent event){
        if(event.getEntity().level().isClientSide()) return;
        if(!PotioneerAbilityConfig.AOJ_MOB_GRIEFING.get()) return;
        for(Player player: event.getEntity().level().players()){
            if(AreaOfJurisdictionAbility.isPosInAOJ(event.getEntity().getOnPos(), player)){
                event.setResult(Event.Result.DENY);
                return;
            }
        }
    }



    @SubscribeEvent
    public static void onAbilityLost(AbilityPossessionEvent.Lost event){
        if(event.getEntity().level().isClientSide()) return;
        Ability abl = event.getAbility();
        if(event.getAbility().isDownside()) return;
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
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
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
            AmplificationEffect amplificationEffect = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());

            if(weakening != null) weakening.artifactRemoved(abilities);
            if(amplificationEffect != null) amplificationEffect.artifactRemoved(abilities);
        });
    }

    @SubscribeEvent
    public static void onAbilityCast(AbilityCastEvent.Pre event){
        if(event.getEntity().level().isClientSide()) return;
        Ability abl = event.getAbility();
        if(event.getAbility().isDownside()) return;
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
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
            AuraRecipientEffect aura = AbilityFunctionHelper.getEffectOnPlayer(BeyonderEffects.TYRANT_AURA_RECIPIENT.getEffectId(), event.getEntity());
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
