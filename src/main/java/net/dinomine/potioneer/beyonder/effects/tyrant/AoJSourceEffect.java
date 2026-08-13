package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.IAreaOfJurisdiction;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static net.dinomine.potioneer.config.PotioneerAbilityConfig.AOJ_RADIUS;

public class AoJSourceEffect extends BeyonderEffect {
    private static final Supplier<Integer> RADIUS = AOJ_RADIUS;
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(AreaOfJurisdictionAbility.isEntityInAOJ(target, target)) cap.getEffectsManager().statsHolder.addRegeneration(1);
        if(target.tickCount%20 == target.getId()%20){
            target.level().getEntities(target,
                            new AABB(target.getOnPos().offset(-RADIUS.get(), 0, -RADIUS.get()).atY(-500), target.getOnPos().offset(RADIUS.get(), 0, RADIUS.get()).atY(500)))
                    .forEach( entity -> applyAojInfluenceToEntity(entity, target));
        }
    }

    private static void applyAojInfluenceToEntity(Entity entity, LivingEntity enforcer){
        if(entity instanceof LivingEntity livingEntity && !AbilityFunctionHelper.areEntitiesAllies(livingEntity, enforcer) && AreaOfJurisdictionAbility.isEntityInAOJ(livingEntity, enforcer)){
            livingEntity.getCapability(CapProvider.BEYONDER_STATS).ifPresent(victimCap -> {
                victimCap.getEffectsManager().addOrRefreshEffect(AoJRecipientEffect.getInstance(enforcer.getUUID()),
                        victimCap, livingEntity);
            });
        }
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }
}
