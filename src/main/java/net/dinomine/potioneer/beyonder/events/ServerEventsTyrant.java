package net.dinomine.potioneer.beyonder.events;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AmplificationEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.WeakeningEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStats;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.event.AbilityCastEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ServerEventsTyrant {

    @SubscribeEvent
    public static void onAbilityCast(AbilityCastEvent.Pre event){
        if(event.getEntity().level().isClientSide()) return;
        AbilityKey ablKey = event.getAbilityInfo().getKey();
        if(event.getAbility().isDownside()) return;
        event.getEntity().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            WeakeningEffect weakening = (WeakeningEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_WEAKENING.getEffectId());
            int weaken = weakening == null ? -1 : weakening.canWeaken(ablKey, event.getEntity());
            AmplificationEffect amplificationEffect = (AmplificationEffect) cap.getEffectsManager().getEffect(BeyonderEffects.TYRANT_AMPLIFICATION.getEffectId());
            int amplify = amplificationEffect == null ? -1 : amplificationEffect.canAmplify(ablKey, event.getEntity());

            if(weaken == amplify || (weaken != -1 && amplify != -1)) return;
            boolean upgrade = amplify != -1;
            //if < 0 -> downgrade ability
            //if > 0 -> upgrade ability
            int ogLevel = event.getAbility().getSequenceLevel();
            int newLevel = Mth.clamp(ogLevel + (upgrade ? -1 : 1), 0, 9);
            if(newLevel == ogLevel) return;
            event.getAbility().upgradeToLevelSilently(newLevel, cap, event.getEntity());
        });
    }
}
