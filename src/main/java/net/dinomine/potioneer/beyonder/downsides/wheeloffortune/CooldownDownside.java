package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class CooldownDownside extends Downside {

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "d_cooldown";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        List<Ability> keys = cap.getAbilitiesManager().getAllAbilities();
        int maxCount = 10 - getSequenceLevel();
        int maxTime = 20*(10-getSequenceLevel())*15;
        for(int i = 0; i < cap.getLuckManager().getRandomNumber(0, maxCount, false, target.getRandom()); i++){
            Ability toCooldown = keys.get(target.getRandom().nextInt(keys.size()));
            cap.getAbilitiesManager().putAbilityOnCooldown(toCooldown.getInstanceId(), cap.getLuckManager().getRandomNumber(20, maxTime, false, target.getRandom()), target);
            keys.remove(toCooldown);
        }
        return true;
    }
}
