package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.ArrestSourceEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.Supplier;

public class ArrestAbility extends PassiveAbility {
    private static final Supplier<Integer> MANUAL_CAST_COST = PotioneerAbilityConfig.ARREST_MANUAL_COST;
    private static final Supplier<Integer> CAST_COST = PotioneerAbilityConfig.ARREST_COST;
    public ArrestAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.TYRANT_ARREST_SOURCE, lv -> lv < 7 ? "arrest_2" : "arrest");
        enabledOnAcquire();
        canFlip(sequenceLevel < 7);
        withCost(CAST_COST.get());
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, LivingEntityBeyonderCapability cap, LivingEntity target) {
        canFlip(newLevel < 7);
        if(newLevel > 6) setEnabled(cap, target, true);
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(getSequenceLevel() >= 7) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        if(cap.getSpirituality() < MANUAL_CAST_COST.get()) return false;
        if(target.level().isClientSide()) return true;
        List<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(target, 4);
        if(hits.isEmpty()) return false;
        boolean aoj = AreaOfJurisdictionAbility.isEntityInAOJ(target, target);
        PacketHandler.sendMessageToClientsAround(target, 4, new GeneralAreaEffectMessage(ParticleMaker.Preset.AOE_END_ROD, target.getOnPos().getCenter().toVector3f(), 4));
        hits.forEach(ent -> ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap -> ArrestSourceEffect.applyArrestToRecipient(target, victimCap, ent, sequenceLevel, aoj)));
        cap.requestActiveSpiritualityCost(MANUAL_CAST_COST.get());
        setNextCooldownAs(20*5);
        return true;
    }
}
