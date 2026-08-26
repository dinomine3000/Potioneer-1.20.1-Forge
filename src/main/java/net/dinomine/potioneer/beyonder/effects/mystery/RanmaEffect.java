package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.DisabledAbilitiesManager;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.server.ServerEffectVisualHandling;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;

public class RanmaEffect extends BeyonderEffect {
    public static final int RANMA_RADIUS = 8;
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target, AcquireType acquireType) {
        if(!target.level().isClientSide()) ServerEffectVisualHandling.addRanmaEntity(target);
        if(acquireType != AcquireType.ADDED) return;
        cap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("ranma", DisabledAbilitiesManager.DisabledAbilityProxy.all(-1, Abilities.RANMA.get().getAblId()), cap, target);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(cost);
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        cap.getAbilitiesManager().getDisabledAbilitiesManager().enableAbility("ranma", cap, target);
        if(!target.level().isClientSide()) ServerEffectVisualHandling.removeRanmaEntity(target);
    }
}
