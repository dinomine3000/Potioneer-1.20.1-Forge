package net.dinomine.potioneer.beyonder.abilities;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbilityFunctionHelper {

    public static void summonAsteroid(BlockPos pos, Level level){
        AsteroidEntity ent = new AsteroidEntity(ModEntities.ASTEROID.get(), level);
        ent.setToHit(pos, level.random);
        level.addFreshEntity(ent);
    }

    public static BlockPos getRandomNearbyBlockPos(BlockPos center, int horizontalRadius, int verticalRadius, RandomSource random) {
        int dx = random.nextInt(-horizontalRadius, horizontalRadius + 1);
        int dy = random.nextInt(-verticalRadius, verticalRadius + 1);
        int dz = random.nextInt(-horizontalRadius, horizontalRadius + 1);
        return center.offset(dx, dy, dz);
    }

    /**
     * increments is how many jumps it can do between min and max. so youll have "increments + 1" levels of values.
     * if min is 1 and max is 3, increments should be set to 2 if you want the levels [1, 2, 3]
     * @param min
     * @param max
     * @param increments
     * @param currentVal
     * @return
     */
    public static float incrementThroughRange(float min, float max, int increments, float currentVal){
        float range = Math.abs(max - min);
        if(range == 0) return min;
        if(currentVal == max){
            return min;
        }
        return Mth.clamp(currentVal + range/increments, min, max);
    }

    public static void removeAttribute(Player player, UUID attributeId, String attributeName, int attributeAmount, AttributeModifier.Operation operation, Attribute attribute){
        player.getAttributes().removeAttributeModifiers(getEntityModifier(attribute, attributeId, operation, attributeName, attributeAmount));
    }

    public static void addAttributeTo(Player player, UUID attributeId, String attributeName, int attributeAmount, AttributeModifier.Operation operation, Attribute attribute){
        player.getAttributes().addTransientAttributeModifiers(getEntityModifier(attribute, attributeId, operation, attributeName, attributeAmount));
    }

    //Credit to the create mod
    private static Multimap<Attribute, AttributeModifier> getEntityModifier(Attribute attribute, UUID attributeId, AttributeModifier.Operation operation, String name, int amount){
        AttributeModifier modifier =
                new AttributeModifier(attributeId, name, amount, operation);

        Supplier<Multimap<Attribute, AttributeModifier>> resMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(attribute, modifier));
        return resMod.get();
    }

    public static ArrayList<LivingEntity> getLivingEntitiesAround(LivingEntity target, double radius){
        return getLivingEntitiesAround(target, radius, ignored -> true);
    }

    public static ArrayList<LivingEntity> getLivingEntitiesAround(BlockPos blockPos, Level level, double radius){
        return getLivingEntitiesAround(blockPos, level, radius, ignored -> true);
    }

    public static ArrayList<LivingEntity> getLivingEntitiesAround(LivingEntity target, double radius, Predicate<? super LivingEntity> pred){
        List<Entity> test = getEntitiesAroundPredicate(target, radius,
                entity -> entity instanceof LivingEntity);
        return new ArrayList<>(test.stream().map(ent -> (LivingEntity) ent).filter(pred).toList());
    }

    public static ArrayList<LivingEntity> getLivingEntitiesAround(BlockPos blockPos, Level level, double radius, Predicate<? super LivingEntity> pred){
        List<Entity> test = getEntitiesAroundPredicate(blockPos, level, radius,
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

    public static void pushEntity(LivingEntity target, Vec3 pushAngle) {
        if(target instanceof Player player){
            player.push(pushAngle.x, pushAngle.y, pushAngle.z);
            player.hurtMarked = true;
        } else target.addDeltaMovement(pushAngle);
    }
}
