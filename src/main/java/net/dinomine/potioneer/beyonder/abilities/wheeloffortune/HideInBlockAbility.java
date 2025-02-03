package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class HideInBlockAbility extends Ability {

    public HideInBlockAbility(int sequence){
        this.info = new AbilityInfo(5, 56, "Hide in Block", sequence, 20, 20);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        flipEnable(cap, target);
        return !isEnabled(cap.getAbilitiesManager());
        //this ability should:
        //make player unable to move - set speed to 0
        //make the player have no collision box - delete it and reset it
        //make the player invisible - mob effect
        //make the player invulnerable - make a beyonder effect and check for it on taking damage, canceling event
        //make the player untargetable by entities - subscribe to entity change target event
        //last X amount of time
        //should also require person to be on ground OR looking at a target block, changing the player position to the inside of the block
        //once the player leaves they should appear in their original spot
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        activate(cap, target);
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
        cap.getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.WHEEL_FORTUNE, getSequence(), 0, -1, true),
                cap, target);
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE)){
            cap.getEffectsManager().removeEffect(BeyonderEffects.EFFECT.WHEEL_FORTUNE,
                    getSequence(), cap, target);
        }
    }
}
