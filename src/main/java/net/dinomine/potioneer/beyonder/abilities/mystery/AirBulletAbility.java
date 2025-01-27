package net.dinomine.potioneer.beyonder.abilities.mystery;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AirBulletAbility extends Ability {

    public AirBulletAbility(int sequence){
        this.info = new AbilityInfo(57, 56, "Air Bullet", 20 + sequence, 10*(10-sequence), 5*20);
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
            double dist = (9 - info.id()%10)*8 + 5;
            float temp = 0.7f;
            Vec3 lookAngle = target.getLookAngle();
            while(temp < dist){
                Vec3 itVector = target.getEyePosition().add(lookAngle.scale(temp));
                level.addParticle(ParticleTypes.POOF, itVector.x, itVector.y, itVector.z,0, -0.02f, 0);
                temp += 0.4f;
            }
        }
        else {
            cap.requestActiveSpiritualityCost(info.cost());
            Vec3 lookAngle = target.getLookAngle();
            Vec3 pos = target.position();
            int radius = (9 - info.id()%10)*8 + 5;
            AABB box = new AABB(
                    pos.x-radius, pos.y-radius, pos.z-radius,
                    pos.x+radius, pos.y+radius, pos.z+radius
            );
            ArrayList<Entity> hits = new ArrayList<>(level.getEntities(target, box, new Predicate<Entity>() {
                @Override
                public boolean test(Entity entity) {
                    if(entity instanceof LivingEntity living){
                        double dist = living.position().subtract(target.position()).length();
//                        System.out.println(dist);
//                        System.out.println(height);
                        boolean hit = living.getBoundingBoxForCulling().intersects(target.getEyePosition(),
                                target.getEyePosition().add(lookAngle.scale(dist+1)));
//                        System.out.println(hit);
                        return hit;
                    }
                    return false;
                }
            }));
            hits.forEach(ent -> {
                int pow = (10-info.id()%10);
                ent.hurt(level.damageSources().indirectMagic(target, target), (float) (0.384f*Math.pow(pow, 2) + 2.461f*pow + 3.938f));
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
