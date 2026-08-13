package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.client.screen.ContractScreen;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class ContractViewAbility extends Ability {
    private ContractAbility.ContractOption condition;
    private ContractAbility.ContractOption reward;

    public void setConditions(ContractAbility.ContractOption condition, ContractAbility.ContractOption reward){
        this.condition = condition;
        this.reward = reward;
        CompoundTag tag = new CompoundTag();
        tag.put("condition", this.condition.saveToNbt());
        tag.put("reward", this.reward.saveToNbt());
        setDataSilent(tag);
    }

    public ContractViewAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(!target.level().isClientSide()) return false;
        condition = ContractAbility.ContractOption.loadFromNbt(getData().getCompound("condition")).get();
        reward = ContractAbility.ContractOption.loadFromNbt(getData().getCompound("reward")).get();
        if(condition == null || reward == null) return false;
        ContractScreen.viewExistingContract(condition, reward, target.getId());
        return false;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "contract_view";
    }
}
