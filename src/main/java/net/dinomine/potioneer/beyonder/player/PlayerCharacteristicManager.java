package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class PlayerCharacteristicManager {
    //TODO: make config file for the 60-40 split of current sequence and previous ones
    /**
     *when calculating the acting percentage, what percentage of it will always correspond to the current sequence acting?
     *this is used so when you advance in the high sequences, the last characteristic doesnt just encompass 7% of the acting bar. instead, itll encompass this percent
     * aka, your current characteristic has THIS percentage weight on the final digestion bar, to make it more important
     **/
    private static final float currentSequenceWeight = 0.5f;

    private HashMap<Integer, Double> actingProgress;
    private HashMap<Integer, Integer> characteristicCountMap;
    /**
     * this is a sort of LIFO "stack".
     * its designed so list.add() will add a characteristic to the stack, so popping from the stack is more like removing from the top.
     * its not a "stack" exactly because i want to insert some characteristics in the middle.
     * calling getPathwaySequenceId() will return the "peek" of this stack, aka the most recent characteristic of a high level.
     */
    private ArrayList<Integer> lastConsumedCharacteristics = new ArrayList<>();

    private int aptitudePathway;

    public PlayerCharacteristicManager(){
        actingProgress = new HashMap<>();
        characteristicCountMap = new HashMap<>();
        Set<ResourceLocation> keys = Pathways.REGISTRY.get().getKeys();
        aptitudePathway = Integer.parseInt(keys.stream().toList().get(new Random().nextInt(0, keys.size())).getPath());
        //if you land on beyonderless, you get the Wheel of Fortune pathway.
        if(aptitudePathway == -1) aptitudePathway = 0;
    }

    private int findCharacteristicOfLevel(int sequenceLevel){
        for(int i = lastConsumedCharacteristics.size(); i >= 1; i--){
            if(lastConsumedCharacteristics.get(i - 1)%10 >= sequenceLevel) return i;
        }
        return 0;
    }

    public void consumeCharacteristic(int characId){
        //add characteristic to the stack
        int idx = findCharacteristicOfLevel(characId%10);
        lastConsumedCharacteristics.add(idx, characId);

        //add count to the count map
        characteristicCountMap.merge(characId, 1, Integer::sum);
        double count = characteristicCountMap.get(characId);

        //adjust acting progress
        actingProgress.put(characId, Mth.clamp(actingProgress.getOrDefault(characId, 0d)*(count-1d)/(count), 0, 1));
    }

    /**
     * this drops the last consumed characteristic.
     * To drop all of them at once, use another method
     * @return the pathway-sequence id of the dropped characteristic
     */
    public int dropLevel(){
        //remove from the stack
        int droppedCharacteristic = -1;
        if(!PotioneerCommonConfig.ALLOW_CHANGING_PATHWAYS.get() && getSequenceLevel() == 9 && lastConsumedCharacteristics.size() == 1){
            return -1;
        }
        if(!lastConsumedCharacteristics.isEmpty()) droppedCharacteristic = lastConsumedCharacteristics.remove(lastConsumedCharacteristics.size()-1);

        //reduce count on the map
        characteristicCountMap.merge(droppedCharacteristic, -1, Integer::sum);
        if(characteristicCountMap.get(droppedCharacteristic) == 0){
            characteristicCountMap.remove(droppedCharacteristic);
            actingProgress.remove(droppedCharacteristic);
        } else {
            double count = characteristicCountMap.get(droppedCharacteristic);

            //adjust acting progress
            actingProgress.put(droppedCharacteristic, Mth.clamp(getActing(droppedCharacteristic)*(count+1d)/(count), 0d, 1d));
        }


        return droppedCharacteristic;
    }

    public List<Integer> dropAllCharacteristics(){
        List<Integer> characteristicsHolder = new ArrayList<>(lastConsumedCharacteristics);

        if(!PotioneerCommonConfig.ALLOW_CHANGING_PATHWAYS.get()){
            int lockedCharacId = characteristicsHolder.remove(0);
            double digestion = getActing(lockedCharacId);

            characteristicCountMap = new HashMap<>();
            characteristicCountMap.put(lockedCharacId, 1);

            actingProgress = new HashMap<>();
            actingProgress.put(lockedCharacId, Mth.clamp(digestion, 0, 1));

            lastConsumedCharacteristics = new ArrayList<>();
            lastConsumedCharacteristics.add(lockedCharacId);
        } else {
            characteristicCountMap = new HashMap<>();
            actingProgress = new HashMap<>();
            lastConsumedCharacteristics = new ArrayList<>();
        }

        return characteristicsHolder;
    }

    public int getSequenceLevel() {
        return getPathwaySequenceId()%10;
    }

    public int getPathwaySequenceId(){
        if(lastConsumedCharacteristics.isEmpty()) return -1;
        Integer res = lastConsumedCharacteristics.get(lastConsumedCharacteristics.size() - 1);
        return res == null ? -1 : res;
    }

    public int getPathwayId(){
        return Math.floorDiv(getPathwaySequenceId(), 10);
    }

    public BeyonderPathway getPathway(){
        return Pathways.getPathwayById(getPathwayId());
    }

    public void tick(){
        if(PotioneerCommonConfig.PASSIVELY_DIGEST_ALL_CHARACTERISTICS.get()){
            double tickVal = PotioneerCommonConfig.PASSIVE_ACTING_LIMIT.get()/PotioneerCommonConfig.PASSIVE_ACTING_RATE.get()*20d;
            for(Map.Entry<Integer, Double> acting: actingProgress.entrySet()){
                if(acting.getValue() >= PotioneerCommonConfig.PASSIVE_ACTING_LIMIT.get()) continue;
                actingProgress.put(acting.getKey(), Mth.clamp(acting.getValue() + tickVal, 0d, 1));
            }
        } else {
            int id = getPathwaySequenceId();
            if(id < 0) return;
            if(getActing(id) >= PotioneerCommonConfig.PASSIVE_ACTING_LIMIT.get()) return;
            double tickVal = PotioneerCommonConfig.PASSIVE_ACTING_LIMIT.get()/PotioneerCommonConfig.PASSIVE_ACTING_RATE.get()*20d;
            actingProgress.put(id, Mth.clamp(getActing(id) + tickVal, 0d, 1));
        }
    }

    public void progressActing(double amount, int pathwayId){
        if(!actingProgress.containsKey(pathwayId)) return;
        double aptitude_mult = PotioneerCommonConfig.DO_APTITUDE_PATHWAYS.get() ? PotioneerCommonConfig.APTITUDE_MULTIPLIER.get() : 1;
        double newVal = Mth.clamp(
                getActing(pathwayId)
                        + (
                            amount
                                *(Math.floorDiv(pathwayId, 10) == aptitudePathway ? aptitude_mult : 1)
                                * PotioneerCommonConfig.UNIVERSAL_ACTING_MULTIPLIER.get()
                                / characteristicCountMap.get(pathwayId)
                        ),
                    0, 1);
        actingProgress.put(pathwayId, newVal);
    }

    public double getActingPercentForSanityCalculation(){
        if(actingProgress.isEmpty()) return 1;
        if(lastConsumedCharacteristics.size() == 1){
            return 0.6d + 0.4d*actingProgress.values().stream().findFirst().get();
        } else {
            return getAdjustedActingPercent(getPathwaySequenceId());
        }
    }
//        //System.out.println("Warning: CHeck this method later, doesnt seem like its being called as often as it should: PlayerCharacteristicManager.getActingPercentForSanityCalculation");
//        int minSequenceLevel = 10;
//        double aggregatePercent = 0d;
//        int i = 0;
//        for(Map.Entry<Integer, Double> charac: actingProgress.entrySet()){
//            i++;
//            int pathwayId = charac.getKey();
//            aggregatePercent += charac.getValue();
//            if (pathwayId%10 < minSequenceLevel){
//                minSequenceLevel = pathwayId%10;
//            }
//        }
//        aggregatePercent = aggregatePercent/i;
//        //if youre a sequence 9, guarantees a minimum of 60% sanity, with the remaining 40% depending on your acting
//        if(minSequenceLevel >= 9) return (0.6 + 0.4*aggregatePercent);
//        //for sequences 8 and onwards, the percentage depends on the acting for each lower sequence
//        //60% depends on the digestion of previous sequences, 40% on the current one.
//        return getAggregatedActingProgress(minSequenceLevel + 1)*0.6 + 0.4*(list[sequenceLevel%10]);
//    }

    /**
     * calculates the acting percent, adjusting for all characteristics, and giving the last consumed characteristic (in the argument currentSequenceLevel) a special weight in the final percent.
     * @param currentSequenceLevel - current sequence level to adjust its percent
     * @return the adjusted percent
     */
    public double getAdjustedActingPercent(int currentSequenceLevel){
        double finalDigestion = 0d;
        int count = 0;
        for(Map.Entry<Integer, Double> charac: actingProgress.entrySet()){
            if(charac.getKey() == currentSequenceLevel) continue;
            count += 1;
            finalDigestion += charac.getValue();
        }
        finalDigestion /= count;
        double currentSequenceActing = getActing(currentSequenceLevel);
        finalDigestion = (1 - currentSequenceWeight) * finalDigestion + currentSequenceWeight * currentSequenceActing;
        return finalDigestion;
    }

    /**
     * returns the acting percent for the specific pathway-sequence id. used for abilities that want to scale on acting percent.
     * @param pathwayId
     * @return
     */
    public double getActingPercentForSequence(int pathwayId){
        return actingProgress.getOrDefault(pathwayId, 0d);
    }

//    public double getAggregatedActingProgress(int maxSequenceLevel){
//        if(actingProgress.isEmpty()) return 1d;
//        double aggregatePercent = 0d;
//        int i = 0;
//        for(Map.Entry<Integer, Double> charac: actingProgress.entrySet()){
//            int pathwayId = charac.getKey();
//            if(pathwayId%10 >= maxSequenceLevel){
//                i++;
//                aggregatePercent += charac.getValue();
//            }
//        }
//        aggregatePercent = aggregatePercent/i;
//        return Mth.clamp(aggregatePercent, 0, 1);
//    }

//    public void resetPassiveActing(PlayerLuckManager luckMng, RandomSource random, int pathwayId){
//        double[] list = getActingList(pathwayId);
//        if(list != null && list.length > 0) list[pathwayId%10] = Mth.clamp(list[pathwayId%10] + passiveActing, 0, 1);
//        passiveActingLimit = randomLimit.get();
//        if(luckMng.passesLuckCheck(0.3f, 40, 0, random)) passiveActingLimit +=0.15f;
//        passiveActing = 0;
//    }

    private double getActing(int pathwaySequenceId){
        return actingProgress.getOrDefault(pathwaySequenceId, 0d);
    }

    public void setActing(double value, int pathwayId){
        actingProgress.put(pathwayId, Mth.clamp(value, 0, 1));
    }

    public Component getDescComponent(){
        if(characteristicCountMap.isEmpty()) return Component.literal("You have no characteristic.");
        String result = "Acting progress detailed breakdown:\n";
        for(Map.Entry<Integer, Double> charact: actingProgress.entrySet()){
            int id = charact.getKey();
            int count = characteristicCountMap.get(id);
            result = result.concat("You have " + count + " " +  Pathways.getPathwayById(id).getSequenceNameFromId(id%10, true)
                    + (id == 1 ? " characteristic that is " : " characteristics that are ")
                    + Math.round(getActing(id)*100d)  + "% digested.\n");
        }
        result = result.concat("Complete acting progress is at " + Math.round(getAdjustedActingPercent(getPathwaySequenceId())*100d));
        result = result.concat("Complete list of characteristics:\n" + lastConsumedCharacteristics.stream().map(val -> Pathways.getPathwayById(val).getSequenceNameFromId(val%10, true)));
        return Component.literal(result);
    }

    public void saveNBTData(CompoundTag tag){
        CompoundTag acting = new CompoundTag();
        acting.put("characteristics", toListTag(lastConsumedCharacteristics));
        ArrayList<Integer> hold = new ArrayList<>();
        ArrayList<Double> finalActing = new ArrayList<>();
        for(int id: lastConsumedCharacteristics){
            if(!hold.contains(id)){
                hold.add(id);
                finalActing.add(getActing(id));
            }
        }
        acting.put("acting_progress", toListTag(finalActing));
        acting.putInt("aptitude", aptitudePathway);
        tag.put("acting", acting);
    }

    private <T extends Number> ListTag toListTag(ArrayList<T> array) {
        ListTag list = new ListTag();
        for (T f : array) {
            if(f instanceof Double dVal){
                list.add(DoubleTag.valueOf(dVal));
            } else if(f instanceof Integer iVal){
                list.add(IntTag.valueOf(iVal));
            }
        }
        return list;
    }

    public void loadNBTData(CompoundTag tag, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!tag.contains("acting")) return;
        //build list of consumed characteristics
        CompoundTag acting = tag.getCompound("acting");
        lastConsumedCharacteristics = fromIntListTag(acting.getList("characteristics", Tag.TAG_INT));

        //build map of acting progress
        //at the same time as i check for repeat, i also build the characteristic counted map
        actingProgress = new HashMap<>();
        ArrayList<Double> temp_acting_list = fromDoubleListTag(acting.getList("acting_progress", Tag.TAG_DOUBLE));
        characteristicCountMap = new HashMap<>();
        int i = 0;
        for(int id: lastConsumedCharacteristics){
            if(!characteristicCountMap.containsKey(id)){
                characteristicCountMap.put(id, 1);
                actingProgress.put(id, temp_acting_list.get(i));
                i++;
            } else {
                characteristicCountMap.put(id, characteristicCountMap.get(id) + 1);
            }
        }

        //get aptitude pathway
        aptitudePathway = acting.getInt("aptitude");

        //get abilities
        setAllAbilities(cap, target);
    }

    private void setAllAbilities(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int highestLevel = getSequenceLevel();
        List<Ability> allAbilities = new ArrayList<>();
        //get highest level for each pathway
        List<Integer> pathwayLevels = closestToLowerTens(lastConsumedCharacteristics);
        //get abilities for each pathway
        for(int sequence: pathwayLevels){
            allAbilities.addAll(Pathways.getPathwayById(sequence).getAbilities(sequence%10));
        }
        //make sure every ability is the highest level
        for(Ability abl: allAbilities){
            abl.setSequenceLevel(highestLevel);
        }
        //set
        for(Ability abl: allAbilities){
            cap.getAbilitiesManager().addAbility(PlayerAbilitiesManager.AbilityList.INTRINSIC, abl, cap, target, false);
        }
    }

    /**
     * given a list of numbers, returns a list of every number that is closest to its lower multiple of 10.
     * This way, we get the highest level for each pathway
     * @param nums, a list of characteristics like [17, 18, 19, 25, 24, 29, 37, 36]
     * @return the best levels for each pathway [17, 24, 36] for the above example
     */
    private static List<Integer> closestToLowerTens(List<Integer> nums) {
        Map<Integer, Integer> bestPerTen = new HashMap<>();

        for (int n : nums) {
            int base = (n / 10) * 10;
            int dist = n - base;

            bestPerTen.compute(base, (k, currentBest) -> {
                if (currentBest == null) return n;
                int currentDist = currentBest - base;
                return dist < currentDist ? n : currentBest;
            });
        }

        return new ArrayList<>(bestPerTen.values());
    }

    private ArrayList<Double> fromDoubleListTag(ListTag list) {
        ArrayList<Double> result = new ArrayList<>();
        for (Tag tag : list) {
            result.add(((DoubleTag) tag).getAsDouble());
        }
        return result;
    }

    private ArrayList<Integer> fromIntListTag(ListTag list) {
        ArrayList<Integer> result = new ArrayList<>();
        for (Tag tag : list) {
            result.add(((IntTag) tag).getAsInt());
        }
        return result;
    }

    public void copyFrom(PlayerCharacteristicManager other) {
        lastConsumedCharacteristics = other.lastConsumedCharacteristics;
        characteristicCountMap = other.characteristicCountMap;
        actingProgress = other.actingProgress;
        aptitudePathway = other.aptitudePathway;
    }

    public void setAbilities(int characteristicId, LivingEntityBeyonderCapability cap, LivingEntity target) {
        //get all abilities from characteristics
        //create cogitation ability based on last consumed characteristic
        //update the abilities manager
        //maybe make it update the intrinsic abilities after the tick is over, so we dont change the list while its being run on the tick() method


        List<Ability> newAbilities = Pathways.getPathwayById(characteristicId).getAbilities(characteristicId%10);
        int pathwaySequenceId = getPathwaySequenceId();
        cap.getAbilitiesManager().grantAbilities(newAbilities, pathwaySequenceId, cap, target);
    }

    public void setAttributes(BeyonderStats beyonderStats, Player player) {
        //get best attributes for each stat based on all the characteristics
        //give BeyonderStats that as the stats to update
        //it already deals with removing the old modifiers and applying these new ones
        List<Integer> bestCharacts = closestToLowerTens(lastConsumedCharacteristics);
        float[] bestStats = new float[5];
        List<float[]> attributesList = new ArrayList<>();
        for(int charac: bestCharacts){
            attributesList.add(Pathways.getPathwayById(charac).getStatsFor(charac%10));
        }
        for(int i = 0; i < 5; i++){
            float bestStat = 0;
            for(float[] attributes: attributesList){
                if(attributes[i] > bestStat) bestStat = attributes[i];
            }
            bestStats[i] = bestStat;
        }
        beyonderStats.setAttributes(bestStats);
        beyonderStats.applyStats(player, true);
    }

    public void reset() {
        //called when forcefully reset characteristics
        lastConsumedCharacteristics = new ArrayList<>();
        characteristicCountMap = new HashMap<>();
        actingProgress = new HashMap<>();
    }

    public int getMaxSpirituality() {
        int bestSpirituality = 0;
        for(int i: lastConsumedCharacteristics){
            int testSpir = Pathways.getPathwayById(i).getMaxSpirituality(i%10);
            if(testSpir > bestSpirituality) bestSpirituality = testSpir;
        }
        return bestSpirituality;
    }
}
