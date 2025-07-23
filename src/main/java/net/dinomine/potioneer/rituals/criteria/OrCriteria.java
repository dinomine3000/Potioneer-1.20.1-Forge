package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class OrCriteria extends ResponseCriteria{

    private List<ResponseCriteria> criteriaList;

    public OrCriteria(List<ResponseCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }
    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        for (ResponseCriteria responseCriteria : criteriaList) {
            if (responseCriteria.checkCondition(input, level)) return true;
        }
        return false;
    }

    @Override
    public CompoundTag saveToNBT() {
        ListTag listTag = new ListTag();
        for (ResponseCriteria criteria : criteriaList) {
            listTag.add(criteria.saveToNBT());
        }
        return envelopTag(listTag, "or_list");
    }

    public static OrCriteria getFromTag(Tag inputTag) throws IllegalArgumentException {
        if(!(inputTag instanceof ListTag listTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        List<ResponseCriteria> criteria = new ArrayList<>();
        for (Tag tag : listTag) {
            if (tag instanceof CompoundTag compoundTag) {
                criteria.add(ResponseCriteria.loadFromNBT(compoundTag));
            }
        }
        return new OrCriteria(criteria);
    }
}
