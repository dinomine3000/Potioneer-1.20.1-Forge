package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class SapEffect extends BeyonderEffect {
    private static final Supplier<Integer> REGEN_DIST = () -> 2;
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(calledOnVictim || attacker == null) return false;
        if(attackerCap.isEmpty()) return false;
        if(victim.distanceTo(attacker) > REGEN_DIST.get()) return false;
        BeyonderCapability cap = attackerCap.get();
        cap.changeSpirituality(cap.getMaxSpirituality()*0.02f);
        attacker.heal(1 + (9-sequenceLevel));
        return false;
    }
}
