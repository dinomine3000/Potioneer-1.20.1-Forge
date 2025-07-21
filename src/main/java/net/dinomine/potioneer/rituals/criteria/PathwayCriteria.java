package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class PathwayCriteria extends ResponseCriteria{

    private final int pathwayId;

    public PathwayCriteria(int pathwayId) {
        this.pathwayId = pathwayId;
    }

    @Override
    public boolean checkCondition(RitualInputData input) {
        return Math.floorDiv(input.pathwayId(), 10) == pathwayId;
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pathwayId", pathwayId);
        return envelopTag(tag, "pathway_id");
    }

    public static PathwayCriteria getFromTag(Tag tag) {
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        int pathwayId = compoundTag.getInt("pathwayId");
        return new PathwayCriteria(pathwayId);
    }
}
