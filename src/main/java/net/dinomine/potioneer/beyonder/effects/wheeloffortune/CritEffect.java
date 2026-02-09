package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.particle.ModParticles;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class CritEffect extends BeyonderEffect {
    private int casterId = -1;
    private float amount = 0;

    public void setValues(int casterId, float amount){
        this.casterId = casterId;
        this.amount = amount;
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof CritEffect critEffect)) return;
        if(critEffect.amount > this.amount){
            setValues(critEffect.casterId, critEffect.amount);
        }
        if(lifetime == 10){
            lifetime = 0;
            maxLife = Math.max(critEffect.maxLife, 11);
        }
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(lifetime == 10){
            LivingEntity caster = (LivingEntity) target.level().getEntity(casterId);
            Vec3 eyePos = target.getEyePosition();
            ((ServerLevel) target.level()).sendParticles(ModParticles.CRIT_PARTICLES.get(), eyePos.x, eyePos.y, eyePos.z, 1, 0, 0, 0, 0);
            target.level().playSound(null, target.getOnPos(), ModSounds.CRIT.get(), SoundSource.NEUTRAL);
            if(caster == null)
                target.hurt(PotioneerDamage.crit((ServerLevel) target.level()), amount);
            else
                target.hurt(PotioneerDamage.crit((ServerLevel) target.level(), caster), amount);
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("caster", casterId);
        nbt.putFloat("dmgAmount", amount);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.casterId = nbt.getInt("caster");
        this.amount = nbt.getFloat("dmgAmount");
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
