package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.SmallFireball;
import net.minecraft.world.phys.Vec3;

public class FireBallAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "fire_ball";
    }

    public FireBallAbility(int sequence){
//        this.info = new AbilityInfo(83, 128, "Fire Ball", 30 + sequence, 20, this.getMaxCooldown(), "fire_ball");
        super(sequence);
        setCost(ignored -> 20);
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost()) return true;
        if(cap.getSpirituality() < cost()) return false;
        float magnitude = 10f;
        Vec3 look = target.getLookAngle();
        int iterations = (9 - getSequenceLevel());
        for(int i = 0; i < iterations; i++){
            Vec3 offset = new Vec3(target.getRandom().triangle(0, 2), 2, target.getRandom().triangle(0, 2));

            //Copied from the blaze class
            SmallFireball fireball = new SmallFireball(target.level(), target, look.x*magnitude, look.y*magnitude, look.z*magnitude);
            fireball.setPos(target.position().add(offset));
            target.level().addFreshEntity(fireball);
            target.level().playSound(null, target.getOnPos(), SoundEvents.FIRECHARGE_USE, SoundSource.PLAYERS);
        }

        cap.requestActiveSpiritualityCost(cost());
        return true;
    }
}
