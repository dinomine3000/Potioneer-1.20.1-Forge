package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class DummyAbility extends Ability{

    public DummyAbility(int sequence, boolean enabled){
        this.sequence = sequence;
        this.enabled = enabled;
        this.info = new AbilityInfo(48, 0, "dummy");
    }

    @Override
    public void active(EntityBeyonderManager cap, LivingEntity target) {
        target.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 100, 1, false, false));
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {

    }
}
