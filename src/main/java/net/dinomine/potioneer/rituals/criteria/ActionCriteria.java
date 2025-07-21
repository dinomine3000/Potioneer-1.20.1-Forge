package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;

public class ActionCriteria extends ResponseCriteria{

    private final RitualInputData.ACTION action;

    public ActionCriteria(RitualInputData.ACTION action) {
        this.action = action;
    }

    @Override
    public boolean checkCondition(RitualInputData input) {
        return input.action().equals(action);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("action", action.toString());
        return tag;
    }

    public static ActionCriteria loadFromNBT(CompoundTag tag) {
        RitualInputData.ACTION action = RitualInputData.ACTION.valueOf(tag.getString("action"));
        return new ActionCriteria(action);
    }
}
