package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class LuckTrendDownwardsDownside extends Downside {

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_luck_trend";
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.getRandom().nextInt(100) == 0){
            cap.getLuckManager().consumeLuck(target, 10 - getSequenceLevel(), false);
        }
    }
}
