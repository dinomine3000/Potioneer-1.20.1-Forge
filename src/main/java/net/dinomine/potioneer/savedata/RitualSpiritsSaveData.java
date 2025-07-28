package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.rituals.RandomizableCriteria;
import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.dinomine.potioneer.rituals.criteria.OfferingsCriteria;
import net.dinomine.potioneer.rituals.criteria.PathwayCriteria;
import net.dinomine.potioneer.rituals.criteria.ResponseCriteria;
import net.dinomine.potioneer.rituals.criteria.SequenceLevelCriteria;
import net.dinomine.potioneer.rituals.responses.AidResponse;
import net.dinomine.potioneer.rituals.responses.HurtResponse;
import net.dinomine.potioneer.rituals.responses.NegativeEffectResponse;
import net.dinomine.potioneer.rituals.responses.SpiritResponse;
import net.dinomine.potioneer.rituals.spirits.EvilSpirit;
import net.dinomine.potioneer.rituals.spirits.PlayerRitualSpirit;
import net.dinomine.potioneer.rituals.spirits.RitualSpiritResponse;
import net.dinomine.potioneer.rituals.spirits.defaultGods.WheelOfFortuneResponse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public class RitualSpiritsSaveData extends SavedData {
    public static final PlayerRitualSpirit PLAYER_SPIRIT = new PlayerRitualSpirit();

    public static final WheelOfFortuneResponse WHEEL_OF_FORTUNE = new WheelOfFortuneResponse();

    private List<EvilSpirit> worldSpirits;

    private RitualSpiritsSaveData(ServerLevel level){
//        System.out.println("Creating new saved spirit data");
        this.worldSpirits = new ArrayList<>();
        this.worldSpirits.add(SpiritHelper.createRandomSpirit());
        setDirty();
    }

    private RitualSpiritsSaveData(List<EvilSpirit> evilSpirits){
        this.worldSpirits = evilSpirits;
    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
        SpiritHelper.saveEvilSpiritList(compoundTag, "evil_spirits", worldSpirits);
//        System.out.println("[RitualSpiritsSaveData] Saving tag as: " + compoundTag);
        return compoundTag;
    }


    public static RitualSpiritsSaveData loadSpirits(CompoundTag nbt, Level level){
//        System.out.println("[RitualSpiritsSaveData] Loading tag: " + nbt);
        List<EvilSpirit> spirits = SpiritHelper.loadEvilSpiritList(nbt, "evil_spirits");
        if(spirits.isEmpty()) return null;
        return new RitualSpiritsSaveData(spirits);
    }

    public static RitualSpiritsSaveData from(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent((tag) -> loadSpirits(tag, level),
                () -> new RitualSpiritsSaveData(level), "potioneer_rituals");
    }

    public EvilSpirit getPlayerSpirit(){
        return PLAYER_SPIRIT;
    }

    public EvilSpirit getSpiritForRitual(RitualInputData inputData){
        EvilSpirit incenseSpirit = checkIncense(inputData);
        if(incenseSpirit != null) return incenseSpirit;
        return checkItems(inputData);
    }

    private EvilSpirit checkIncense(RitualInputData inputData){
        if(WHEEL_OF_FORTUNE.isValidIncense(inputData.incense())){
            return WHEEL_OF_FORTUNE;
        }
        for(EvilSpirit spirit: worldSpirits){
            if(spirit.isValidIncense(inputData.incense())){
                return spirit;
            }
        }
        return null;
    }

    private EvilSpirit checkItems(RitualInputData inputData){
        if(WHEEL_OF_FORTUNE.isValidItems(inputData.offerings())){
            return WHEEL_OF_FORTUNE;
        }
        for(EvilSpirit spirit: worldSpirits){
            if(spirit.identifiedBy(inputData)){
                return spirit;
            }
        }
        return null;
    }


    public String getSpiritsAsString(){
        return worldSpirits.toString();
    }

    private static ArrayList<String> possibleItems = new ArrayList<>();
    static{
        possibleItems.add(Items.BLAZE_POWDER.getDescriptionId());
        possibleItems.add(Items.GLISTERING_MELON_SLICE.getDescriptionId());
        possibleItems.add(Items.APPLE.getDescriptionId());
        possibleItems.add(Items.IRON_NUGGET.getDescriptionId());
        possibleItems.add(Items.GOLD_INGOT.getDescriptionId());
        possibleItems.add(Items.RABBIT_FOOT.getDescriptionId());
        possibleItems.add(Items.INK_SAC.getDescriptionId());
        possibleItems.add(Items.AMETHYST_SHARD.getDescriptionId());
        possibleItems.add(Items.EGG.getDescriptionId());
        possibleItems.add(Items.DIAMOND.getDescriptionId());
        possibleItems.add(Items.BONE.getDescriptionId());
    }

    public static List<String> getRandomItems(int n){
        return getRandomSample(possibleItems, n);
    }

    public static void saveStringList(CompoundTag tag, String key, List<String> strings) {
        ListTag listTag = new ListTag();
        for (String s : strings) {
            listTag.add(StringTag.valueOf(s));
        }
        tag.put(key, listTag);
    }

    public static List<String> loadStringList(CompoundTag tag, String key) {
        List<String> result = new ArrayList<>();
        if (tag.contains(key, 9)) { // 9 = ListTag
            ListTag listTag = tag.getList(key, 8); // 8 = StringTag
            for (int i = 0; i < listTag.size(); i++) {
                result.add(listTag.getString(i));
            }
        }
        return result;
    }

    public static <T> List<T> getRandomSample(List<T> list, int n) {
        if (n > list.size()) {
            throw new IllegalArgumentException("Sample size cannot be greater than list size.");
        }

        List<T> copy = new ArrayList<>(list); // Copy to avoid modifying original list
        Collections.shuffle(copy);            // Shuffle the copy
        return copy.subList(0, n);            // Return first n elements
    }

    public static class SpiritHelper {

        public static ArrayList<Supplier<ResponseCriteria>> randomCriteria = new ArrayList<>();
        public static ArrayList<Supplier<SpiritResponse>> randomResponses = new ArrayList<>();

        static{
            randomCriteria.add(() -> new PathwayCriteria(0));
            randomCriteria.add(() -> new OfferingsCriteria(new ArrayList<>()));
            randomCriteria.add(() -> new SequenceLevelCriteria(0));

            randomResponses.add(() -> new HurtResponse(false, 1));
            randomResponses.add(() -> new AidResponse(0, false));
            randomResponses.add(() -> new NegativeEffectResponse(false, 1));
        }

        public static EvilSpirit createRandomSpirit(){
            List<ResponseCriteria> criteria = RitualSpiritsSaveData.getRandomSample(randomCriteria, 2)
                    .stream().map(Supplier::get).toList();
            List<SpiritResponse> response = RitualSpiritsSaveData.getRandomSample(randomResponses, 2)
                    .stream().map(Supplier::get).toList();

            criteria = criteria.stream().map(crit ->{
                if(crit instanceof RandomizableCriteria randomizer){
                    return randomizer.getRandom();
                } else return crit;
            }).toList();

            response = response.stream().map(resp ->{
                if(resp instanceof RandomizableResponse randomizer){
                    return randomizer.getRandom();
                } else return resp;
            }).toList();

            List<String> itemIds = RitualSpiritsSaveData.getRandomItems(2);

            RitualResponseLogic logic = new RitualResponseLogic(criteria.get(0), criteria.get(1), response.get(0), response.get(1));
            return new EvilSpirit(logic, itemIds);
        }

        public static void saveEvilSpiritList(CompoundTag tag, String key, List<EvilSpirit> spirits) {
            ListTag listTag = new ListTag();
            for (EvilSpirit spirit : spirits) {
                listTag.add(spirit.saveToNBT());
            }
            tag.put(key, listTag);
        }

        public static List<EvilSpirit> loadEvilSpiritList(CompoundTag tag, String key) {
            List<EvilSpirit> result = new ArrayList<>();
            if (tag.contains(key, 9)) { // 9 = ListTag
                ListTag list = tag.getList(key, 10); // 10 = CompoundTag
                for (int i = 0; i < list.size(); i++) {
                    CompoundTag spiritTag = list.getCompound(i);
                    result.add(EvilSpirit.fromNBT(spiritTag));
                }
            }
            return result;
        }

    }
}
