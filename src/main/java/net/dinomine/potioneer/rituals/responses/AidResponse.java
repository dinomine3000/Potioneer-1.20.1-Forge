package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

import static net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse.getPlayer;

public class AidResponse extends SpiritResponse implements RandomizableResponse<AidResponse> {

    private final int pathwayId;
    private final boolean targetCaster;

    public AidResponse(int pathwayId, boolean targetCaster) {
        this.pathwayId = Math.floorDiv(pathwayId, 10);
        this.targetCaster = targetCaster;
    }

    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        Player effectTarget = getPlayer(inputData, level, targetCaster);
        if(effectTarget == null) return;
        effectTarget.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().addOrReplaceEffect(getRandomEffectByPathway(pathwayId), cap, effectTarget);
        });
    }

    private BeyonderEffect getRandomEffectByPathway(int pathwayId) {
        BeyonderEffects.BeyonderEffectType effectId = switch (pathwayId) {
            case 0 -> getRandomEffectByPrefix("WHEEL");
            case 1 -> getRandomEffectByPrefix("TYRANT");
            case 2 -> getRandomEffectByPrefix("MYSTERY");
            case 3 -> getRandomEffectByPrefix("RED");
            case 4 -> getRandomEffectByPrefix("PARAGON");
            default -> getRandomEffectByPrefix("");
        };
        if(effectId == null) return null;
        return BeyonderEffects.byId(effectId.getEffectId(), pathwayId%10, 0, 20*60, true);
    }


    private static BeyonderEffects.BeyonderEffectType getRandomEffectByPrefix(String prefix) {
//        List<BeyonderEffects.BeyonderEffectType> matching = Arrays.stream(BeyonderEffects.EFFECT.values())
//                .filter(e -> e.name().startsWith(prefix))
//                .collect(Collectors.toList());
//
//        if (matching.isEmpty()) return null; // Or throw an exception

        return BeyonderEffects.getRandomEffect(prefix);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pathwaySequenceId", pathwayId);
        tag.putBoolean("targetCaster", targetCaster);
        return envelopTag(tag, "aid");
    }

    public static AidResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        int pathwayId = compoundTag.getInt("pathwaySequenceId");
        boolean targetCaster = compoundTag.getBoolean("targetCaster");
        return new AidResponse(pathwayId, targetCaster);
    }

    @Override
    public AidResponse getRandom() {
        Random random = new Random();
        return new AidResponse(random.nextInt(0, 5), random.nextBoolean());
    }
}
