package net.dinomine.potioneer.beyonder.abilities;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.function.Predicate;

public class AbilityFunctionHelper {

    public static ArrayList<Entity> getLivingEntitiesAround(LivingEntity target, double radius){
        return getEntitiesAroundPredicate(target, radius, entity -> entity instanceof LivingEntity);
    }

    public static ArrayList<Entity> getEntitiesAroundPredicate(LivingEntity target, double radius, Predicate<? super Entity> pred){
        Level level = target.level();
        Vec3 pos = target.position();
        AABB box = new AABB(
                pos.x-radius, pos.y-radius, pos.z-radius,
                pos.x+radius, pos.y+radius, pos.z+radius
        );
        ArrayList<Entity> hits = new ArrayList<>(level.getEntities((Entity) null, box, pred));
        return hits;
    }

    public static ArrayList<Entity> getLivingEntitiesLooking(LivingEntity target, double radius){
        Vec3 lookAngle = target.getLookAngle();
        return getEntitiesAroundPredicate(target, radius, entity -> {
            if(entity instanceof LivingEntity living){
                double dist = living.position().subtract(target.position()).length();
                return living.getBoundingBoxForCulling().intersects(target.getEyePosition(),
                        target.getEyePosition().add(lookAngle.scale(dist+1)));
            }
            return false;
        });
    }
}
