package net.dinomine.potioneer.beyonder.player.luck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static net.dinomine.potioneer.beyonder.player.PlayerLuckManager.MAXIMUM_LUCK;
import static net.dinomine.potioneer.beyonder.player.PlayerLuckManager.MINIMUM_LUCK;

public class LuckRange {
    private int minBase;
    private int maxBase;
    private int positionBase;
    private boolean suppress = false;

    private HashMap<UUID, Integer> luckEventChanceMap = new HashMap<>();
    public int getChance(){
        int sum = 1;
        for(int val: luckEventChanceMap.values()){
            sum += val;
        }
        return Math.max(sum, 1);
    }

    public void changeChance(UUID uuid, int diffVal) {
        luckEventChanceMap.put(uuid, diffVal);
    }

    public void removeChanceModifier(UUID uuid) {
        luckEventChanceMap.remove(uuid);
    }

    private HashMap<UUID, Integer> minAttributeMap = new HashMap<>();
    private int getMinAttribute(){
        int sum = 0;
        for(int val: minAttributeMap.values()){
            sum += val;
        }
        return sum;
    }
    private HashMap<UUID, Integer> maxAttributeMap = new HashMap<>();
    private int getMaxAttribute(){
        int sum = 0;
        for(int val: maxAttributeMap.values()){
            sum += val;
        }
        return sum;
    }
    private HashMap<UUID, Integer> posAttributeMap = new HashMap<>();
    private int getPosAttribute(){
        int sum = 0;
        for(int val: posAttributeMap.values()){
            sum += val;
        }
        return sum;
    }

    private int minDecay = 0;
    private int maxDecay = 0;
    private int posDecay = 0;

    public LuckRange(int minDist, int maxDist){
        this.minBase = minDist;
        this.maxBase = maxDist;
        this.positionBase = 0;
    }

    public int getMinLuck(){
        if(suppress){
            return -(calculatMin() + calculateMax());
        } else {
            return calculatePosition() - calculatMin();
        }
    }
    public int getMaxLuck(){
        if(suppress){
            return 0;
        } else {
            return calculatePosition() + calculateMax();
        }
    }
    public int getPosition(){
        if(suppress){
            return -calculateMax();
        } else {
            return calculatePosition();
        }
    }

    private int calculateMax(){
        int test = Math.max(maxBase + getMaxAttribute() + maxDecay, 0);
        if(calculatePosition() + test > MAXIMUM_LUCK) return MAXIMUM_LUCK - calculatePosition();
        return test;
    }

    private int calculatMin(){
        int test = Math.max(minBase + getMinAttribute() + minDecay, 0);
        if(calculatePosition() - test < MINIMUM_LUCK) return -(MINIMUM_LUCK - calculatePosition());
        return test;
    }

    private int calculatePosition(){
        return Mth.clamp(positionBase + getPosAttribute() + posDecay, MINIMUM_LUCK, MAXIMUM_LUCK);
    }

    public void setSuppress(boolean newSup){
        this.suppress = newSup;
    }

    public void resetAttributes(){
        this.posAttributeMap = new HashMap<>();
        this.maxAttributeMap = new HashMap<>();
        this.minAttributeMap = new HashMap<>();
    }

    public void changeRange(UUID uuid, int minDelta, int maxDelta, int posDelta){
        minAttributeMap.put(uuid, minDelta);
        maxAttributeMap.put(uuid, maxDelta);
        posAttributeMap.put(uuid, posDelta);
    }

    public void removeModifier(UUID uuid){
        minAttributeMap.remove(uuid);
        maxAttributeMap.remove(uuid);
        posAttributeMap.remove(uuid);
    }

    public void changeDecayRange(int minDelta, int maxDelta, int posDelta){
        this.minDecay += minDelta;
        this.maxDecay += maxDelta;
        this.posDecay += posDelta;
    }

    public int changeLuck(int oldLuck, int proposedChange){
        int test = oldLuck + proposedChange;
        if(test > getMaxLuck() && proposedChange > 0) return oldLuck;
        if(test < getMinLuck() && proposedChange < 0) return oldLuck;
        return test;
    }

    public void tenSecondTick(){
        posDecay -= Integer.signum(posDecay);
        maxDecay -= Integer.signum(maxDecay);
        minDecay -= Integer.signum(minDecay);
    }

    @Override
    public String toString() {
        return "Luck Range: " + (getMaxLuck() - getMinLuck())
                + "\nMinimum Base luck: " + minBase
                + "\nMaximum Base luck: " + maxBase
                + "\nMinimum Attribute luck: " + getMinAttribute()
                + "\nMaximum Attribute luck: " + getMaxAttribute()
                + "\nMinimum Decay luck: " + minDecay
                + "\nMaximum Decay luck: " + maxDecay
                + "\nPosition Base: " + positionBase
                + "\nPosition Attribute: " + getPosAttribute()
                + "\nCenter: " + getPosition();
    }

    public CompoundTag saveNBTData(CompoundTag compoundTag) {
        compoundTag.putInt("minBase", minBase);
        compoundTag.putInt("maxBase", maxBase);
        compoundTag.putInt("posBase", positionBase);
        writeMapIntoTag(compoundTag, minAttributeMap, "minAttribute");
        writeMapIntoTag(compoundTag, maxAttributeMap, "maxAttribute");
        writeMapIntoTag(compoundTag, posAttributeMap, "posAttribute");
        writeMapIntoTag(compoundTag, luckEventChanceMap, "chanceAttribute");
        compoundTag.putInt("minDecay", minDecay);
        compoundTag.putInt("maxDecay", maxDecay);
        compoundTag.putInt("posDecay", posDecay);
        compoundTag.putBoolean("suppress", suppress);
        return compoundTag;
    }

    public void loadNBTData(CompoundTag rangeData) {
        this.minBase = rangeData.getInt("minBase");
        this.maxBase = rangeData.getInt("maxBase");
        this.positionBase = rangeData.getInt("posBase");
        this.minAttributeMap = readMapFromTag(rangeData, "minAttribute");
        this.maxAttributeMap = readMapFromTag(rangeData, "maxAttribute");
        this.posAttributeMap = readMapFromTag(rangeData, "posAttribute");
        this.luckEventChanceMap = readMapFromTag(rangeData, "chanceAttribute");
        this.minDecay = rangeData.getInt("minDecay");
        this.maxDecay = rangeData.getInt("maxDecay");
        this.posDecay = rangeData.getInt("posDecay");
        this.suppress = rangeData.getBoolean("suppress");
    }

    private static final String ENTRY_UUID = "UUID";
    private static final String ENTRY_VALUE = "VALUE";

    /**
     * Writes a Map<UUID, Integer> into the given CompoundTag and returns it.
     */
    private static CompoundTag writeMapIntoTag(CompoundTag tag, Map<UUID, Integer> map, String mapKey) {
        ListTag listTag = new ListTag();

        for (Map.Entry<UUID, Integer> entry : map.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putUUID(ENTRY_UUID, entry.getKey());
            entryTag.putInt(ENTRY_VALUE, entry.getValue());
            listTag.add(entryTag);
        }

        tag.put(mapKey, listTag);
        return tag;
    }

    /**
     * Reads a Map<UUID, Integer> from the given CompoundTag.
     * Returns a new empty map if none exists.
     */
    private static HashMap<UUID, Integer> readMapFromTag(CompoundTag tag, String mapKey) {
        HashMap<UUID, Integer> map = new HashMap<>();

        if (!tag.contains(mapKey, Tag.TAG_LIST)) {
            return map;
        }

        ListTag listTag = tag.getList(mapKey, Tag.TAG_COMPOUND);

        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag entryTag = listTag.getCompound(i);

            if (entryTag.hasUUID(ENTRY_UUID) && entryTag.contains(ENTRY_VALUE, Tag.TAG_INT)) {
                UUID uuid = entryTag.getUUID(ENTRY_UUID);
                int value = entryTag.getInt(ENTRY_VALUE);
                map.put(uuid, value);
            }
        }

        return map;
    }

    public LuckRange copyOnDeath(){
        posAttributeMap.clear();
        minAttributeMap.clear();
        maxAttributeMap.clear();
        return this;
    }

    public float[] getDataForHud() {
        float[] res = new float[10];
        res[0] = getMinAttribute();
        res[1] = getPosAttribute();
        res[2] = getMaxAttribute();

        res[3] = minDecay;
        res[4] = posDecay;
        res[5] = maxDecay;

        res[6] = minBase;
        res[7] = positionBase;
        res[8] = maxBase;

        return res;
    }
}
