package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.entities.custom.CloneEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

import static net.dinomine.potioneer.beyonder.abilities.mystery.CloneAbility.MAX_CLONE_DIST;

public class IllusionEffect extends BeyonderEffect {
    private UUID cloneId = null;

    @Override
    protected void setPriority(int sequenceLevel) {
        this.priority = Priority.VERY_LOW;
    }

    public void setClone(CloneEntity clone){cloneId = clone.getUUID();}
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(!(target instanceof Player player) || tooFar(getClone((ServerLevel) player.level(), player.getUUID(), false), player)) invalidateEffect(target);
    }

    public static boolean tooFar(CloneEntity clone, Player player){
        if(clone == null || player == null || !AbilityFunctionHelper.hasEffect(BeyonderEffects.MYSTERY_ILLUSION.getEffectId(), player)) return true;
        return clone.distanceTo(player) > MAX_CLONE_DIST.get();
    }

    private CloneEntity getClone(ServerLevel level, UUID playerId, boolean acrossDimensions){
        Entity ent = acrossDimensions ? AbilityFunctionHelper.getEntityAcrossDimensions(level, this.cloneId) : level.getEntity(this.cloneId);
        if(ent instanceof CloneEntity clone && clone.isCloneOf(playerId)) return clone;
        return null;
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
    }

    public void invalidateEffect(LivingEntity target){
        if(target.level().isClientSide()) return;
        CloneEntity clone = getClone((ServerLevel) target.level(), target.getUUID(), true);
        if(clone == null) {
            endEffectWhenPossible();
            return;
        }
        clone.remove(Entity.RemovalReason.DISCARDED);
    }


    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        invalidateEffect(victim);
        return true;
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putUUID("cloneId", cloneId);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.cloneId = nbt.getUUID("cloneId");
    }
}
