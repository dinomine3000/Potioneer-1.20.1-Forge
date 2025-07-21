package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

import java.util.Arrays;
import java.util.function.Supplier;

public class PlayerActingManager {
    private double[] minerProgress;
    private double[] swimmerProgress;
    private double[] tricksterProgress;
    private double[] warriorProgress;
    private double[] crafterProgress;

    private double passiveActingLimit;
    private double passiveActing = 0f;
    private double intrinsicActingMultiplier;
    private static final Supplier<Double> randomLimit = () -> {
        double min = PotioneerCommonConfig.MINIMUM_PASSIVE_ACTING_LIMIT.get();
        double max = PotioneerCommonConfig.MAXIMUM_PASSIVE_ACTING_LIMIT.get();
        double newMax = Math.max(min, max);
        double newMin = Math.min(min, max);
        return newMin + Math.random() * (Math.max(newMax - newMin, 0));
    };

    public PlayerActingManager(){
        minerProgress = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        swimmerProgress = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        tricksterProgress = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        warriorProgress = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        crafterProgress = new double[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        passiveActingLimit = randomLimit.get();
        intrinsicActingMultiplier = PotioneerCommonConfig.DO_INTRINSIC_ACTING_MULTIPLIERS.get() ? Math.random()*PotioneerCommonConfig.MAXIMUM_INTRINSIC_ACTING_MULTIPLIER.get(): 1;
    }

    public void tick(){
        passiveActing = Mth.clamp(passiveActing + 0.00001333d, 0f, passiveActingLimit);
    }

    private double[] getActingList(int pathwayId){
        int path = Math.floorDiv(pathwayId, 10);
        return switch (path) {
            case 0 -> minerProgress;
            case 1 -> swimmerProgress;
            case 2 -> tricksterProgress;
            case 3 -> warriorProgress;
            case 4 -> crafterProgress;
            default -> null;
        };
    }

    public void progressActing(double amount, int pathwayId){
        double[] list = getActingList(pathwayId);
        if(list == null) return;
        list[pathwayId%10] = Mth.clamp(list[pathwayId%10]  + amount*intrinsicActingMultiplier * PotioneerCommonConfig.UNIVERSAL_ACTING_MULTIPLIER.get(), 0, 1);
    }

    public double getActingPercentForSanityCalculation(int pathwayId){
        if(pathwayId < 0) return 1;
        double[] list = getActingList(pathwayId);
        if(list == null || list.length == 0) return 1;
        //if youre a sequence 9, guarantees a minimum of 60% sanity, with the remaining 40% depending on your acting
        if(pathwayId%10 == 9) return (0.6 + 0.4*(list[9]));
        //for sequences 8 and onwards, the percentage depends on the acting for each lower sequence
        //60% depends on the digestion of previous sequences, 40% on the current one.
        return getAggregatedActingProgress(pathwayId + 1)*0.6 + 0.4*(list[pathwayId%10]);
    }

    public double getAggregatedActingProgress(int pathwayId){
        double[] list = getActingList(pathwayId);
        if(list == null || list.length == 0) return 1;
        double res = 0;
        int i;
        for(i = 9; i > pathwayId%10; i--){
            res += list[i];
        }
        res += Mth.clamp(list[i] + passiveActing, 0, 1);
        return res/((10-i));
    }

    public void resetPassiveActing(PlayerLuckManager luckMng, RandomSource random, int pathwayId){
        double[] list = getActingList(pathwayId);
        if(list != null && list.length > 0) list[pathwayId%10] = Mth.clamp(list[pathwayId%10] + passiveActing, 0, 1);
        passiveActingLimit = randomLimit.get();
        if(luckMng.passesLuckCheck(0.3f, 40, 0, random)) passiveActingLimit +=0.15f;
        passiveActing = 0;
    }

    public void setActing(double value, int pathwayId){
        double[] list = getActingList(pathwayId);
        if(list == null) return;
        list[pathwayId%10] = value;
    }

    public Component getDescComponent(int pathwayId){
        return Component.literal("Acting at: " + getAggregatedActingProgress(pathwayId) + "\nPassive limit: " + passiveActingLimit + "\nIntrinsic Multiplier" + intrinsicActingMultiplier
                + "\nMiner pathway: " + Arrays.toString(minerProgress)
                + "\nSwimmer pathway: " + Arrays.toString(swimmerProgress)
                + "\nTrickster pathway: " + Arrays.toString(tricksterProgress)
                + "\nWarrior pathway: " + Arrays.toString(warriorProgress)
                + "\nCrafter pathway: " + Arrays.toString(crafterProgress)
        );
    }

    public void saveNBTData(CompoundTag tag){
        CompoundTag acting = new CompoundTag();
        acting.putDouble("passiveActing", passiveActing);
        acting.putDouble("passiveActingLimit", passiveActingLimit);
        acting.putDouble("multiplier", intrinsicActingMultiplier);

        acting.put("minerProgress", toListTag(minerProgress));
        acting.put("swimmerProgress", toListTag(swimmerProgress));
        acting.put("tricksterProgress", toListTag(tricksterProgress));
        acting.put("warriorProgress", toListTag(warriorProgress));
        acting.put("crafterProgress", toListTag(crafterProgress));
        tag.put("acting", acting);
    }

    private ListTag toListTag(double[] array) {
        ListTag list = new ListTag();
        for (double f : array) {
            list.add(DoubleTag.valueOf(f));
        }
        return list;
    }

    public void loadNBTData(CompoundTag tag) {
        if(!tag.contains("acting")) return;
        CompoundTag acting = tag.getCompound("acting");
        passiveActing = acting.getDouble("passiveActing");
        passiveActingLimit = acting.getDouble("passiveActingLimit");
        intrinsicActingMultiplier = acting.getDouble("multiplier");

        minerProgress = fromListTag(acting.getList("minerProgress", Tag.TAG_DOUBLE));
        swimmerProgress = fromListTag(acting.getList("swimmerProgress", Tag.TAG_DOUBLE));
        tricksterProgress = fromListTag(acting.getList("tricksterProgress", Tag.TAG_DOUBLE));
        warriorProgress = fromListTag(acting.getList("warriorProgress", Tag.TAG_DOUBLE));
        crafterProgress = fromListTag(acting.getList("crafterProgress", Tag.TAG_DOUBLE));
    }

    private double[] fromListTag(ListTag list) {
        double[] result = new double[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = ((DoubleTag) list.get(i)).getAsDouble();
        }
        return result;
    }

    public void copyFrom(PlayerActingManager actingManager) {
        minerProgress = actingManager.minerProgress;
        swimmerProgress = actingManager.swimmerProgress;
        tricksterProgress = actingManager.tricksterProgress;
        warriorProgress = actingManager.warriorProgress;
        crafterProgress = actingManager.crafterProgress;
        passiveActing = actingManager.passiveActing;
        passiveActingLimit = actingManager.passiveActingLimit;
        intrinsicActingMultiplier = actingManager.intrinsicActingMultiplier;
    }
}
