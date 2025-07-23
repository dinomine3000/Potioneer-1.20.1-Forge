package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

import static net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse.getPlayer;

public class NegativeEffectResponse extends SpiritResponse implements RandomizableResponse<NegativeEffectResponse> {
    private final boolean targetCaster;
    private final int effectId;

    public NegativeEffectResponse(boolean targetCaster, int effectId){
        this.targetCaster = targetCaster;
        this.effectId = effectId;
    }
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        RandomSource random = level.getRandom();
        System.out.println("Giving negative effect");
        Player player = getPlayer(inputData, level, targetCaster);
        if(player == null) return;
        player.addEffect(new MobEffectInstance(MobEffect.byId(effectId), random.nextIntBetweenInclusive(20*30, 20*120), random.nextInt(2, 5)));
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("targetCaster", this.targetCaster);
        tag.putInt("effectId", this.effectId);
        return envelopTag(tag, "negative_effect");
    }

    public static NegativeEffectResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        return new NegativeEffectResponse(compoundTag.getBoolean("targetCaster"), compoundTag.getInt("effectId"));
    }

    @Override
    public NegativeEffectResponse getRandom() {
        Random random = new Random();
        int choice = random.nextInt(6);
        int id = switch(choice) {
            case 0 -> MobEffect.getId(MobEffects.POISON);
            case 1 -> MobEffect.getId(MobEffects.DIG_SLOWDOWN);
            case 2 -> MobEffect.getId(MobEffects.HUNGER);
            case 3 -> MobEffect.getId(MobEffects.BLINDNESS);
            case 4 -> MobEffect.getId(MobEffects.WITHER);
            case 5 -> MobEffect.getId(MobEffects.MOVEMENT_SLOWDOWN);
            default -> MobEffect.getId(MobEffects.WEAKNESS);
        };
        return new NegativeEffectResponse(random.nextBoolean(), id);
    }
}
