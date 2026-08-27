package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.BugEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeMod;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

public class BugAbility extends PassiveAbility {
    private AbilityOptions pOptions = null;

    public BugAbility() {
        super(BeyonderEffects.MYSTERY_BUG_DEFENSE, ign -> "bug");
        canFlip();
        enabledOnAcquire();
    }

    @Override
    public void init() {
        super.init();
        buildPOptions();
    }

    private void buildPOptions(){
        AbilityOptions options = new AbilityOptions();
        options.addEmptyOption("plant", Component.literal("Plant bug"));
        CompoundTag dataTag = getData();
        if(dataTag.contains("bugId")) options.addEmptyOption("clear", Component.literal("Remove bug"));
        options.addEmptyOption("toggle", Component.literal("Toggle"));
        this.pOptions = options;
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity caster, CompoundTag args) {
        String input = AbilityOptionsUtil.validadeArguments(args, this, this.pOptions, caster.level().isClientSide, true);
        if(input.isEmpty()) return false;
        if(input.equalsIgnoreCase("plant")){
            if(caster.level().isClientSide) return true;
            ServerLevel level = (ServerLevel) caster.level();
            Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntity(caster, caster.getAttributeValue(ForgeMod.ENTITY_REACH.get()), true);
            if(optTarget.isEmpty()) return false;
            LivingEntity target = optTarget.get();
            CapProvider.beyonder(target).ifPresent(tarCap -> {
                if(tarCap.getEffectsManager().addEffectNoRefresh(BugEffect.createInstance(getInstanceId(), caster.getUUID(), getSequenceLevel()), tarCap, target)){
                    if(isTargetBugged(level, caster)){
                        clearBug(level, caster);
                    }
                    CompoundTag dataTag = getData();
                    dataTag.putUUID("bugId", target.getUUID());
                    setData(dataTag, caster);
                    buildPOptions();
                } else {
                    caster.sendSystemMessage(Component.literal("Failed to plant bug: entity already has a bug."));
                }
            });
        } else if(input.equalsIgnoreCase("clear")){
            if(caster.level() instanceof ServerLevel sLevel) clearBug(sLevel, caster);
            return true;
        } else if(input.equalsIgnoreCase("toggle")){
            super.primary(cap, caster);
            return true;
        }
        return false;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity caster) {
        CompoundTag tag = getData();
        if(!tag.contains("bugId")) return false;
        if(caster.level().isClientSide()) return true;
        UUID bugId = tag.getUUID("bugId");
        Entity ent = ((ServerLevel) caster.level()).getEntity(bugId);
        if(!(ent instanceof LivingEntity bugEntity)) return false;
        if(!isTargetBugged((ServerLevel) caster.level(), caster)) return false;
        BlockPos bugPos = bugEntity.getOnPos();
        BlockPos casterPos = caster.getOnPos();
        BlinkAbility.teleport(ent, (ServerLevel) caster.level(), BlinkAbility.breadthFirstSearch(casterPos, 2, caster.level()));
        BlinkAbility.teleport(caster, (ServerLevel) bugEntity.level(), BlinkAbility.breadthFirstSearch(bugPos, 2, bugEntity.level()));
        if(!shouldKeepBug(bugEntity, caster)){
            clearBug((ServerLevel) caster.level(), caster);
        }
        return true;
    }

    public boolean isBug(UUID testId){
        return getData().contains("bugId") && getData().getUUID("bugId").equals(testId);
    }

    public boolean isTargetBugged(ServerLevel level, LivingEntity caster){
        if(!getData().contains("bugId")) return false;
        Entity ent = level.getEntity(getData().getUUID("bugId"));
        if(!(ent instanceof LivingEntity lEnt) || lEnt.isDeadOrDying()) return false;
        BugEffect eff = AbilityFunctionHelper.getEffectOnTarget(BeyonderEffects.MYSTERY_BUG.getEffectId(), lEnt);
        return eff != null && eff.isBugOf(caster.getUUID());
    }

    public void clearBug(ServerLevel level, LivingEntity caster){
        if(isTargetBugged(level, caster)){
            Entity ent = level.getEntity(getData().getUUID("bugId"));
            CapProvider.beyonder(ent).ifPresent(cap -> cap.getEffectsManager().removeEffect(BeyonderEffects.MYSTERY_BUG.getEffectId()));
        }
        CompoundTag tag = getData();
        tag.remove("bugId");
        setData(tag, caster);
        buildPOptions();
    }

    private static boolean shouldKeepBug(LivingEntity bugEntity, LivingEntity caster){
        //if allies, always keep.
        if(AbilityFunctionHelper.areEntitiesAllies(bugEntity, caster)) return true;
        //if the bug has more health than the caster, then cut the bug
        if(bugEntity.getMaxHealth() > caster.getHealth()) return false;

        Optional<BeyonderCapability> bugCap = CapProvider.beyonder(bugEntity);
        Optional<BeyonderCapability> casterCap = CapProvider.beyonder(caster);
        //if for some reason neither have a capability, keep it.
        if(bugCap.isEmpty() || casterCap.isEmpty()) return true;
        //if the bug isnt a beyonder (so most living entities that arent a player, or beyonderless players) keep it, since theyre weak
        if(!bugCap.get().isBeyonder()) return true;
        //in the case of the bug being a beyonder, dont keep it if it is of a higher sequence level
        if(bugCap.get().getSequenceLevel() < casterCap.get().getSequenceLevel()) return false;
        //in the case of being the same level or lower, keep it if the bug has less spirituality than the caster.
        return bugCap.get().getSpirituality() < casterCap.get().getSpirituality();
    }

    @Override
    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {
        buildPOptions();
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "bug";
    }

    public LivingEntity getBug(ServerLevel level) {
        if(!getData().contains("bugId")) return null;
        return (LivingEntity) AbilityFunctionHelper.getEntityAcrossDimensions(level, getData().getUUID("bugId"));
    }
}
