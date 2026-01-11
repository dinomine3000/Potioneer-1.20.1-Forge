package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class ThunderCreateAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "summon_thunder";
    }

    public ThunderCreateAbility(int sequence){
//        this.info = new AbilityInfo(31, 224, "Summon Thunder", 10 + sequence, 160, 20*60, "summon_thunder");
        super(sequence);
        setCost(ignored -> 160);
        defaultMaxCooldown = 20*60;
    }


    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost() && !target.level().isRaining()){
            ((ServerLevel) target.level()).setWeatherParameters(0, 20*60*(1 + 2*(7-getSequenceLevel())), true, true);
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }
}
