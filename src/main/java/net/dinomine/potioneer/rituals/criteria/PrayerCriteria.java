package net.dinomine.potioneer.rituals.criteria;

import net.dinomine.potioneer.rituals.RandomizableCriteria;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

public class PrayerCriteria extends ResponseCriteria implements RandomizableCriteria<PrayerCriteria> {

    private final RitualInputData.FIRST_VERSE first;
    private final RitualInputData.SECOND_VERSE second;
    public PrayerCriteria(RitualInputData.FIRST_VERSE first, RitualInputData.SECOND_VERSE second){
        this.first = first;
        this.second = second;
    }

    @Override
    public PrayerCriteria getRandom() {
        RitualInputData.FIRST_VERSE[] verses = RitualInputData.FIRST_VERSE.values();
        RitualInputData.FIRST_VERSE first = verses[(int)(Math.random()*verses.length)];
        RitualInputData.SECOND_VERSE[] verses2 = RitualInputData.SECOND_VERSE.values();
        RitualInputData.SECOND_VERSE second = verses2[(int)(Math.random()*verses2.length)];
        return new PrayerCriteria(first, second);
    }

    @Override
    public boolean checkCondition(RitualInputData input, Level level) {
        return input.firstVerse().equals(this.first) && input.secondVerse().equals(this.second);
    }

    @Override
    public Tag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("first", first.name());
        tag.putString("second", second.name());
        return envelopTag(tag, "prayer");
    }

    public static PrayerCriteria getFromTag(Tag tag) {
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        RitualInputData.FIRST_VERSE first = RitualInputData.FIRST_VERSE.valueOf(compoundTag.getString("first"));
        RitualInputData.SECOND_VERSE second = RitualInputData.SECOND_VERSE.valueOf(compoundTag.getString("second"));
        return new PrayerCriteria(first, second);
    }

}
