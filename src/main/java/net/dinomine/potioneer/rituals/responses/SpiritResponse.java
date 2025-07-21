package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.criteria.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public abstract class SpiritResponse {
    public abstract void enactResponse(RitualInputData inputData);
    public abstract CompoundTag saveToNBT();
    protected CompoundTag envelopTag(Tag tag, String id){
        CompoundTag res = new CompoundTag();
        res.put("information", tag);
        res.putString("type", id);
        return res;
    }

    public static SpiritResponse loadFromNBT(CompoundTag tag) throws IllegalArgumentException {
        String type = tag.getString("type");
        Tag infoTag = tag.get("information");
        return switch (type) {
            case "some_other" -> AidResponse.getFromTag(infoTag);
            // case "some_other" -> SomeOtherCriteria.loadFromNBT(tag);
            default -> throw new IllegalArgumentException("Unknown criteria type: " + type);
        };
    }
}
