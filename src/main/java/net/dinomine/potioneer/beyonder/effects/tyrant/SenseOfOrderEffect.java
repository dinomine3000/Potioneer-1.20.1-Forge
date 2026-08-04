package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.tyrant.AreaOfJurisdictionAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.player.Player;

import java.util.Optional;
import java.util.function.Supplier;

import static net.dinomine.potioneer.config.PotioneerAbilityConfig.SENSE_OF_ORDER_RADIUS;

public class SenseOfOrderEffect extends BeyonderEffect {
    private static final Supplier<Integer> orderRadius = SENSE_OF_ORDER_RADIUS;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || !(target instanceof Player player)) return;
        cap.requestPassiveSpiritualityCost(cost);
        if(target.tickCount%20 != target.getId()%20) return;
        target.level().getEntities(target, target.getBoundingBox().inflate(orderRadius.get())).forEach(ent -> makeVisible(ent, target));
    }

    private static void makeVisible(Entity entity, LivingEntity enforcer){
        if(!(entity instanceof LivingEntity livingEntity)) return;
        if(AbilityFunctionHelper.areEntitiesAllies(livingEntity, enforcer)) return;
        Optional<LivingEntityBeyonderCapability> optCapEnforcer = enforcer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(optCapEnforcer.isEmpty()) return;
        boolean aoj = AreaOfJurisdictionAbility.isEntityInAOJ(livingEntity, enforcer);
        if(livingEntity.getMobType() == MobType.UNDEAD || aoj) livingEntity.addEffect(new MobEffectInstance(MobEffects.GLOWING, 40, 0, false, false, true));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
