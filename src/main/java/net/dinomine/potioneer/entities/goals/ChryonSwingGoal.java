package net.dinomine.potioneer.entities.goals;

import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ChryonSwingGoal extends MeleeAttackGoal {
    protected final ChryonEntity mob;

    public ChryonSwingGoal(ChryonEntity pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier, false);
        mob = pMob;
    }


    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double dist) {
        double reach = this.getAttackReachSqr(pEnemy);
        if (dist <= reach && mob.canSwing()) {
            if(!mob.isSwinging()){
                mob.setSwinging(true);
            }
        }
    }
}
