package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.entity.LivingEntity;

public class ChaosLuckDownside extends Downside {
    public ChaosLuckDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(cap.getLuckManager().getRandomNumber(0, 3600, true, target.getRandom()) == 0){
            cap.getLuckManager().castOrHurryEvent(target, cap);
            ParticleMaker.createDiceEffectForEntity(target.level(), target);
        }
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_chaos";
    }
}
