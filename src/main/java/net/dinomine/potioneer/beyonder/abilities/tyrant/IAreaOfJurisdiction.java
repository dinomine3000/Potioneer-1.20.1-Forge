package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;

import java.util.ArrayList;
import java.util.List;

public interface IAreaOfJurisdiction {
    List<BlockPos> getCenters(String dimensionLocation);
    List<Integer> getSides(String dimensionLocation);

    default List<BlockPos> getCentersFromTag(CompoundTag tag, String dimensionKey) {
        List<BlockPos> centers = new ArrayList<>();
        List<String> dimensions = getDimensionsFromTag(tag);
        if (tag.contains("centers", Tag.TAG_LIST)) {
            ListTag centersTag = tag.getList("centers", Tag.TAG_COMPOUND);
            for (int i = 0; i < centersTag.size(); i++) {
                if(dimensions.size() > i && !dimensions.get(i).equalsIgnoreCase(dimensionKey)) continue;
                centers.add(NbtUtils.readBlockPos(centersTag.getCompound(i)));
            }
        }
        return centers;
    }

    default List<Integer> getSideFromTag(CompoundTag tag, String dimensionKey) {
        List<Integer> radii = new ArrayList<>();
        List<String> dimensions = getDimensionsFromTag(tag);
        if (tag.contains("sides", Tag.TAG_LIST)) {
            ListTag radiiTag = tag.getList("sides", Tag.TAG_INT);
            for (int i = 0; i < radiiTag.size(); i++) {
                if(dimensions.size() > i && !dimensions.get(i).equalsIgnoreCase(dimensionKey)) continue;
                radii.add(radiiTag.getInt(i));
            }
        }
        return radii;
    }

    default List<String> getDimensionsFromTag(CompoundTag tag) {
        List<String> dimensions = ModTags.readStringList(tag, "dimensions");
        return dimensions;
    }
    default CompoundTag getCompoundTag(List<BlockPos> centers, List<Integer> radii, List<String> dimensions){
        return getCompoundTag(new CompoundTag(), centers, radii, dimensions);
    }

    default CompoundTag getCompoundTag(CompoundTag resTag, List<BlockPos> centers, List<Integer> radii, List<String> dimensions){
        ListTag centersTag = new ListTag();
        for (BlockPos pos : centers) {
            centersTag.add(NbtUtils.writeBlockPos(pos));
        }
        resTag.put("centers", centersTag);

        ListTag radiiTag = new ListTag();
        for (int r : radii) {
            radiiTag.add(IntTag.valueOf(r));
        }
        resTag.put("sides", radiiTag);

        ModTags.writeStringList(resTag, "dimensions", dimensions);
        return resTag;
    }
}
