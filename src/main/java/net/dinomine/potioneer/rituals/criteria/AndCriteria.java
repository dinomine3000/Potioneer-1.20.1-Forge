package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

public class AndCriteria extends ResponseCriteria{

    private List<ResponseCriteria> criteriaList;

    public AndCriteria(List<ResponseCriteria> criteriaList) {
        this.criteriaList = criteriaList;
    }
    @Override
    public boolean checkCondition(RitualInputData input) {
        for (ResponseCriteria responseCriteria : criteriaList) {
            if (!responseCriteria.checkCondition(input)) return false;
        }
        return true;
    }

    @Override
    public CompoundTag saveToNBT() {
        ListTag listTag = new ListTag();
        for (ResponseCriteria criteria : criteriaList) {
            listTag.add(criteria.saveToNBT());
        }
        return envelopTag(listTag, "and_list");
    }

    public static AndCriteria getFromTag(Tag inputTag) throws IllegalArgumentException {
        if(!(inputTag instanceof ListTag listTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        List<ResponseCriteria> criteria = new ArrayList<>();
        for (Tag tag : listTag) {
            if (tag instanceof CompoundTag compoundTag) {
                criteria.add(ResponseCriteria.loadFromNBT(compoundTag));
            }
        }
        return new AndCriteria(criteria);
    }
}
