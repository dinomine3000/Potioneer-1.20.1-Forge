package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

import javax.annotation.Nullable;

public class LuckChangeEvent extends LivingEvent {
    private int oldLuck;
    private int newLuck;
    private boolean natural;

    public boolean isNatural() {
        return natural;
    }

    public int getNewLuck() {
        return newLuck;
    }

    public int getOldLuck() {
        return oldLuck;
    }

    public LuckChangeEvent(@Nullable LivingEntity entity, int oldLuck, int newLuck, boolean natural) {
        super(entity);
        this.oldLuck = oldLuck;
        this.newLuck = newLuck;
        this.natural = natural;
    }
}
