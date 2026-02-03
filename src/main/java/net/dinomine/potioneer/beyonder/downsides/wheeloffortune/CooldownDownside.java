package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;

public class CooldownDownside extends Downside {
    public CooldownDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_cooldown";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        List<AbilityKey> keys = cap.getAbilitiesManager().getAbilityKeys();
        int maxCount = 10 - getSequenceLevel();
        int maxTime = 20*(10-getSequenceLevel())*15;
        for(int i = 0; i < cap.getLuckManager().getRandomNumber(0, maxCount, false, target.getRandom()); i++){
            AbilityKey toCooldown = keys.get(target.getRandom().nextInt(keys.size()));
            cap.getAbilitiesManager().putAbilityOnCooldown(toCooldown, cap.getLuckManager().getRandomNumber(20, maxTime, false, target.getRandom()), target);
            keys.remove(toCooldown);
        }
        return true;
    }
}
