package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordingAbility extends AbilityWithOptions {

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        List<AbilityRepr> existingAbls = getAbilitiesInData();
        for(AbilityRepr abl: existingAbls){
            Ability toAdd = Abilities.getFactoryAndConstruct(abl.ablId, abl.sequenceLevel, AbilityInfo.Group.RECORDED);
            if(toAdd == null) continue;
            toAdd.setInstanceId(abl.instanceId);
            cap.getAbilitiesManager().addAndInitializeAbility(toAdd, cap, target, true, true);
        }
        setEnabled(cap, target, false);
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        flipEnable(cap, target);
        return true;
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        super.passive(cap, target);
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        if(target.level().isClientSide()) return true;
        UUID ablId = UUID.fromString(args);
        return cap.getAbilitiesManager().useAbility(cap, target, ablId, true, true, new CompoundTag());
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "recording";
    }

    public void onAbilityCast(LivingEntity thisOwner, BeyonderCapability cap, Ability abilityCast){
        if(thisOwner.level().isClientSide()) return;
        List<AbilityRepr> existingAbls = getAbilitiesInData();
        if(abilityCast.isOfGroup(AbilityInfo.Group.RECORDED)){
            if(existingAbls.removeIf(repr -> repr.instanceId.equals(abilityCast.getInstanceId()))){
                cap.getAbilitiesManager().removeAbility(abilityCast.getInstanceId(), cap, thisOwner, true);
                thisOwner.sendSystemMessage(Component.literal("Consumed ability: " + abilityCast.getMainDescId()));
                saveAbilitiesToData(existingAbls, thisOwner);
                return;
            }
        } else {
            if(!isEnabled()) return;
            if(abilityCast.is(Abilities.RECORDING)) return;
            if(existingAbls.size() > 5) return;

            ResourceLocation ablId = abilityCast.getAbilityId();
            int lvl = abilityCast.getSequenceLevel();
            Ability toAdd = Abilities.getFactoryAndConstruct(ablId, lvl, AbilityInfo.Group.RECORDED);
            if(toAdd != null) {
                existingAbls.add(new AbilityRepr(toAdd.getInstanceId(), ablId, lvl));
                saveAbilitiesToData(existingAbls, thisOwner);
                cap.getAbilitiesManager().addAndInitializeAbility(toAdd, cap, thisOwner, true, true);
                thisOwner.sendSystemMessage(Component.literal("Reorded abiity: " + toAdd.getMainDescId()));
                setEnabled(cap, thisOwner, false);
                putOnCooldown(20*5, thisOwner);
            }
        }
    }

    @Override
    public void onAbilityRemoved(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        List<AbilityRepr> existingAbls = getAbilitiesInData();
        for(AbilityRepr abl: existingAbls){
            cap.getAbilitiesManager().removeAbility(abl.instanceId, cap, target, true);
        }
    }

    private void saveAbilitiesToData(List<AbilityRepr> abls, LivingEntity target){
        CompoundTag dataTag = getData();
        ListTag ablList = new ListTag();
        for(AbilityRepr abl: abls) ablList.add(abl.saveToTag(new CompoundTag()));
        dataTag.put("recordedAbilities", ablList);
        setData(dataTag, target);
        buildOptions();
    }

    private List<AbilityRepr> getAbilitiesInData(){
        CompoundTag dataTag = getData();
        ListTag tagList = dataTag.getList("recordedAbilities", Tag.TAG_COMPOUND);
        List<AbilityRepr> res = new ArrayList<>();
        for (int i = 0; i < tagList.size(); i++) {
            res.add(AbilityRepr.fromTag(tagList.getCompound(i)));
        }
        return res;
    }

    /*
    *
    * tag has to contain:
    *  - ability id
    *  - ability instance id
    *  - ability sequence level
    *
    */

    @Override
    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {
        buildOptions();
    }


    private void buildOptions(){
        AbilityOptions options = new AbilityOptions();
        for(AbilityRepr abl: getAbilitiesInData()){
            options.addEmptyOption(abl.instanceId.toString(), Ability.getNameComponent(Abilities.getFactory(abl.ablId).get().construct(abl.sequenceLevel, AbilityInfo.Group.RECORDED).getMainDescId()));
        }
        setSecondaryOptions(options);
    }

    private record AbilityRepr(UUID instanceId, ResourceLocation ablId, int sequenceLevel){
        public CompoundTag saveToTag(CompoundTag tag){
            tag.putUUID("instanceId", instanceId);
            tag.putString("ablId", ablId.toString());
            tag.putInt("level", sequenceLevel);
            return tag;
        }

        public static AbilityRepr fromTag(CompoundTag tag){
            return new AbilityRepr(tag.getUUID("instanceId"), new ResourceLocation(tag.getString("ablId")), tag.getInt("level"));
        }
    }
}
