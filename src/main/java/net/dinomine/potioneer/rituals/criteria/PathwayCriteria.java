package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RandomizableCriteria;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.Random;

public class PathwayCriteria extends ResponseCriteria implements RandomizableCriteria<PathwayCriteria> {

    private final int pathwayId;

    public PathwayCriteria(int pathwayId) {
        this.pathwayId = pathwayId;
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return Math.floorDiv(input.pathwaySequenceId(), 10) == pathwayId;
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

    @Override
    public PathwayCriteria getRandom() {
        Random random = new Random();
        return new PathwayCriteria(random.nextInt(5));
    }
}
