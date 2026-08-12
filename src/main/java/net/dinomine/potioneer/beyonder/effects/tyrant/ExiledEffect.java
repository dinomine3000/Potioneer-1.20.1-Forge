package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.AbstractSourceRecipientEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.UUID;

public class ExiledEffect extends AbstractSourceRecipientEffect {
    private static final float PUSH_FORCE = 0.5f;
    private int timeInAoj = 20*5;
    public static ExiledEffect getInstance(UUID enforcerId, int sequenceLevel){
        //we limit sequence level to 2 plateaus: before and after sequence 5.
        //at sequence 6 it only pushes them away
        //at sequence 5, itll teleport the person into a restricted zone instead. so we replace the effect that existed before
        ExiledEffect eff = (ExiledEffect) BeyonderEffects.TYRANT_EXILE.createInstance(sequenceLevel < 6 ? 0 : 9, 0, -1, true);
        eff.setEnforcer(enforcerId);
        return eff;
    }

    private void setEnforcer(UUID enforcer){
        this.sources.put(enforcer, PotioneerAbilityConfig.EXILE_DURATION.get());
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!(target.level() instanceof ServerLevel serverLevel)) return;
        tickDownTime(target);
        for(UUID id: this.sources.keySet()){
            Entity enforcer = AbilityFunctionHelper.getEntityAcrossDimensions(serverLevel, id);
            if(!AreaOfJurisdictionAbility.isEntityInAOJ(target, enforcer)) return;
            addForce(target, enforcer);
        }
    }

    private static void addForce(LivingEntity target, Entity enforcer){
        //1. find closest center
        List<BlockPos> centers = AreaOfJurisdictionAbility.getCentersOfEnforcer(enforcer, target.level().dimension());
        if(centers.isEmpty()) return;
        BlockPos bestMatch = null;
        double bestDist = Float.MAX_VALUE;
        for(BlockPos pos: centers){
            double iDist = pos.distSqr(target.getOnPos());
            if(iDist < bestDist){
                bestMatch = pos;
                bestDist = iDist;
            }
        }

        //2. shoo them the other way.
        assert bestMatch != null;
        Vec3 force = target.getOnPos().getCenter().subtract(bestMatch.getCenter()).normalize().scale(PUSH_FORCE);
        AbilityFunctionHelper.pushEntity(target, force.with(Direction.Axis.Y, 0));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
