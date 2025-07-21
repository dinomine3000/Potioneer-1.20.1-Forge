package net.dinomine.potioneer.savedata;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.spirits.EvilSpirit;
import net.dinomine.potioneer.rituals.spirits.defaultGods.WheelOfFortuneResponse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.List;

public class RitualSpiritsSaveData extends SavedData {
    public static final WheelOfFortuneResponse WHEEL_OF_FORTUNE = new WheelOfFortuneResponse();

    private List<EvilSpirit> worldSpirits;

    public void findSpiritForRitual(RitualInputData inputData){
        if(WHEEL_OF_FORTUNE.identifiedBy(inputData)){

        }
    }

    private RitualSpiritsSaveData(ServerLevel level){

    }

    @Override
    public CompoundTag save(CompoundTag compoundTag) {
    }


    public static RitualSpiritsSaveData load(CompoundTag nbt, Level level){
    }

    public static RitualSpiritsSaveData from(ServerLevel level){
        return level.getServer().overworld().getDataStorage().computeIfAbsent((tag) -> load(tag, level),
                () -> new RitualSpiritsSaveData(level), "potioneer_rituals");
    }
}
