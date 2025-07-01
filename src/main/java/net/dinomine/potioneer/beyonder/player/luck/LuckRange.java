package net.dinomine.potioneer.beyonder.player.luck;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;

import static net.dinomine.potioneer.beyonder.player.PlayerLuckManager.MAXIMUM_LUCK;
import static net.dinomine.potioneer.beyonder.player.PlayerLuckManager.MINIMUM_LUCK;

public class LuckRange {
    private int minBase;
    private int maxBase;
    private int positionBase;
    private boolean suppress = false;

    private int minAttribute = 0;
    private int maxAttribute = 0;
    private int posAttribute = 0;

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
        int test = Math.max(maxBase + maxAttribute + maxDecay, 0);
        if(calculatePosition() + test > MAXIMUM_LUCK) return MAXIMUM_LUCK - calculatePosition();
        return test;
    }

    private int calculatMin(){
        int test = Math.max(minBase + minAttribute + minDecay, 0);
        if(calculatePosition() - test < MINIMUM_LUCK) return -(MINIMUM_LUCK - calculatePosition());
        return test;
    }

    private int calculatePosition(){
        return Mth.clamp(positionBase + posAttribute + posDecay, MINIMUM_LUCK, MAXIMUM_LUCK);
    }

    public void setSuppress(boolean newSup){
        this.suppress = newSup;
    }

    public void resetAttributes(){
        this.minAttribute = 0;
        this.maxAttribute = 0;
        this.posAttribute = 0;
    }

    public void changeRange(int minDelta, int maxDelta, int posDelta){
        this.minAttribute += minDelta;
        this.maxAttribute += maxDelta;
        this.posAttribute += posDelta;
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
                + "\nMinimum Attribute luck: " + minAttribute
                + "\nMaximum Attribute luck: " + maxAttribute
                + "\nMinimum Decay luck: " + minDecay
                + "\nMaximum Decay luck: " + maxDecay
                + "\nPosition Base: " + positionBase
                + "\nPosition Attribute: " + posAttribute
                + "\nCenter: " + getPosition();
    }

    public CompoundTag saveNBTData(CompoundTag compoundTag) {
        compoundTag.putInt("minBase", minBase);
        compoundTag.putInt("maxBase", maxBase);
        compoundTag.putInt("posBase", positionBase);
        compoundTag.putInt("minAtt", minAttribute);
        compoundTag.putInt("maxAtt", maxAttribute);
        compoundTag.putInt("posAtt", posAttribute);
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
        this.minAttribute = rangeData.getInt("minAtt");
        this.maxAttribute = rangeData.getInt("maxAtt");
        this.posAttribute = rangeData.getInt("posAtt");
        this.minDecay = rangeData.getInt("minDecay");
        this.maxDecay = rangeData.getInt("maxDecay");
        this.posDecay = rangeData.getInt("posDecay");
        this.suppress = rangeData.getBoolean("suppress");
    }
}
