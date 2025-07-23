package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;

public class ActionCriteria extends ResponseCriteria{

    private final String action;

    public ActionCriteria(String action) {
        this.action = action.toLowerCase();
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return input.thirdVerse().toLowerCase().contains(action);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("action", action);
        return tag;
    }

    public static ActionCriteria loadFromNBT(CompoundTag tag) {
        String action = tag.getString("action");
        return new ActionCriteria(action);
    }
}
