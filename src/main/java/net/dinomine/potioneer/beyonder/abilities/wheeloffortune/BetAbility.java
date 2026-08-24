package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.GamblingEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.world.entity.LivingEntity;

public class BetAbility extends Ability {
    private static final int cost = 0;
    @Override
    public void init() {
        defaultMaxCooldown = PotioneerAbilityConfig.BET_COOLDOWN.get();
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "bet";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost || target.level().isClientSide()) return false;
        cap.requestActiveSpiritualityCost(cost);
        int luck = cap.getLuckManager().getLuck();
        int minDuration = 25;
        int maxDuration = 300;
        int maxLevel = 1;
        float multiplier = 1f + Math.max(luck, 0)/150f;
        GamblingEffect.applyPositiveEffect(cap, target, cap.getLuckManager(), getSequenceLevel(), (int)(multiplier*minDuration), (int)(multiplier*maxDuration), (int)(multiplier*maxLevel), target.getRandom());
        cap.getLuckManager().consumeLuck(target, Math.max(luck, 100), false);
        return true;
    }
}
