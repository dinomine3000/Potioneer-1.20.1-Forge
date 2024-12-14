package net.dinomine.potioneer.entities.goals;

import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class ChryonPierceGoal extends Goal {
    protected final ChryonEntity mob;
    private final double speedModifier;
    private final boolean followingTargetEvenIfNotSeen;
    private Path path;
    private double pathedTargetX;
    private double pathedTargetY;
    private double pathedTargetZ;
    private int ticksUntilNextPathRecalculation;
    private int ticksUntilNextAttack;
    private final int attackInterval = 20;
    private long lastCanUseCheck;
    private static final long COOLDOWN_BETWEEN_CAN_USE_CHECKS = 20L;
    private int failedPathFindingPenalty = 0;
    private boolean canPenalize = false;
    private int MAX_COOLDOWN = 100;
    private int cooldown;


    public ChryonPierceGoal(ChryonEntity pMob, double pSpeedModifier) {
        this.mob = pMob;
        this.cooldown = -1;
        this.speedModifier = pSpeedModifier;
        this.followingTargetEvenIfNotSeen = false;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        return Math.max(this.cooldown--, -1) < 0 && !this.getNearbyEnemies().isEmpty();
    }

    private List<LivingEntity> getNearbyEnemies() {
        List<LivingEntity> enemies = new ArrayList<>();
        if(this.mob.getTarget() != null && this.mob.getTarget().isAlive()){
            enemies.add(this.mob.getTarget());
        }
        return enemies;
    }


    public boolean canContinueToUse() {
        LivingEntity livingentity = this.mob.getTarget();
        if (livingentity == null) {
            return false;
        } else if (!livingentity.isAlive()) {
            return false;
        } else {
            checkAndPerformAttack(livingentity, this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(livingentity));
            this.ticksUntilNextAttack--;
            return !(livingentity instanceof Player) || !livingentity.isSpectator() && !((Player)livingentity).isCreative();
        }
    }

    public void start() {
        this.mob.setAggressive(true);
        this.ticksUntilNextAttack = 0;
        if(this.mob.getTarget() != null){
            checkAndPerformAttack(this.mob.getTarget(), this.mob.getPerceivedTargetDistanceSquareForMeleeAttack(this.mob.getTarget()));
        }
    }

    public void stop() {
        this.cooldown = MAX_COOLDOWN;
    }

    public boolean requiresUpdateEveryTick() {
        return true;
    }

    protected void checkAndPerformAttack(LivingEntity pEnemy, double pDistToEnemySqr) {
        double d0 = this.getAttackReachSqr(pEnemy);
        if (pDistToEnemySqr <= d0 && this.ticksUntilNextAttack <= 0) {
            this.resetAttackCooldown();
            this.mob.swing(InteractionHand.MAIN_HAND);
        } else if (this.ticksUntilNextAttack == 40) {
            this.mob.doHurtTarget(pEnemy);
        }
    }

    protected void resetAttackCooldown() {
        this.ticksUntilNextAttack = this.adjustedTickDelay(60);
    }

    protected int getAttackInterval() {
        return this.adjustedTickDelay(60);
    }

    protected double getAttackReachSqr(LivingEntity pAttackTarget) {
        return (double)(this.mob.getBbWidth() * 2.0F * this.mob.getBbWidth() * 2.0F + pAttackTarget.getBbWidth());
    }
}
