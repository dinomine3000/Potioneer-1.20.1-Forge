package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public abstract class SpiritResponse {
    public abstract void enactResponse(RitualInputData inputData, Level level);
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
            case "aid" -> AidResponse.getFromTag(infoTag);
            case "hurt" -> HurtResponse.getFromTag(infoTag);
            case "negative_effect" -> NegativeEffectResponse.getFromTag(infoTag);
            case "consumeSpirituality" -> SpiritualityConsumeResponse.getFromTag(infoTag);
            case "nothing" -> new NothingResponse();
            case "player" -> new PlayerResponse();
            case "summoning" -> SummonResponse.loadFromNbt(infoTag);
            // case "some_other" -> SomeOtherCriteria.loadFromNBT(tag);
            default -> throw new IllegalArgumentException("Unknown criteria type: " + type);
        };
    }
}
