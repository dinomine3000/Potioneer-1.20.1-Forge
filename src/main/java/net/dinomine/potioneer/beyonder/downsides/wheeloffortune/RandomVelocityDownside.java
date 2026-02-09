package net.dinomine.potioneer.beyonder.downsides.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.VelocityAbility;
import net.dinomine.potioneer.beyonder.downsides.Downside;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.VelocityEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class RandomVelocityDownside extends Downside {
    public RandomVelocityDownside(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(cap.getLuckManager().getRandomNumber(0, 3600, true, target.getRandom()) == 0){
            VelocityEffect effect = (VelocityEffect) BeyonderEffects.WHEEL_VELOCITY.createInstance(getSequenceLevel(), 0,
                    cap.getLuckManager().getRandomNumber(20*10, 20*120, false, target.getRandom()), true);
            effect.attackSpeed = cap.getLuckManager().getRandomNumber(1, VelocityAbility.levelToMaxAttack.apply(getSequenceLevel()), false, target.getRandom());
            effect.movementSpeed = cap.getLuckManager().getRandomNumber(1, VelocityAbility.levelToMaxMovement.apply(getSequenceLevel()), false, target.getRandom());
            cap.getEffectsManager().addOrReplaceEffect(effect, cap, target);
        }
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "d_velocity";
    }
}
