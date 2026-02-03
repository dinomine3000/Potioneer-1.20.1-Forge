package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class LuckTrendDownwardsDownside extends Downside {

    public LuckTrendDownwardsDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_luck_trend";
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.getRandom().nextInt(100) == 0){
            cap.getLuckManager().consumeLuck(10 - getSequenceLevel());
        }
    }
}
