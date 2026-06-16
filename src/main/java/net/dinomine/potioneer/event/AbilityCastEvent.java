package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingConversionEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

public abstract class AbilityCastEvent extends LivingEvent {
    private final Ability ability;
    private final boolean primary;
    private final CompoundTag args;

    public AbilityCastEvent(Ability abl, LivingEntity caster, boolean primary, CompoundTag args){
        super(caster);
        this.ability = abl;
        this.primary = primary;
        this.args = args;
    }

    public CompoundTag getArguments(){return args;}

    public Ability getAbility(){
        return ability;
    }

    public AbilityInfo getAbilityInfo(){
        return ability.getAbilityInfo();
    }

    public boolean wasPrimary(){
        return primary;
    }

    @Cancelable
    public static class Pre extends AbilityCastEvent {

        public Pre(Ability abl, LivingEntity caster, boolean primary, CompoundTag args) {
            super(abl, caster, primary, args);
        }
    }

    public static class Post extends AbilityCastEvent {

        public Post(Ability abl, LivingEntity caster, boolean primary, CompoundTag args) {
            super(abl, caster, primary, args);
        }
    }

}
