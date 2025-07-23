package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public abstract class ResponseCriteria {

    public abstract boolean checkCondition(RitualInputData input, Level level);

    public abstract Tag saveToNBT();

    protected CompoundTag envelopTag(Tag tag, String id){
        CompoundTag res = new CompoundTag();
        res.put("information", tag);
        res.putString("type", id);
        return res;
    }

    public static ResponseCriteria loadFromNBT(CompoundTag tag) throws IllegalArgumentException {
        String type = tag.getString("type");
        Tag infoTag = tag.get("information");
        return switch (type) {
            case "or_list" -> OrCriteria.getFromTag(tag);
            case "and_list" -> AndCriteria.getFromTag(tag);
            case "sequence_level" -> SequenceLevelCriteria.getFromTag(infoTag);
            case "pathway_id" -> PathwayCriteria.getFromTag(infoTag);
            case "offerings" -> OfferingsCriteria.getFromTag(infoTag);
            // case "some_other" -> SomeOtherCriteria.loadFromNBT(tag);
            default -> throw new IllegalArgumentException("Unknown criteria type: " + type);
        };
    }
}
