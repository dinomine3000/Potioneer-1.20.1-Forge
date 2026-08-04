package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;

public class MistBlinkingAnchorsAbility extends AbilityWithOptions {
    public MistBlinkingAnchorsAbility(int sequenceLevel) {
        super(sequenceLevel);
        AbilityOptions sOptions = new AbilityOptions()
                .addEmptyOption("clear", Component.literal("Clear Anchors"))
                .addEmptyOption("anchor1", Component.literal("Set Anchor 1"))
                .addEmptyOption("anchor2", Component.literal("Set Anchor 2"))
                .addEmptyOption("anchor3", Component.literal("Set Anchor 3"))
                .addEmptyOption("anchor4", Component.literal("Set Anchor 4"));
        setSecondaryOptions(sOptions);
        withCost(10);
    }

    @Override
    protected void loadExtraNbtInfo(CompoundTag tag) {
        super.loadExtraNbtInfo(tag);
        setPrimaryOptions(createPOptions());
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(cap.getSpirituality() < cost()) return false;
        CompoundTag anchorTag = getData().getCompound(args);
        if(anchorTag.isEmpty()) return false;
        if(target.level().isClientSide()) return true;
        BlockPos anchorPos = new BlockPos(anchorTag.getInt("x"), anchorTag.getInt("y"), anchorTag.getInt("z"));
        if(!AreaOfJurisdictionAbility.isPosInAOJ(anchorPos, target)) return false;
        MistBlinkingAbility.doMistBlinkingTo(target, cap, (ServerLevel) target.level(), cost(), anchorPos.above(), sequenceLevel);
        return true;
    }

    @Override
    protected boolean secondaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("clear")) {
            setData(new CompoundTag(), target);
            setPrimaryOptions(createPOptions());
            return true;
        }
        BlockPos anchor = target.getOnPos();
        CompoundTag anchorTag = new CompoundTag();
        anchorTag.putInt("x", anchor.getX());
        anchorTag.putInt("y", anchor.getY());
        anchorTag.putInt("z", anchor.getZ());
        CompoundTag dataTag = getData();
        dataTag.put(args, anchorTag);
        setData(dataTag, target);
        setPrimaryOptions(createPOptions());
        return true;
    }

    private AbilityOptions createPOptions(){
        CompoundTag dataTag = getData();
        AbilityOptions pOptions = new AbilityOptions();
        for(int i = 1; i <= 4; i++){
            if(!dataTag.contains("anchor" + i)) break;
            pOptions.addEmptyOption("anchor" + i, Component.literal("Teleport to anchor " + i));
        }
        return pOptions;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "mist_blinking_anchors";
    }
}
