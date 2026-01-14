package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.savedata.RitualSpiritsSaveData;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public abstract class Deity extends EvilSpirit {

    protected int pathwayId;
    protected String incense;

    public Deity(int pathwayId, List<String> validItems, String incense){
        super();
        setupLogic();
        this.pathwayId = pathwayId;
        itemsId = validItems;
        this.incense = incense;
    }

    protected abstract void setupLogic();

    @Override
    protected void aidTarget(RitualInputData inputData) {
        int id = pathwayId/10;
        int level = inputData.pathwayId()%10;
        switch (id){
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                break;
        }
    }

    @Override
    protected void guideTarget(RitualInputData inputData) {
        if(inputData.offerings().isEmpty()) return;

    }

    @Override
    protected void imbue(RitualInputData inputData) {

    }

    @Override
    public boolean isValidIncense(String incenseId){
        return incenseId.equalsIgnoreCase(incense);
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag deityTag = new CompoundTag();
        deityTag.putInt("pathwayId", pathwayId);
        return deityTag;
    }

    public static Deity getDeityFromNBT(CompoundTag tag){
        int pathway = tag.getInt("pathwayId");
        return switch (Math.floorDiv(pathway, 10)){
            case 0 -> RitualSpiritsSaveData.WHEEL_OF_FORTUNE;
            default -> null;
        };
    }
}
