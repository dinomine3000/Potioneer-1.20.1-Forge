package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

public class FakeLagDownside extends Downside {
    public FakeLagDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_lag";
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.getRandom().nextInt(500) == 0){
            CompoundTag tag = getData();
            if(tag.contains("posX")){
                float health = tag.getFloat("health");
                target.setHealth(health);
                target.teleportTo(tag.getDouble("posX"), tag.getDouble("posY"), tag.getDouble("posZ"));
                target.level().playSound(null, target.getOnPos(), ModSounds.UNLUCK.get(), SoundSource.NEUTRAL);
                tag.remove("posX");
            } else {
                tag.putFloat("health", target.getHealth());
                tag.putDouble("posX", target.getX());
                tag.putDouble("posY", target.getY());
                tag.putDouble("posZ", target.getZ());
            }
            setData(tag, target);
        }
    }
}
