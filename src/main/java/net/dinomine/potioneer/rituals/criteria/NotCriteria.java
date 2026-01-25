package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class NotCriteria extends ResponseCriteria{

    private ResponseCriteria criteria;

    public NotCriteria(ResponseCriteria criteriaList) {
        this.criteria = criteriaList;
    }
    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return !criteria.checkCondition(input, level);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("criteria", criteria.saveToNBT());
        return envelopTag(tag, "not");
    }

    public static NotCriteria getFromTag(Tag inputTag) throws IllegalArgumentException {
        if(!(inputTag instanceof CompoundTag tag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        ResponseCriteria criteria1 = ResponseCriteria.loadFromNBT(tag.getCompound("criteria"));
        return new NotCriteria(criteria1);
    }
}
