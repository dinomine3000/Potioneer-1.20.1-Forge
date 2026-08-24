package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.misc.BeyonderCogitationEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class CogitationAbility extends PassiveAbility {
    public CogitationAbility(){
        super(BeyonderEffects.COGITATION, integer -> "cogitation");
        canFlip();
    }

    @Override
    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target) {
        BeyonderCogitationEffect eff = (BeyonderCogitationEffect) BeyonderEffects.COGITATION.createInstance(getSequenceLevel(), 0, -1, true);
        eff.ablId = getAbilityId();
        return eff;
    }
}
