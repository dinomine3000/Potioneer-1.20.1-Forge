package net.dinomine.potioneer.beyonder.abilities;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.AsteroidEntity;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class AbilityFunctionHelper {
    public static void sendCommandMessage(LivingEntity target, String commandToRun, String commandIdentifier){
        sendCommandMessage(target, commandToRun, Component.translatable("message.potioneer.message." + commandIdentifier), Component.translatable("message.potioneer.clickable." + commandIdentifier), Component.translatable("message.potioneer.tooltip." + commandIdentifier));
    }
    public static void sendCommandMessage(LivingEntity target, String commandToRun, MutableComponent messageComponent, MutableComponent clickableComponent, MutableComponent tooltipComponent){
        Component clickableText =
                clickableComponent
                .withStyle(style -> style
                        .withUnderlined(true)
                        .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, commandToRun))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, tooltipComponent))
                );

        Component message = messageComponent
                .append(" ")
                .append(clickableText);

        target.sendSystemMessage(message);
    }

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

    public static void removeAttribute(LivingEntity player, Multimap<Attribute, AttributeModifier> pMap){
        player.getAttributes().removeAttributeModifiers(pMap);
    }

    public static void removeAttribute(LivingEntity player, UUID attributeId, String attributeName, double attributeAmount, AttributeModifier.Operation operation, Attribute attribute){
        removeAttribute(player, getEntityModifier(attribute, attributeId, operation, attributeName, attributeAmount));
    }

    public static void addAttributeTo(LivingEntity player, UUID attributeId, String attributeName, double attributeAmount, AttributeModifier.Operation operation, Attribute attribute){
        addAttributeTo(player, getEntityModifier(attribute, attributeId, operation, attributeName, attributeAmount));
    }

    public static void addAttributeTo(LivingEntity player, Multimap<Attribute, AttributeModifier> pMap){
        player.getAttributes().addTransientAttributeModifiers(pMap);
    }

    //Credit to the create mod
    public static Multimap<Attribute, AttributeModifier> getEntityModifier(Attribute attribute, UUID attributeId, AttributeModifier.Operation operation, String name, double amount){
        AttributeModifier modifier =
                new AttributeModifier(attributeId, name, amount, operation);

        Supplier<Multimap<Attribute, AttributeModifier>> resMod = Suppliers.memoize(() ->
                ImmutableMultimap.of(attribute, modifier));
        return resMod.get();
    }

    public static @Nullable LivingEntity getLivingEntityLooking(LivingEntity looker, double reach, int inflate){
        List<LivingEntity> hits = getLivingEntitiesLooking(looker, reach, inflate, false);
        if(hits.isEmpty()) return null;
        LivingEntity closest = null;
        double smallestDist = Double.MAX_VALUE;
        for(LivingEntity ent: hits){
            if(ent.is(looker)) continue;
            double iDist = looker.distanceTo(ent);
            if(iDist < smallestDist){
                smallestDist = iDist;
                closest = ent;
            }
        }
        return closest;
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
        return getTargetEntity(looker, radius, 0, false);
    }

    public static Optional<LivingEntity> getTargetEntity(LivingEntity looker, double radius, boolean includeAllies){
        return getTargetEntity(looker, radius, 0, includeAllies);
    }

    public static Optional<LivingEntity> getTargetEntityClosestToCrosshair(LivingEntity looker, double radius, float inflate, boolean includeAllies){
        ArrayList<LivingEntity> targets = getLivingEntitiesLooking(looker, radius, inflate);
        Optional<LivingEntity> result = Optional.empty();

        // Track the highest dot product (closest to 1.0 means closest to crosshair)
        // We set a minimum threshold (e.g., 0.5) so it doesn't snap to things behind or way off-screen
        double bestAlignment = 0.5;

        // Get the looker's normalized gaze direction vector
        Vec3 lookDir = looker.getLookAngle().normalize();
        Vec3 eyePos = looker.getEyePosition(1.0F);

        for (LivingEntity entity : targets) {
            // Ally check guard clause
            if (!looker.level().isClientSide() && !includeAllies && looker instanceof Player lookerPlayer && entity instanceof Player entityPlayer) {
                AllySystemSaveData allies = AllySystemSaveData.from((ServerLevel) looker.level());
                if (allies.isPlayerAllyOf(lookerPlayer.getUUID(), entityPlayer.getUUID())) {
                    continue;
                }
            }

            // Calculate direction vector from looker's eyes to target's torso/center
            Vec3 targetCenter = entity.position().add(0, entity.getBbHeight() * 0.5, 0);
            Vec3 toTarget = targetCenter.subtract(eyePos).normalize();

            // Dot product reveals angular alignment: 1.0 = perfectly centered, 0.0 = perpendicular, -1.0 = directly behind
            double alignment = lookDir.dot(toTarget);

            // If this target is better aligned with the crosshair than previous ones, select it
            if (alignment > bestAlignment) {
                bestAlignment = alignment;
                result = Optional.of(entity);
            }
        }

        return result;
    }

    public static Optional<LivingEntity> getTargetEntity(LivingEntity looker, double radius, float inflate, boolean includeAllies){
        ArrayList<LivingEntity> targets = getLivingEntitiesLooking(looker, radius, inflate);
        Optional<LivingEntity> result = Optional.empty();
        double smallestDist = Integer.MAX_VALUE;
        for(LivingEntity entity: targets){
            if(!looker.level().isClientSide() && !includeAllies && looker instanceof Player lookerPlayer && entity instanceof Player entityPlayer){
                AllySystemSaveData allies = AllySystemSaveData.from((ServerLevel) looker.level());
                if(allies.isPlayerAllyOf(lookerPlayer.getUUID(), entityPlayer.getUUID())) continue;
            }
            double testDist = looker.position().distanceTo(entity.position());
            if(testDist < smallestDist){
                smallestDist = testDist;
                result = Optional.of(entity);
            }
        }
        return result;
    }

    private static boolean isTargetInSightsOf(LivingEntity target, LivingEntity looker){
        return isTargetInSightsOf(target, looker, 0);
    }

    private static boolean isTargetInSightsOf(LivingEntity target, LivingEntity looker, float inflate){return isTargetInSightsOf(target, looker, inflate, true);}
    private static boolean isTargetInSightsOf(LivingEntity target, LivingEntity looker, float inflate, boolean hitThroughWalls){
        if(looker.is(target)) return false;
        Vec3 lookAngle = looker.getLookAngle();
        double dist = target.position().subtract(looker.getEyePosition()).length();
        Vec3 eye = looker.getEyePosition();
        Vec3 end = looker.getEyePosition().add(lookAngle.scale(dist+1));
        return target.getBoundingBoxForCulling().inflate(inflate).intersects(eye, end) && (hitThroughWalls || looker.hasLineOfSight(target));
    }

    public static ArrayList<LivingEntity> getLivingEntitiesLooking(LivingEntity looker, double radius){
        return getLivingEntitiesLooking(looker, radius, 0);
    }

    public static ArrayList<LivingEntity> getLivingEntitiesLooking(LivingEntity looker, double radius, float inflate){return getLivingEntitiesLooking(looker, radius, inflate, true);}
    public static ArrayList<LivingEntity> getLivingEntitiesLooking(LivingEntity looker, double radius, float inflate, boolean hitThroughWalls){
        return getLivingEntitiesAround(looker, radius, ent -> isTargetInSightsOf(ent, looker, inflate, hitThroughWalls));
    }

    public static void pushEntity(LivingEntity target, Vec3 pushAngle) {
        target.push(pushAngle.x, pushAngle.y, pushAngle.z);
        target.hasImpulse = true;
        target.hurtMarked = true;
        /*if(target instanceof Player player){
            player.push(pushAngle.x, pushAngle.y, pushAngle.z);
            target.hasImpulse = true;
            player.hurtMarked = true;
        } else target.addDeltaMovement(pushAngle);*/
    }

    public static BlockHitResult getBlockLooking(LivingEntity target) {
        return target.level().clip(new ClipContext(
                target.getEyePosition(1),
                target.getEyePosition(1).add(target.getLookAngle().scale(10d)),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.WATER,
                target
        ));
    }
}
