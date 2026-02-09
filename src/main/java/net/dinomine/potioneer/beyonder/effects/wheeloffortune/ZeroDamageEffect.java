package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class ZeroDamageEffect extends BeyonderEffect {
    private long timestamp = 0L;
    private static final int COOLDOWN = 20*3;
    private boolean doBlocks = false;

    public void setBlock(boolean blocks) {
        this.doBlocks = blocks;
    }

    public boolean doBlocks(){
        return doBlocks;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    public void playSound(LivingEntity target){
        if(target.level().getGameTime() - timestamp > COOLDOWN){
            target.level().playSound(null, target.getOnPos().above(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.3f, (float) target.getRandom().triangle(1d, 0.2d));
            timestamp = target.level().getGameTime();
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("blocks", doBlocks);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.doBlocks = nbt.getBoolean("blocks");
    }
}
