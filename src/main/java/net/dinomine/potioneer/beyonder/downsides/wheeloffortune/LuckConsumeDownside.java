package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.world.entity.LivingEntity;

public class LuckConsumeDownside extends Downside {
    public LuckConsumeDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_luck";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        int amount = cap.getLuckManager().getRandomNumber(0, 40 + 10*(10-getSequenceLevel()), false, target.getRandom());
        cap.getLuckManager().consumeLuck(amount);
        if(amount > 25) ParticleMaker.createDiceEffectForEntity(target.level(), target);
        return true;
    }
}
