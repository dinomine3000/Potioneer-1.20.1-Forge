package net.dinomine.potioneer.entities.goals;

import net.dinomine.potioneer.entities.custom.WanderingCactusEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.util.DefaultRandomPos;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CactusWanderGoal extends WaterAvoidingRandomStrollGoal {
    protected final WanderingCactusEntity mob;

    public CactusWanderGoal(WanderingCactusEntity pMob) {
        super(pMob, 1f);
        setInterval(20);
        mob = pMob;
    }

    @Nullable
    @Override
    protected Vec3 getPosition() {
        return DefaultRandomPos.getPos(this.mob, 32, 7);
    }

    @Override
    public boolean canUse() {
        if(mob.isWandering()) return super.canUse();
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if(mob.isWandering()) return super.canContinueToUse();
        return false;
    }
}
