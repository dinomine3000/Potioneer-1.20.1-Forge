package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;

public class AirBulletAbility extends Ability {
    @Override
    protected String getDescId(int sequenceLevel) {
        return "air_bullet";
    }

    public AirBulletAbility(int sequence){
        super(sequence);
        setCost(i -> 60 + 10*(9-i));
    }

//    @Override
//    public AbilityInfo getAbilityinfo(int sequenceLevel) {
//        return new AbilityInfo(57, 56, "Air Bullet", 20 + sequenceLevel, 60 + 10*(9-sequenceLevel), 5*20, "air_bullet");
//    }


    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost() || target == null){
            System.out.println("Not enough spirituality to cast air bullet on client side: " + target.level().isClientSide());
            return false;
        }
        Level level = target.level();
        double dist = Math.max((10 - getSequenceLevel())*8 - 8, 21);
        if(level.isClientSide()){
            HitResult hit = target.pick(dist, 0, false);
            float temp = 0.7f;
            Vec3 lookAngle = target.getLookAngle();
            while(temp < Math.min(dist, hit.distanceTo(target))){
                Vec3 itVector = target.getEyePosition().add(lookAngle.scale(temp));
                level.addParticle(ParticleTypes.POOF, itVector.x, itVector.y, itVector.z,0, -0.02f, 0);
                temp += 0.4f;
            }
        }
        else {
            cap.requestActiveSpiritualityCost(cost());
            HitResult hit = target.pick(dist, 0, false);
            ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target,
                    Math.min(dist, hit.distanceTo(target))
            );
            hits.forEach(ent -> {
                int pow = (9-getSequenceLevel());
                if(ent != target) ent.hurt(level.damageSources().indirectMagic(target, null),
                        (float) (0.384f*Math.pow(target.getAttribute(Attributes.ATTACK_DAMAGE).getBaseValue()*pow, 2) + 2.461f*pow + 3.938f));
            });
            level.playSound(null, target.getOnPos().above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
        }
        putOnCooldown(target);
        return true;
    }

}
