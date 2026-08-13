package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class ChaosLuckDownside extends Downside {
    public ChaosLuckDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(cap.getLuckManager().getRandomNumber(0, 3600, true, target.getRandom()) == 0){
            cap.getLuckManager().castOrHurryEvent(target, cap);
            ParticleMaker.createDiceEffectForEntity(target.level(), target);
        }
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.WHEEL_CALAMITY.createInstance(sequenceLevel, 0, 20*60*20, true), cap, target);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_chaos";
    }
}
