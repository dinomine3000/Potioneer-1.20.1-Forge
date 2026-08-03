package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class RainLeapAbility extends Ability {

    @Override
    protected String getMainDescId(int sequenceLevel) {
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
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }
}
