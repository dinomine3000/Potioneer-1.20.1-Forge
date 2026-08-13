package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AuraDownsideEffect extends BeyonderEffect {

    private int mobId = 0;

    public void setup(int id){this.mobId = id;}

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }


    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(mobId == 0) {
            endEffectWhenPossible();
            return;
        }
        Entity ent = target.level().getEntity(mobId);
        if(!(ent instanceof LivingEntity livingEntity)){
            endEffectWhenPossible();
            return;
        }

        applyAuraEffects(target, livingEntity);
    }

    private void applyAuraEffects(LivingEntity livingEntity, LivingEntity auraEntity){
        livingEntity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 20*2, 0, false, false, true));
        ParticleMaker.createAuraParticles(auraEntity, livingEntity);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("mobId", mobId);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        mobId = nbt.getInt("mobId");
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        if(attacker == null || attacker.getId() != mobId) return false;
        event.setAmount(event.getAmount()*0.2f);
        return false;
    }
}
