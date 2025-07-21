package net.dinomine.potioneer.rituals.spirits;

import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.RitualResponseLogic;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class RitualSpiritResponse {
    protected RitualResponseLogic responseLogic;

    public RitualSpiritResponse(@Nullable RitualResponseLogic responseLogic){
        this.responseLogic = responseLogic;
    }

    protected void setLogic(RitualResponseLogic newLogic){
        responseLogic = newLogic;
    }

    public void respondTo(RitualInputData inputData){
        responseLogic.onRitualToEntity(inputData);
    }

    public boolean identifiedBy(RitualInputData data){
        return isValidIncense(data.incense()) || isValidItems(data.offerings());
    }

    protected void defaultNormalResponse(RitualInputData inputData){
        switch (inputData.action()){
            case AID -> aidTarget(inputData);
            case GUIDANCE -> guideTarget(inputData);
            case IMBUEMENT -> inbueTarget(inputData);
            default -> defaultMethod(inputData);
        }
    }

    protected void defaultMethod(RitualInputData inputData){
    }

    protected void aidTarget(RitualInputData inputData){
    }

    protected void guideTarget(RitualInputData inputData){
    }

    protected void inbueTarget(RitualInputData inputData){
    }

    public abstract boolean isValidIncense(String incenseId);

    public abstract boolean isValidItems(List<ItemStack> items);
}
