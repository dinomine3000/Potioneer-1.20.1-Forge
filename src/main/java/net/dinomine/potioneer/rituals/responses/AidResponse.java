package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class AidResponse extends SpiritResponse{

    private final int pathwayId;
    private final boolean targetCaster;

    public AidResponse(int pathwayId, boolean targetCaster) {
        this.pathwayId = pathwayId;
        this.targetCaster = targetCaster;
    }

    @Override
    public void enactResponse(RitualInputData inputData) {
        LivingEntity effectTarget = inputData.target();
        if(targetCaster){
            effectTarget = inputData.caster();
        }
        inputData.caster().getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.getEffectsManager().addOrReplaceEffect(getRandomEffectByPathway(pathwayId), cap, inputData.caster());
        });
    }

    private BeyonderEffect getRandomEffectByPathway(int pathwayId) {
        BeyonderEffects.EFFECT effectId = null;
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
        }
        return BeyonderEffects.byId(effectId, pathwayId%10, 0, 20*60, true);
    }


    private static BeyonderEffects.EFFECT getRandomEffectByPrefix(String prefix) {
        List<BeyonderEffects.EFFECT> matching = Arrays.stream(BeyonderEffects.EFFECT.values())
                .filter(e -> e.name().startsWith(prefix))
                .collect(Collectors.toList());

        if (matching.isEmpty()) return null; // Or throw an exception

        return matching.get(new Random().nextInt(matching.size()));
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("pathwayId", pathwayId);
        tag.putBoolean("targetCaster", targetCaster);
        return envelopTag(tag, "pathway_id");
    }

    public static AidResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        int pathwayId = compoundTag.getInt("pathwayId");
        boolean targetCaster = compoundTag.getBoolean("targetCaster");
        return new AidResponse(pathwayId, targetCaster);
    }
}
