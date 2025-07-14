package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.function.Predicate;

public class PushAbility extends Ability {

    public PushAbility(int sequence){
        this.info = new AbilityInfo(57, 296, "Push Away", 20 + sequence, 50*(8-sequence), 3*20, "push_away");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost()) return false;
        Level level = target.level();
        if(level.isClientSide()){
            int temp = 0;
            Vec3 lookAngle = target.getLookAngle();
            Vec3 eyePos = target.getEyePosition();
            while(temp < 8){
                Vec3 itVector = target.getEyePosition().add(lookAngle.scale(temp/8f));
                level.addParticle(ParticleTypes.POOF, eyePos.x, eyePos.y, eyePos.z,itVector.x, -0.02f, itVector.z);
                temp += 1;
            }
        }
        else {
            cap.requestActiveSpiritualityCost(info.cost());
            Vec3 lookAngle = target.getLookAngle();
            int radius = (9 - getSequence())*3 + 2;
            ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, radius);
            float mult = 3;
            hits.forEach(ent -> {
                if(ent instanceof Player player){
                    player.push(lookAngle.x * mult, (-1 - mult / 2), lookAngle.z * mult);
                    player.hurtMarked = true;
                } else ent.addDeltaMovement(lookAngle.multiply(mult, -1 - mult/2, mult));
            });
            level.playSound(null, target.getOnPos().above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
        }
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
