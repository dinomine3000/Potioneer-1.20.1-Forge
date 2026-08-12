package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ArrowGravitateEffect extends BeyonderEffect {
    private LivingEntity proposedTarget = null;
    private float velocity = 3;

    public void setValues(LivingEntity proposedTarget, float velocity){
        this.proposedTarget = proposedTarget;
        this.velocity = velocity;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(proposedTarget == null || !proposedTarget.isAlive()) {
            endEffectWhenPossible();
            return;
        }
        List<Entity> hits = AbilityFunctionHelper.getEntitiesAroundPredicate(target, 5, ent -> ent instanceof AbstractArrow);
        for(Entity ent: hits){
            if(!(ent instanceof AbstractArrow arrow) ) continue;
            if(arrow.getOwner() != null && arrow.getOwner().is(target)){
                //if(!(ent instanceof MarkedProjectile markedProjectile) || markedProjectile.potioneer$marked()) continue;
//                arrow.shoot(proposedTarget.getX() - arrow.getX(), proposedTarget.getY() - arrow.getY() + 1, proposedTarget.getZ() - arrow.getZ(), velocity, 0);

                arrow.setDeltaMovement(new Vec3(proposedTarget.getX() - arrow.getX(), proposedTarget.getEyeY() - arrow.getY(), proposedTarget.getZ() - arrow.getZ())
                        .normalize().scale(arrow.getDeltaMovement().length()));
                endEffectWhenPossible();
                arrow.hasImpulse = true;
                //markedProjectile.potioneer$setMarked();
            }
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }
}
