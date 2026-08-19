package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.downsides.tyrant.AxisDownside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.phys.Vec3;

public class ShockEffect extends BeyonderEffect {


    @Override
    public boolean shouldPersistInDeath() {
        return false;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target, AcquireType acquireType) {
        if(acquireType != AcquireType.ADDED) return;
        cap.getAbilitiesManager().getAllAbilities().forEach(abl -> abl.putOnCooldown(20, target));
        if(target instanceof Monster monster) monster.setTarget(null);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        AxisDownside.alignLook(target);
        target.setDeltaMovement(Vec3.ZERO.with(Direction.Axis.Y, -0.5));
        target.hasImpulse = true;
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
