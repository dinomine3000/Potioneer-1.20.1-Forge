package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class FateCastDownside extends Downside {

    public FateCastDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_fate";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        cap.getLuckManager().castEventNoRefresh(target);
        return true;
    }
}
