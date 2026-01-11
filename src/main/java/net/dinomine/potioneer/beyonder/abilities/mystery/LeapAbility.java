package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class LeapAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "leap";
    }

    public LeapAbility(int sequence){
//        this.info = new AbilityInfo(57, 200, "Leap", 20 + sequence, 33 - 3*(9-sequence), getMaxCooldown(), "leap");
        super(sequence);
        setCost(level -> 33 - 3*(9-level));
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        Vec3 look = target.getLookAngle();
        double mult = 1 + 1.2*(9-getSequenceLevel());
        if(target instanceof Player player){
            player.push(look.x*mult, look.y*mult/2f, look.z*mult);
            player.hurtMarked = true;
        } else target.addDeltaMovement(look.multiply(mult, mult/2, mult));
//        if(target instanceof Player player && !(player.isCreative() || player.isSpectator())) {
//            cap.getEffectsManager().addEffectNoNotify(BeyonderEffects.byId(BeyonderEffects.EFFECT.MYSTERY_FALL_NEGATE, getSequence(), 0, -1, true));
//        }
        if(!target.level().isClientSide()){
            cap.requestActiveSpiritualityCost(cost());
        }
        return true;
    }
}
