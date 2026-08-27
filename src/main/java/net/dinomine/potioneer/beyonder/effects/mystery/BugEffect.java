package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.mystery.BugAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

public class BugEffect extends BeyonderEffect {
    private UUID castingInstanceId = null;
    private UUID casterId = null;

    public static BugEffect createInstance(UUID ablId, UUID casterId, int level){
        BugEffect eff = (BugEffect) BeyonderEffects.MYSTERY_BUG.createInstance(level, -1, false);
        eff.castingInstanceId = ablId;
        eff.casterId = casterId;
        return eff;
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(castingInstanceId == null) endEffectWhenPossible();
        if(casterId == null) endEffectWhenPossible();
        if(target.level().isClientSide()) return;
        ServerLevel level = (ServerLevel) target.level();
        Entity caster = AbilityFunctionHelper.getEntityAcrossDimensions(level, casterId);
        if(caster == null) return;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(caster);
        if(optCap.isEmpty()){
            endEffectWhenPossible();
            return;
        }
        BeyonderCapability casterCap = optCap.get();
        Ability abl = casterCap.getAbilitiesManager().getAbilityById(castingInstanceId);
        if(!(abl instanceof BugAbility bugAbility)) {
            endEffectWhenPossible();
            return;
        }
        if(!bugAbility.isBug(target.getUUID())) endEffectWhenPossible();
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void onDie(BeyonderCapability cap, LivingEntity target) {
        if(castingInstanceId == null || casterId == null) return;
        if(target.level().isClientSide()) return;
        ServerLevel level = (ServerLevel) target.level();
        Entity ent = AbilityFunctionHelper.getEntityAcrossDimensions(level, casterId);
        if(!(ent instanceof LivingEntity caster)) return;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(caster);
        if(optCap.isEmpty()) return;
        BeyonderCapability casterCap = optCap.get();
        Ability abl = casterCap.getAbilitiesManager().getAbilityById(castingInstanceId);
        if(!(abl instanceof BugAbility bugAbility)) return;
        if(bugAbility.isBug(target.getUUID())) bugAbility.clearBug(level, caster);
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putUUID("casterId", casterId);
        nbt.putUUID("ablId", castingInstanceId);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.casterId = nbt.getUUID("casterId");
        this.castingInstanceId = nbt.getUUID("ablId");
    }

    public boolean isBugOf(UUID casterId) {
        return this.casterId != null && this.casterId.equals(casterId);
    }
}
