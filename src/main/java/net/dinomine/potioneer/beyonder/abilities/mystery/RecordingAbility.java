package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class RecordingAbility extends AbilityWithOptions {
    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        return super.primary(cap, target);
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        return false;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "recording";
    }

    public void onAbilityCast(LivingEntity thisOwner, BeyonderCapability cap, Ability abilityCast){
        /*if(abilityCast.getAbilityKey().isOwnedBy(getInstanceId())){
            cap.getAbilitiesManager().removeAbility(abilityCast.getAbilityKey(), cap, thisOwner, true);
        } else {
            AbilityKey newKey = new AbilityKey(PlayerAbilitiesManager.AbilityList.RECORDED.name(), abilityCast.getAbilityId(), abilityCast.getSequenceLevel()).markOwnedBy(getInstanceId());
            Ability toGive = Abilities.createAbilityInstance(newKey);
            cap.getAbilitiesManager().addAndInitializeAbility(newKey, toGive, cap, thisOwner, true, true);
        }*/
    }

    @Override
    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {

    }


    private AbilityOptions buildOptions(){
        return new AbilityOptions();
    }
}
