package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.tyrant.ExiledEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class ExileAbility extends Ability {
    public static final int EXILE_DURATION = 20*30;
    public ExileAbility(int sequenceLevel) {
        super(sequenceLevel);
        defaultMaxCooldown = 20*10;
        withCost(80);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "exile";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity caster) {
        if(cap.getSpirituality() < cost()) return false;
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, 5, 1);
        if(target == null) return false;
        if(caster.level().isClientSide()) return true;
        ExiledEffect eff = ExiledEffect.getInstance(caster.getUUID(), sequenceLevel);
        LivingEntityBeyonderCapability targetCap = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();
        targetCap.getEffectsManager().addOrRefreshEffect(eff, targetCap, target);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }
}
