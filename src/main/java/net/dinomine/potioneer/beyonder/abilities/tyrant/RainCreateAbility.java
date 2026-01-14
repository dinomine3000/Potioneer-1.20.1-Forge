package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class RainCreateAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "summon_rain";
    }

    public RainCreateAbility(int sequence){
//        this.info = new AbilityInfo(31, 200, "Summon Rain", 10 + sequence, 70, 20*10, "summon_rain");
        super(sequence);
        setCost(ignored -> 70);
        defaultMaxCooldown = 20*10;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost() && !target.level().isRaining()){
            ((ServerLevel) target.level()).setWeatherParameters(0, 20*60*(1 + 2*(7-getSequenceLevel())), true, false);
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }
}
