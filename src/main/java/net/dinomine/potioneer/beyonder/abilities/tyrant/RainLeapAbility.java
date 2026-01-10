package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RainLeapAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "rain_leap";
    }

    public RainLeapAbility(int sequence){
        //TODO: balance spirituality cost
//        this.info = new AbilityInfo(31, 176, "Rain Leap", 10 + sequence, 25, 20*5, "rain_leap");
//        this.isActive = true;
        super(sequence);
        setCost(ignored -> 25);
        defaultMaxCooldown = 20*5;
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost() || (!target.level().isRaining() && !target.level().isThundering() && !target.isInWater())) return false;
        Vec3 look = target.getLookAngle();
        double mult = 2 + 1.2*(8-getSequenceLevel());
        target.addDeltaMovement(look.multiply(mult, mult/2, mult));
        if(!target.level().isClientSide()){
            target.level().playSound(null, target, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1, 0.5f);
            cap.requestActiveSpiritualityCost(cost());
        }
        return putOnCooldown(target);
    }
}
