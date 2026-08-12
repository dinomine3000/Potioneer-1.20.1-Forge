package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager;
import net.dinomine.potioneer.beyonder.abilities.tyrant.ProhibitionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class AbilityProhibitionEffect extends AbstractSourceRecipientEffect {

    public static AbilityProhibitionEffect createInstance(int sequenceLevel, UUID sourceId){
        AbilityProhibitionEffect eff = (AbilityProhibitionEffect) BeyonderEffects.TYRANT_ABILITY_PROHIBITION.createInstance(sequenceLevel, 0, -1, false);
        eff.setup(sourceId);
        return eff;
    }

    public void setup(UUID sourceId){
        addSource(sourceId, ProhibitionAbility.ABILITY_PROHIBITION_WINDOW.get(), null);
    }

    public boolean onAbilityCast(LivingEntity target, String abilityId){
        if(target.level().isClientSide()) return false;
        ServerLevel level = (ServerLevel) target.level();
        int radius = ProhibitionAbility.PROHIBITION_RADIUS.get();

        //1. find all sources on this effect. ignore casts
        List<Player> casters = getPlayerList(level).stream().filter(player ->
                player.position().distanceTo(target.position()) < radius
                && (PotioneerAbilityConfig.PROHIBITION_AFFECTS_SELF.get() || !player.is(target))
        ).toList();
        if(casters.isEmpty()) return false;

        //2. contact them and find every entity around them
        Set<LivingEntity> hits = new HashSet<>();
        DimensionChunkSavedData data = DimensionChunkSavedData.from(level);
        for(Player player: casters){
            hits.addAll(AbilityFunctionHelper.getLivingEntitiesAround(player, radius));

            //2.1 if a source is in an area controlled by their rule pylon, also include anyone in that pylon's area.
            RulePylonBlockEntity be = data.getBlockEntityOfChunk(level, player.getOnPos(), false);
            if(be != null && be.isOwner(player)) hits.addAll(be.getEntities());

            //3. clear other instances of this effect, as created by the sources
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getAbilitiesManager().getAbilities(Abilities.PROHIBITION.getAblId()).stream()
                        .map(abl -> (ProhibitionAbility) abl)
                        .forEach(prohibitionAbility -> prohibitionAbility.clearEffectForEveryone(level, player));
            });

            PacketHandler.sendMessageToClientsAround(player, radius,
                    new GeneralAreaEffectMessage(ParticleMaker.Preset.AOE_END_ROD, player.getOnPos().getCenter().toVector3f(), radius));
        }

        //4. disable the given ability to everyone in that area
        for(LivingEntity hit: hits){
            DisabledAbilitiesManager.DisabledAbilityProxy proxy = DisabledAbilitiesManager.DisabledAbilityProxy.byId(abilityId, ProhibitionAbility.ABILITY_PROHIBITION_DURATION.get());
            hit.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility(UUID.randomUUID().toString(), proxy, cap, hit);
            });
        }
        return true;
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof AbilityProhibitionEffect incomingEffect)) return;
        for(Map.Entry<UUID, Integer> entry: incomingEffect.sources.entrySet()){
            addSource(entry.getKey(), entry.getValue(), target);
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        tickDownTime(target);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
