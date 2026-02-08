package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.Optional;


public class FateAbility extends Ability {
    @Override
    protected String getDescId(int sequenceLevel) {
        return "fate";
    }

    public FateAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*60;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        if(target.level().isClientSide()) return true;
        cap.getEffectsManager().addEffectNoCheck(BeyonderEffects.WHEEL_FATE.createInstance(getSequenceLevel(), cost(), 2, true), cap, target);
        cap.getLuckManager().consumeLuck(50);
        if(getSequenceLevel() < 6) ParticleMaker.createDiceEffectForEntity(target.level(), target);
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(getSequenceLevel() > 5) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        Optional<LivingEntity> eventTarget = AbilityFunctionHelper.getTargetEntity(target, target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + 1, true);
        if(eventTarget.isPresent()){
            Optional<LivingEntityBeyonderCapability> optCap = eventTarget.get().getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
            if(optCap.isEmpty()) return false;
            LivingEntityBeyonderCapability fateCap = optCap.get();
            fateCap.getEffectsManager().addEffectNoCheck(BeyonderEffects.WHEEL_FATE.createInstance(getSequenceLevel(), 0, 2, true), fateCap, eventTarget.get());
            cap.getLuckManager().consumeLuck(50);
            cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.MISFORTUNE_ACTING_INC, 5);
            ParticleMaker.createDiceEffectForEntity(target.level(), eventTarget.get());
            return true;
        }
        return false;
    }
}
