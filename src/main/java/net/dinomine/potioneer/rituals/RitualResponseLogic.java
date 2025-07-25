package net.dinomine.potioneer.rituals;

import net.dinomine.potioneer.rituals.criteria.OrCriteria;
import net.dinomine.potioneer.rituals.criteria.ResponseCriteria;
import net.dinomine.potioneer.rituals.responses.SpiritResponse;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;

import java.util.List;

public class RitualResponseLogic {
    ResponseCriteria responseCriteria;
    ResponseCriteria punishmentCriteria;
    SpiritResponse punishmentResponse;
    SpiritResponse normalResponse;

    public RitualResponseLogic(List<ResponseCriteria> punishmentCriteria, List<ResponseCriteria> responseCriteria,
                               SpiritResponse punishmentLogic, SpiritResponse responseLogic){
        this(new OrCriteria(punishmentCriteria), new OrCriteria(responseCriteria), punishmentLogic, responseLogic);
    }

    public RitualResponseLogic(ResponseCriteria punishmentCriteria, ResponseCriteria responseCriteria,
                               SpiritResponse punishmentLogic, SpiritResponse responseLogic){
        this.responseCriteria = responseCriteria;
        this.punishmentCriteria = punishmentCriteria;
        this.punishmentResponse = punishmentLogic;
        this.normalResponse = responseLogic;
    }

    public void onRitualToEntity(RitualInputData inputData, Level level){
        //check for punishments first
        if(punishmentCriteria.checkCondition(inputData, level)){
            castPunishment(inputData, level);
            return;
        }

        //check for successful responses
        if(responseCriteria.checkCondition(inputData, level)){
            castResponse(inputData, level);
        }

    }

    private void castResponse(RitualInputData inputData, Level level) {
        normalResponse.enactResponse(inputData, level);
    }

    private void castPunishment(RitualInputData inputData, Level level){
        punishmentResponse.enactResponse(inputData, level);
    }

    public CompoundTag saveToNBT(){
        Tag punishmentCriteria = this.punishmentCriteria.saveToNBT();
        Tag responseCriteria = this.responseCriteria.saveToNBT();
        Tag punishmentResponse = this.punishmentResponse.saveToNBT();
        Tag response = this.normalResponse.saveToNBT();

        CompoundTag resultTag = new CompoundTag();
        resultTag.put("punishmentCriteria", punishmentCriteria);
        resultTag.put("responseCriteria", responseCriteria);
        resultTag.put("punishmentResponse", punishmentResponse);
        resultTag.put("normalResponse", response);

        return resultTag;
    }

    public static RitualResponseLogic fromNBT(CompoundTag compoundTag){
        ResponseCriteria punishmentCriteria = ResponseCriteria.loadFromNBT(compoundTag.getCompound("punishmentCriteria"));
        ResponseCriteria responseCriteria = ResponseCriteria.loadFromNBT(compoundTag.getCompound("responseCriteria"));
        SpiritResponse punishmentResponse = SpiritResponse.loadFromNBT(compoundTag.getCompound("punishmentResponse"));
        SpiritResponse normalResponse = SpiritResponse.loadFromNBT(compoundTag.getCompound("normalResponse"));
        return new RitualResponseLogic(punishmentCriteria, responseCriteria, punishmentResponse, normalResponse);
    }

    @Override
    public String toString() {
        return saveToNBT().getAsString();
    }
}
