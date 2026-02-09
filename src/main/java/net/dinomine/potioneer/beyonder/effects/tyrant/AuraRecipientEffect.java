package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;

public class AuraRecipientEffect extends AbstractSourceRecipientEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    public void addSourceSilent(UUID enforcerId){
        super.addSource(enforcerId, 40, null);
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof AuraRecipientEffect aojEffect)) return;
        for(Map.Entry<UUID, Integer> entry: aojEffect.sources.entrySet()){
            addSource(entry.getKey(), entry.getValue(), target);
        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        for(UUID id: sources.keySet()){
            Player playerEnforcer = target.level().getPlayerByUUID(id);
            if(playerEnforcer == null) continue;
            applyAuraEffects(target, cap, playerEnforcer);
        }
        if(target.level().isClientSide()) return;
        tickDownTime(target);
        if(target.tickCount%40 == 0){
            target.level().playSound(null, target.getOnPos(), SoundEvents.WARDEN_HEARTBEAT, SoundSource.NEUTRAL, 1, 1);
        }
    }

    private void applyAuraEffects(LivingEntity livingEntity, LivingEntityBeyonderCapability cap, Player enforcer){
        if(!sources.containsKey(enforcer.getUUID())) return;
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20*2, 0, false, false, true));
        ParticleMaker.createAuraParticles(enforcer, livingEntity);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
