package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Random;

import static net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse.getPlayer;

public class HurtResponse extends SpiritResponse implements RandomizableResponse<HurtResponse> {
    private final boolean targetCaster;
    private final int damage;

    public HurtResponse(boolean targetCaster, int damage){
        this.targetCaster = targetCaster;
        this.damage = damage;
    }
    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        Player player = getPlayer(inputData, level, targetCaster);
        if(player == null) return;
        player.hurt(player.damageSources().magic(), this.damage);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("damage", this.damage);
        tag.putBoolean("targetCaster", this.targetCaster);
        return envelopTag(tag, "hurt");
    }

    public static HurtResponse getFromTag(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        return new HurtResponse(compoundTag.getBoolean("targetCaster"), compoundTag.getInt("damage"));
    }

    @Override
    public HurtResponse getRandom() {
        Random random = new Random();
        return new HurtResponse(random.nextBoolean(), random.nextInt(20));
    }
}
