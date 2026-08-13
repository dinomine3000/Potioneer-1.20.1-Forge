package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.config.PotioneerAbilityConfig;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;
import java.util.function.Supplier;

public class BribeSourceEffect extends BeyonderEffect {
    private String type = "";
    public static final Supplier<Integer> BRIBE_DURATION = PotioneerAbilityConfig.BRIBE_DURATION;

    public void setup(String type){this.type = type;}
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    public BribeRecipientEffect createRecipientEffect(UUID ownerId) {
        BribeRecipientEffect eff = (BribeRecipientEffect) BeyonderEffects.TYRANT_BRIBE_RECIPIENT.createInstance(sequenceLevel, 0, BRIBE_DURATION.get(), true);
        eff.type = type;
        eff.ownerId = ownerId;
        return eff;
    }
}
