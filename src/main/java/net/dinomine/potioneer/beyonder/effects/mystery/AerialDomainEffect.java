package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AerialDomainEffect extends BeyonderEffect {
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim) return false;
        if(!AbilityFunctionHelper.isEntityStandingOnGround(0, victim, true)) event.setAmount(event.getAmount() + 4);
        if(!AbilityFunctionHelper.isEntityStandingOnGround(0, attacker, true)) event.setAmount(event.getAmount() + 2);
        return false;
    }
}
