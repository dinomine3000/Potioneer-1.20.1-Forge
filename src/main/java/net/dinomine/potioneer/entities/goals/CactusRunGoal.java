package net.dinomine.potioneer.entities.goals;

import net.dinomine.potioneer.entities.custom.WanderingCactusEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;

public class CactusRunGoal<T extends LivingEntity> extends AvoidEntityGoal<T> {

    public CactusRunGoal(WanderingCactusEntity pMob, Class<T> pEntityClassToAvoid, float pMaxDistance, double pWalkSpeedModifier, double pSprintSpeedModifier) {
        super(pMob, pEntityClassToAvoid, pMaxDistance, pWalkSpeedModifier, pSprintSpeedModifier);
    }

    @Override
    public boolean canUse() {
        if(((WanderingCactusEntity) mob).isRunning()) return super.canUse();
        return false;
    }

    @Override
    public boolean canContinueToUse() {
        if(((WanderingCactusEntity) mob).isRunning()) return super.canContinueToUse();
        return false;
    }
}
