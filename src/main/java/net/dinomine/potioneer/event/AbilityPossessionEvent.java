package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public abstract class AbilityPossessionEvent extends LivingEvent {
    private final Ability ability;
    private final AbilityKey abilityKey;

    public AbilityPossessionEvent(Ability abl, AbilityKey ablkey, LivingEntity caster){
        super(caster);
        this.ability = abl;
        this.abilityKey = ablkey;
    }

    public Ability getAbility(){
        return ability;
    }

    public AbilityKey getAbilityKey(){
        return abilityKey;
    }

    public static class Gained extends AbilityPossessionEvent {

        public Gained(Ability abl, AbilityKey key, LivingEntity caster) {
            super(abl, key, caster);
        }
    }

    public static class Lost extends AbilityPossessionEvent {

        public Lost(Ability abl, AbilityKey key, LivingEntity caster) {
            super(abl, key, caster);
        }
    }

}
