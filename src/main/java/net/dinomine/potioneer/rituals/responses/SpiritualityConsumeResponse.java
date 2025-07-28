package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.savedata.RitualSpiritsSaveData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import static net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse.getPlayer;
import static net.dinomine.potioneer.savedata.RitualSpiritsSaveData.SpiritHelper.randomResponses;

public class SpiritualityConsumeResponse extends SpiritResponse implements RandomizableResponse {

    private final SpiritResponse defaultResponse;
    private final int minSpir;
    private final int maxSpir;

    public SpiritualityConsumeResponse(SpiritResponse defaultResponse, int minSpir, int maxSpir){
        this.defaultResponse = defaultResponse;
        this.minSpir = minSpir;
        this.maxSpir = maxSpir;
    }

    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        Player player = getPlayer(inputData, level, true);
        if(player != null){
            player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
                cap.requestActiveSpiritualityCost(level.random.nextInt(minSpir, maxSpir));
            });
            defaultResponse.enactResponse(inputData, level);
        }
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.put("otherResponse", defaultResponse.saveToNBT());
        tag.putInt("min", minSpir);
        tag.putInt("max", maxSpir);
        return envelopTag(tag, "consumeSpirituality");
    }

    public static SpiritualityConsumeResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        SpiritResponse defaultResponse = SpiritResponse.loadFromNBT(compoundTag.getCompound("otherResponse"));
        int min = compoundTag.getInt("min");
        int max = compoundTag.getInt("max");
        return new SpiritualityConsumeResponse(defaultResponse, min, max);
    }

    @Override
    public SpiritResponse getRandom() {
        SpiritResponse resp = RitualSpiritsSaveData.getRandomSample(randomResponses, 1).stream().toList().get(0).get();
        return new SpiritualityConsumeResponse(null, 1, 2);
    }
}
