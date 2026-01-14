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
            cap.getEffectsManager().addOrReplaceEffect(getRandomEffectByPathway(pathwayId), cap, level.getPlayerByUUID(inputData.caster()));
        });
    }

    private BeyonderEffect getRandomEffectByPathway(int pathwayId) {
        BeyonderEffects.BeyonderEffectType effectId = null;
        switch (pathwayId){
            case 0:
                effectId = getRandomEffectByPrefix("WHEEL");
                break;
            case 1:
                effectId = getRandomEffectByPrefix("TYRANT");
                break;
            case 2:
                effectId = getRandomEffectByPrefix("MYSTERY");
                break;
            case 3:
                effectId = getRandomEffectByPrefix("RED");
                break;
            case 4:
                effectId = getRandomEffectByPrefix("PARAGON");
                break;
            default:
                effectId = getRandomEffectByPrefix("");
                break;
        }
        return BeyonderEffects.byId(effectId.getEffectId(), pathwayId%10, 0, 20*60, true);
    }


    private static BeyonderEffects.BeyonderEffectType getRandomEffectByPrefix(String prefix) {
        //TODO make this work
//        List<BeyonderEffects.BeyonderEffectType> matching = Arrays.stream(BeyonderEffects.EFFECT.values())
//                .filter(e -> e.name().startsWith(prefix))
//                .collect(Collectors.toList());
//
//        if (matching.isEmpty()) return null; // Or throw an exception

        return BeyonderEffects.PARAGON_REGEN;
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pathwayId", pathwayId);
        tag.putBoolean("targetCaster", targetCaster);
        return envelopTag(tag, "aid");
    }

    public static AidResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        int pathwayId = compoundTag.getInt("pathwayId");
        boolean targetCaster = compoundTag.getBoolean("targetCaster");
        return new AidResponse(pathwayId, targetCaster);
    }

    @Override
    public AidResponse getRandom() {
        Random random = new Random();
        return new AidResponse(random.nextInt(0, 5), random.nextBoolean());
    }
}
