package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;

public abstract class AbilityPossessionEvent extends LivingEvent {
    private final Ability ability;

    public AbilityPossessionEvent(Ability abl, LivingEntity caster){
        super(caster);
        this.ability = abl;
    }

    public Ability getAbility(){
        return ability;
    }

    public static class Gained extends AbilityPossessionEvent {

        public Gained(Ability abl, LivingEntity caster) {
            super(abl, caster);
        }
    }

    public static class Lost extends AbilityPossessionEvent {

        public Lost(Ability abl, LivingEntity caster) {
            super(abl, caster);
        }
    }

}
