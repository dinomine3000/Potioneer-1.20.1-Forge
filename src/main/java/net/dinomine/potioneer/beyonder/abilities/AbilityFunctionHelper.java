package net.dinomine.potioneer.beyonder.abilities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class AbilityFunctionHelper {

    public static ArrayList<LivingEntity> getLivingEntitiesAround(LivingEntity target, double radius){
        return getLivingEntitiesAround(target, radius, ignored -> true);
    }

    public static ArrayList<LivingEntity> getLivingEntitiesAround(LivingEntity target, double radius, Predicate<? super LivingEntity> pred){
        List<Entity> test = getEntitiesAroundPredicate(target, radius,
                entity -> entity instanceof LivingEntity);
        return new ArrayList<>(test.stream().map(ent -> (LivingEntity) ent).filter(pred).toList());
    }

    public static ArrayList<Entity> getEntitiesAroundPredicate(LivingEntity target, double radius, Predicate<? super Entity> pred){
        return getEntitiesAroundPredicate(target.getOnPos(), target.level(), radius, pred);
    }

    public static ArrayList<Entity> getEntitiesAroundPredicate(BlockPos blockPos, Level level, double radius, Predicate<? super Entity> pred){
        Vec3 pos = blockPos.getCenter();
        AABB box = new AABB(
                pos.x-radius, pos.y-radius, pos.z-radius,
                pos.x+radius, pos.y+radius, pos.z+radius
        );
        ArrayList<Entity> res = new ArrayList<>(level.getEntities((Entity) null, box, pred));
        return res;
    }

    public static Optional<LivingEntity> getTargetEntity(LivingEntity looker, double radius){
        ArrayList<LivingEntity> targets = getLivingEntitiesLooking(looker, radius);
        Optional<LivingEntity> result = Optional.empty();
        double smallestDist = Integer.MAX_VALUE;
        for(LivingEntity entity: targets){
            double testDist = looker.position().distanceTo(entity.position());
            if(testDist < smallestDist){
                smallestDist = testDist;
                result = Optional.of(entity);
            }
        }
        return result;
    }

    private static boolean isTargetInSightsOf(LivingEntity target, LivingEntity looker){
        if(looker.is(target)) return false;
        Vec3 lookAngle = looker.getLookAngle();
        double dist = target.position().subtract(looker.getEyePosition()).length();
        Vec3 eye = looker.getEyePosition();
        Vec3 end = looker.getEyePosition().add(lookAngle.scale(dist+1));
        return target.getBoundingBoxForCulling().intersects(eye, end);
    }

    public static ArrayList<LivingEntity> getLivingEntitiesLooking(LivingEntity looker, double radius){
        return getLivingEntitiesAround(looker, radius, ent -> isTargetInSightsOf(ent, looker));
    }
}
