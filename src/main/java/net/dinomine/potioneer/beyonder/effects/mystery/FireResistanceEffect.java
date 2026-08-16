package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class FireResistanceEffect extends BeyonderEffect {
    private boolean resFlag = false;

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {}

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target instanceof Player player){
            if(isInFire(player, sequenceLevel)){
                if(!player.hasEffect(MobEffects.FIRE_RESISTANCE)){
                    this.resFlag = true;
                    player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, maxLife - lifetime, 0, false, false));
                }
                target.extinguishFire();
            } else {
                if(resFlag){
                    target.removeEffect(MobEffects.FIRE_RESISTANCE);
                    resFlag = false;
                }
            }
        }
    }

    public static boolean isInFire(LivingEntity player, int level){
        return player.isOnFire();
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("fireFlag", resFlag);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.resFlag = nbt.getBoolean("fireFlag");
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(resFlag){
            target.removeEffect(MobEffects.WATER_BREATHING);
            resFlag = false;
        }
    }
}
