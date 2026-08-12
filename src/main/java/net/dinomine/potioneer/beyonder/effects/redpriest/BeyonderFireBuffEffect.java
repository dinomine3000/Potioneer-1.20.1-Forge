package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderFireBuffEffect extends BeyonderEffect {
    private boolean applied = false;

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.isOnFire() && !target.hasEffect(MobEffects.FIRE_RESISTANCE)){
            target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 1, false, false));
            applied = true;
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(applied && target.hasEffect(MobEffects.FIRE_RESISTANCE))
            target.removeEffect(MobEffects.FIRE_RESISTANCE);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        //TODO check this NBT loading is working
        nbt.putBoolean("applied", applied);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        applied = nbt.getBoolean("applied");
    }
}
