package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class CalamityEffect extends BeyonderEffect {
    private static final Function<Integer, Integer> addedChance = level -> 2 + Math.max(7-level, 0);
    private static final UUID luckAttributeUUID = UUID.fromString("3aa8f6cd-4039-427b-98f1-a52c0825a5f9");

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int numArtifacts = cap.getAbilitiesManager().getNumArtifacts();
        if(sequenceLevel <= 7){
            cap.getLuckManager().chanceLuckEventChange(luckAttributeUUID, addedChance.apply(getSequenceLevel()));
            cap.getLuckManager().changeLuckRange(luckAttributeUUID, 100, 100, -numArtifacts*25);
            return;
        }
        cap.getLuckManager().changeLuckRange(luckAttributeUUID, 0, 0, -numArtifacts*25);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().removeModifier(luckAttributeUUID);
        cap.getLuckManager().removeLuckEventModifier(luckAttributeUUID);
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(attacker == null || victim.level().isClientSide() || optAttackerCap.isEmpty() || !calledOnVictim) return false;
        LivingEntityBeyonderCapability attackerCap = optAttackerCap.get();

        if(sequenceLevel < 6){
            if(!attackerCap.getLuckManager().passesLuckCheck(9/10f, (int) (event.getAmount()*5), 0, attacker.getRandom())){
                attackerCap.getLuckManager().castOrHurryEvent(attacker, attackerCap);
            }
            DamageRecordingEffect eff = (DamageRecordingEffect) victimCap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_DAMAGE_RECORDING.getEffectId());
            if(eff == null) return false;
            float amount = eff.getRecordedDamage(false);
            attacker.hurt(PotioneerDamage.crit((ServerLevel) victim.level(), victim), amount/3f);
            attacker.level().playSound(null, attacker.getOnPos(), ModSounds.CRIT.get(), SoundSource.PLAYERS, 1, 1);
        } else if(sequenceLevel < 8){
            if(!attackerCap.getLuckManager().passesLuckCheck(9/10f, (int) (event.getAmount()*5), 0, attacker.getRandom())){
                if(sequenceLevel > 5)
                    attackerCap.getLuckManager().castEventNoRefresh(attacker);
            }
        }
        return false;
    }
}
