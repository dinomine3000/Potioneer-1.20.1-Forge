package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class MiningSpeedAbility extends PassiveAbility {

    public MiningSpeedAbility(int sequence){
//        this.info = new AbilityInfo(5, 32, "Mining Speed", sequence, 0, this.getMaxCooldown(), "mining");
        super(sequence, BeyonderEffects.WHEEL_MINING, level -> "mining");
    }

    @Override
    public boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return putOnCooldown(target);
        System.out.println("Warning: Havent implemented secondary ability for mining speed");
        return putOnCooldown(target);
    }
}
