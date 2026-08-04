package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.config.PotioneerGameplayConfig;
import net.dinomine.potioneer.util.misc.ModTags;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;
import java.util.stream.Collectors;

import static net.dinomine.potioneer.util.misc.CharacteristicHelper.closestToLowerTens;
import static net.dinomine.potioneer.util.misc.CharacteristicHelper.floorDivTen;

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
    private boolean isClientSide = false;
    private int aptitudePathway;

    public PlayerCharacteristicManager(){
        actingProgress = new HashMap<>();
        characteristicCountMap = new HashMap<>();
        Set<ResourceLocation> keys = Pathways.REGISTRY.get().getKeys();
        aptitudePathway = Integer.parseInt(keys.stream().toList().get(new Random().nextInt(0, keys.size())).getPath());
        //if you land on beyonderless, you get the Wheel of Fortune pathway.
        if(aptitudePathway == -1) aptitudePathway = 0;
    }

    public int getAptitudePathway(){return aptitudePathway;}

    private int findCharacteristicOfLevel(int sequenceLevel){
        for(int i = lastConsumedCharacteristics.size(); i >= 1; i--){
            if(lastConsumedCharacteristics.get(i - 1)%10 >= sequenceLevel) return i;
        }
        return 0;
    }

    public void consumeCharacteristic(LivingEntityBeyonderCapability cap, LivingEntity entity, int characId){
        //add characteristic to the stack
        int idx = findCharacteristicOfLevel(characId%10);
        lastConsumedCharacteristics.add(idx, characId);

        //add count to the count map
        characteristicCountMap.merge(characId, 1, Integer::sum);
        double count = characteristicCountMap.get(characId);

        //adjust acting progress
        actingProgress.put(characId, Mth.clamp(actingProgress.getOrDefault(characId, 0d)*(count-1d)/(count), 0, 1));


        cap.getAbilitiesManager().grantIntrinsicAbilities(getAbilitiesFromCharacteristics(), getPathwaySequenceId(), true, cap, entity);
        if(entity instanceof Player player) setAttributes(cap.getBeyonderStats(), player);
    }

    /**
     * this drops the last consumed characteristic.
     * To drop all of them at once, use another method
     * @return the pathway-sequence id of the dropped characteristic
     */
    public List<Integer> dropLevel(LivingEntityBeyonderCapability cap, LivingEntity target, boolean forceDrop){
        if(lastConsumedCharacteristics.isEmpty())
            return List.of(-1);
        //remove from the stack
        if(!forceDrop && !PotioneerGameplayConfig.ALLOW_CHANGING_PATHWAYS.get() && getSequenceLevel() == 9 && lastConsumedCharacteristics.size() == 1){
            return List.of(-1);
        }
        int droppedCharacteristic = lastConsumedCharacteristics.remove(lastConsumedCharacteristics.size()-1);

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

        setAllAbilities(cap, target, false);
        if(target instanceof Player player){
            setAttributes(cap.getBeyonderStats(), player);
        }
        return List.of(droppedCharacteristic);
    }

    public List<List<Integer>> dropAllCharacteristics(LivingEntityBeyonderCapability cap, LivingEntity target){
        List<Integer> characteristicsHolder = new ArrayList<>(lastConsumedCharacteristics);

        if(!PotioneerGameplayConfig.ALLOW_CHANGING_PATHWAYS.get()){
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

        if(!characteristicsHolder.isEmpty()){
            setAllAbilities(cap, target, false);
            if(target instanceof Player player){
                setAttributes(cap.getBeyonderStats(), player);
                cap.getBeyonderStats().applyStats(player, true);
            }
        }

        return groupByHouseOfTen(characteristicsHolder);
    }

    private static List<List<Integer>> groupByHouseOfTen(List<Integer> numbers) {
        if (numbers == null || numbers.isEmpty()) {
            return Collections.emptyList();
        }

        // Group by house, then extract only the values (the lists)
        return new ArrayList<>(numbers.stream()
                .collect(Collectors.groupingBy(num -> Math.floorDiv(num, 10)))
                .values());
    }

    public int getSequenceLevel() {
        return getPathwaySequenceId()%10;
    }

    /**
     * since lastConsumedCharacteristics is a stack where the highest is the most recent of the highest level,
     * this works.
     * @return
     */
    public int getPathwaySequenceId(){
        if(lastConsumedCharacteristics.isEmpty()) return -1;
        Integer res = lastConsumedCharacteristics.get(lastConsumedCharacteristics.size() - 1);
        return res == null ? -1 : res;
    }

    /**
     * On client side, the only thing that matters is the last consumed characterstics.
     * @param player
     * @param lastConsumedCharacteristics
     */
    public void setCharacteristicsOnClient(Player player, List<Integer> lastConsumedCharacteristics){
        if(!player.level().isClientSide()) return;
        this.lastConsumedCharacteristics = new ArrayList<>(lastConsumedCharacteristics);
        this.isClientSide = true;
    }

    public int getPathwayId(){
        return Math.floorDiv(getPathwaySequenceId(), 10);
    }

    public BeyonderPathway getPathway(){
        return Pathways.getPathwayBySequenceId(getPathwaySequenceId());
    }

    public void tick(){
        if(PotioneerGameplayConfig.PASSIVELY_DIGEST_ALL_CHARACTERISTICS.get()){
            double tickVal = PotioneerGameplayConfig.PASSIVE_ACTING_LIMIT.get()/ PotioneerGameplayConfig.PASSIVE_ACTING_RATE.get()*20d;
            for(Map.Entry<Integer, Double> acting: actingProgress.entrySet()){
                if(acting.getValue() >= PotioneerGameplayConfig.PASSIVE_ACTING_LIMIT.get()) continue;
                actingProgress.put(acting.getKey(), Mth.clamp(acting.getValue() + tickVal, 0d, 1));
            }
        } else {
            int id = getPathwaySequenceId();
            if(id < 0) return;
            if(getActing(id) >= PotioneerGameplayConfig.PASSIVE_ACTING_LIMIT.get()) return;
            double tickVal = PotioneerGameplayConfig.PASSIVE_ACTING_LIMIT.get()/(PotioneerGameplayConfig.PASSIVE_ACTING_RATE.get()*20d);
            progressActing(tickVal, id);
        }
    }

    private int getCount(int pathSeqId){
        return characteristicCountMap.getOrDefault(pathSeqId, 0);
    }

    public void progressActing(double amount, int pathwayId){
        if(!actingProgress.containsKey(pathwayId)) return;
        double aptitude_mult = PotioneerGameplayConfig.DO_APTITUDE_PATHWAYS.get() ? PotioneerGameplayConfig.APTITUDE_MULTIPLIER.get() : 1;
        double newVal = Mth.clamp(
                getActing(pathwayId)
                        + (
                            amount
                                *(Math.floorDiv(pathwayId, 10) == aptitudePathway ? aptitude_mult : 1)
                                * PotioneerGameplayConfig.UNIVERSAL_ACTING_MULTIPLIER.get()
                                / getCount(pathwayId)
                        ),
                    0, 1);
        actingProgress.put(pathwayId, newVal);
    }

    public double getActingPercentForSanityCalculation(){
        if(actingProgress.isEmpty()) return 1;
        if(isClientSide) return 1;
        if(lastConsumedCharacteristics.size() == 1){
//            return 0.6d + 0.4d * actingProgress.values().stream().findFirst().get();
            return Mth.clamp(0.6d + 0.4d*actingProgress.values().stream().findFirst().get() - 0.25*(9-getSequenceLevel()), 0, 1);
        } else {
            List<Integer> presentPathways = floorDivTen(lastConsumedCharacteristics);
            if(presentPathways.size() == 1)
                return getAdjustedActingPercent(getPathwaySequenceId());

            ArrayList<ArrayList<Integer>> groups = PotioneerGameplayConfig.getPathwayGroups();
            //TODO if this has a significant impact on performance, cache the penalty amount
            double totalPenalty = PotioneerGameplayConfig.PATHWAY_SANITY_PENALTY.get() * (presentPathways.size() - 1);
            for(ArrayList<Integer> group: groups){
                if(group.stream().anyMatch(presentPathways::contains))
                    totalPenalty += PotioneerGameplayConfig.GROUP_SANITY_PENALTY.get();
            }
            //remove one because we dont count the original group
            //if youre in 2 groups, penalty should be 1x, if youre in 4 groups, penalty should be 3x
            totalPenalty -= PotioneerGameplayConfig.GROUP_SANITY_PENALTY.get();

            return getAdjustedActingPercent(getPathwaySequenceId()) * (1-totalPenalty);
        }
    }

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
        finalDigestion /= Math.max(count, 1);
        double currentSequenceActing = getActing(currentSequenceLevel);
        finalDigestion = count == 0 ? currentSequenceActing : (1 - currentSequenceWeight) * finalDigestion + currentSequenceWeight * currentSequenceActing;
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
            result = result.concat("You have " + count + " " +  Pathways.getPathwayBySequenceId(id).getSequenceNameFromId(id%10, true)
                    + (count == 1 ? " characteristic that is " : " characteristics that are ")
                    + Math.round(getActing(id)*100d)  + "% digested.\n");
        }
        result = result.concat("Complete acting progress is at " + Math.round(getAdjustedActingPercent(getPathwaySequenceId())*100d) + "%");
        result = result.concat("\nComplete list of characteristics:\n" + lastConsumedCharacteristics.stream().map(val -> Pathways.getPathwayBySequenceId(val).getSequenceNameFromId(val%10, true)).toList());
        return Component.literal(result);
    }

    public void saveNBTData(CompoundTag tag){
        CompoundTag acting = new CompoundTag();
        acting.put("characteristics", ModTags.toNumberListTag(lastConsumedCharacteristics));
        ArrayList<Integer> hold = new ArrayList<>();
        ArrayList<Double> finalActing = new ArrayList<>();
        for(int id: lastConsumedCharacteristics){
            if(!hold.contains(id)){
                hold.add(id);
                finalActing.add(getActing(id));
            }
        }
        acting.put("acting_progress", ModTags.toNumberListTag(finalActing));
        acting.putInt("aptitude", aptitudePathway);
        tag.put("acting", acting);
    }

    public void loadNBTData(CompoundTag tag, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!tag.contains("acting")) return;
        //build list of consumed characteristics
        CompoundTag acting = tag.getCompound("acting");
        lastConsumedCharacteristics = ModTags.fromIntListTag(acting.getList("characteristics", Tag.TAG_INT));
        //build map of acting progress
        //at the same time as i check for repeat, i also build the characteristic counted map
        actingProgress = new HashMap<>();
        ArrayList<Double> temp_acting_list = ModTags.fromDoubleListTag(acting.getList("acting_progress", Tag.TAG_DOUBLE));
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
        setAllAbilities(cap, target, true);

        //set stats
        if(target instanceof Player player){
            setAttributes(cap.getBeyonderStats(), player);
            cap.getBeyonderStats().applyStats(player, false);
        }
    }

    private void setAllAbilities(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        List<Ability> allAbilities = getAbilitiesFromCharacteristics();
        cap.getAbilitiesManager().grantIntrinsicAbilities(allAbilities, getPathwaySequenceId(), !fromLoading, cap, target);
    }

    public void copyFrom(PlayerCharacteristicManager other) {
        lastConsumedCharacteristics = other.lastConsumedCharacteristics;
        characteristicCountMap = other.characteristicCountMap;
        actingProgress = other.actingProgress;
        aptitudePathway = other.aptitudePathway;
    }

    public List<Ability> getAbilitiesFromCharacteristics() {
        //get all abilities from characteristics
        //create cogitation ability based on last consumed characteristic
        //update the abilities manager
        //maybe make it update the intrinsic abilities after the tick is over, so we dont change the list while its being run on the tick() method


        int pathwaySequenceId = getPathwaySequenceId();
        List<Ability> newAbilities = new ArrayList<>();
        for(Integer characId: closestToLowerTens(lastConsumedCharacteristics)){
            newAbilities.addAll(Pathways.getPathwayBySequenceId(characId).getAbilities(characId%10, pathwaySequenceId%10));
        }
        return newAbilities;
    }

    public void setAttributes(BeyonderStats beyonderStats, Player player) {
        //get best attributes for each stat based on all the characteristics
        //give BeyonderStats that as the stats to update
        //it already deals with removing the old modifiers and applying these new ones
        beyonderStats.resetStats();
        List<Integer> bestCharacts = closestToLowerTens(lastConsumedCharacteristics);
        for (BeyonderStats.StatType type : BeyonderStats.StatType.values()) {
            if(type == BeyonderStats.StatType.STAMINA) continue;
            float highestStat = 0f;
            for (int charac : bestCharacts) {
                Map<BeyonderStats.StatType, Float> pathStats = Pathways.getPathwayBySequenceId(charac).getStatsFor(charac % 10);
                highestStat = Math.max(highestStat, pathStats.getOrDefault(type, 0f));
            }
            beyonderStats.addStat(type, highestStat);
        }
        int sequenceLevel = getSequenceLevel();
        float bestStamina = 0;
        for(int charac: bestCharacts){
            if(charac%10 != sequenceLevel) continue;
            Map<BeyonderStats.StatType, Float> pathStats = Pathways.getPathwayBySequenceId(charac).getStatsFor(charac % 10);
            bestStamina = Math.max(bestStamina, pathStats.get(BeyonderStats.StatType.STAMINA));
        }
        beyonderStats.addStamina(bestStamina == 0 ? 5 : bestStamina);
        beyonderStats.applyStats(player, true);
    }

    public void reset() {
        //called when forcefully reset characteristics
        lastConsumedCharacteristics = new ArrayList<>();
        characteristicCountMap = new HashMap<>();
        actingProgress = new HashMap<>();
    }

    public int getMaxSpirituality() {
        int bestSpirituality = 100;
        for(int i: lastConsumedCharacteristics){
            int testSpir = Pathways.getPathwayBySequenceId(i).getMaxSpirituality(i%10);
            if(testSpir > bestSpirituality) bestSpirituality = testSpir;
        }
        return (int) (bestSpirituality* PotioneerGameplayConfig.UNIVERSAL_MAX_SPIRITUALITY_MULTIPLIER.get());
    }

    public ArrayList<Integer> getLastConsumedCharacteristics() {
        return lastConsumedCharacteristics;
    }

    public boolean hasMoreThanOneCharacteristic() {
        return lastConsumedCharacteristics.size() > 1;
    }
}
