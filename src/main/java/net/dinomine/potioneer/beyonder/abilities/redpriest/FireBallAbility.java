package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class FireBallAbility extends Ability {

    public FireBallAbility(int sequence){
        this.info = new AbilityInfo(83, 128, "Fire Ball", 30 + sequence, 20, this.getCooldown(), "fire_ball");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;
        if(cap.getSpirituality() < info.cost()) return false;
        float magnitude = 10f;
        Vec3 look = target.getLookAngle();
        int iterations = (9 - getSequence());
        for(int i = 0; i < iterations; i++){
            Vec3 offset = new Vec3(target.getRandom().triangle(0, 2), 2, target.getRandom().triangle(0, 2));

            //Copied from the blaze class
            SmallFireball fireball = new SmallFireball(target.level(), target, look.x*magnitude, look.y*magnitude, look.z*magnitude);
            fireball.setPos(target.position().add(offset));
            target.level().addFreshEntity(fireball);
            target.level().playSound(null, target.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS);
        }

        cap.requestActiveSpiritualityCost(info.cost());
        return true;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
