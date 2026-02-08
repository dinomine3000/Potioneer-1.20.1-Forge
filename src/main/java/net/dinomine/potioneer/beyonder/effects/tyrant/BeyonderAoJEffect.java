package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.IAreaOfJurisdiction;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class BeyonderAoJEffect extends BeyonderEffect {
    private static final int proximityRadius = 32;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()){
            if(cap.getAbilitiesManager().isEnabledAtLevelOrLower(Abilities.AOJ.getAblId(), getSequenceLevel()) && target.tickCount%20 == 0){
                List<BlockPos> centers = new ArrayList<>();
                List<Integer> radii = new ArrayList<>();
                for(Ability abl: cap.getAbilitiesManager().getAbilities()){
                    if(abl instanceof IAreaOfJurisdiction aojAbl){
                        centers.addAll(aojAbl.getCenters());
                        radii.addAll(aojAbl.getRadius());
                    }
                }
                if(!centers.isEmpty())
                    ParticleMaker.createAreaOfJurisdiction(target.level(), (int)(target.getY()), centers, radii);
            }
        } else {
            if(target.tickCount%20 == target.getId()%20){
                AllySystemSaveData allySystemSaveData = AllySystemSaveData.from((ServerLevel) target.level());
                target.level().getEntities(target,
                        new AABB(target.getOnPos().offset(-proximityRadius, 0, -proximityRadius).atY(-500), target.getOnPos().offset(proximityRadius, 0, proximityRadius).atY(500)))
                        .forEach( entity -> applyAojInfluenceToEntity(entity, target, allySystemSaveData, cap));
            }
        }
    }

    private static void applyAojInfluenceToEntity(Entity entity, LivingEntity enforcer, AllySystemSaveData allyData, LivingEntityBeyonderCapability cap){
        if(entity instanceof LivingEntity livingEntity && !allyData.areEntitiesAllies(livingEntity, enforcer) && AreaOfJurisdictionAbility.isPosInAOJ(livingEntity.getOnPos(), cap, 0)){
            livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap -> {
                victimCap.getEffectsManager().addOrReplaceEffect(BeyonderAoJInfluenceEffect.getInstance(enforcer.getUUID()),
                        victimCap, livingEntity);
            });
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
