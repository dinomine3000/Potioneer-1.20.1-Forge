package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.Optional;


public class DamageRecordingEffect extends BeyonderEffect {
    private static final int recordingMaxTime = 10*20;
    private float recordedDamage = 0;
    private long recordingTimeStamp = 0L;
    private boolean recording = false;

    public void setRecording(Level level){
        recordedDamage = 0;
        recordingTimeStamp = level.getGameTime();
        recording = true;
    }

    public float getRecordedDamage(boolean consume){
        float result = recordedDamage;
        if(consume) recordedDamage = 0;
        return result;
    }

    public boolean isRecording() {
        return recording;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(recording && target.level().getGameTime() - recordingTimeStamp > recordingMaxTime){
            target.sendSystemMessage(Component.translatable("ability.potioneer.damage_recording_over", recordedDamage));
            recording = false;
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("recording", recording);
        nbt.putLong("recordingTimestamp", recordingTimeStamp);
        nbt.putFloat("damage", recordedDamage);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.recording = nbt.getBoolean("recording");
        this.recordingTimeStamp = nbt.getLong("recordingTimestamp");
        this.recordedDamage = nbt.getFloat("damage");
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;

        if(victim.level().getGameTime() - recordingTimeStamp < recordingMaxTime){
            recordedDamage += event.getAmount();
        }
        return false;
    }
}
