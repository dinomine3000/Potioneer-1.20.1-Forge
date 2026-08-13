package net.dinomine.potioneer.beyonder.downsides.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraDownsideEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraRecipientEffect;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraSourceEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class AuraDownside extends Downside {
    public AuraDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(target.tickCount%20 != target.getId()%20) return;
        if(cap.getLuckManager().passesLuckCheck(0.9f, 0, 0, target.getRandom())) return;

        List<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, 16);
        for(LivingEntity hit: hits){
            if(target.is(hit)) continue;
            AuraDownsideEffect eff = (AuraDownsideEffect) BeyonderEffects.TYRANT_AURA_DOWNSIDE.createInstance(sequenceLevel, 0, 20*20, true);
            eff.setup(hit.getId());
            cap.getEffectsManager().addOrReplaceEffect(eff, cap, target);
            break;
        }
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_aura";
    }
}
