package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MiningSpeedAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderMiningSpeedEffect extends BeyonderEffect {
    public float miningSpeed = -1;

    public BeyonderMiningSpeedEffect(){
        super();
    }
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(miningSpeed == -1)
            miningSpeed = MiningSpeedAbility.levelToMaxSpeed.apply(sequenceLevel);
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(sequenceLevel > 7) miningSpeed = MiningSpeedAbility.levelToMaxSpeed.apply(sequenceLevel);
        cap.getEffectsManager().statsHolder.multMiningSpeed(miningSpeed);
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putFloat("speed", miningSpeed);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        if(nbt.contains("speed")) miningSpeed = nbt.getFloat("speed");
        System.out.println("Loaded mining speed with multiplier " + miningSpeed);
    }
}
