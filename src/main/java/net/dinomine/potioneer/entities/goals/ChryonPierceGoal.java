package net.dinomine.potioneer.entities.goals;

import net.dinomine.potioneer.entities.custom.ChryonEntity;
import net.dinomine.potioneer.registry.DamageTypesRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;

public class ChryonPierceGoal extends MeleeAttackGoal {
    protected final ChryonEntity mob;


    public ChryonPierceGoal(ChryonEntity pMob, double pSpeedModifier) {
        super(pMob, pSpeedModifier, false);
        mob = pMob;
    }

    @Override
    protected void checkAndPerformAttack(LivingEntity pEnemy, double dist) {
        double reach = this.getAttackReachSqr(pEnemy);
        if (dist <= reach && mob.canPierce()) {
            if(!mob.isPiercing()){
                mob.setPiercing(true);
            }
        }
    }
}
