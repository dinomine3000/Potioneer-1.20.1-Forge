package net.dinomine.potioneer.beyonder.downsides.tyrant;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class WaterDownside extends Downside {

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(!cap.getLuckManager().passesLuckCheck(0.7f, 0, 0, target.getRandom())) cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_DROWNING.createInstance(getSequenceLevel(), 0, 20*10, true), cap, target);
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.isInWater()) cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.TYRANT_WATER_PRISON.createInstance(getSequenceLevel(), 0, 20, true), cap, target);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_water";
    }
}
