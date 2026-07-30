package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.PhasingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.dinomine.potioneer.mob_effects.ServerEffectVisualHandling;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

import java.util.Optional;

public class MistEffect extends BeyonderEffect {
    private boolean wasFlyingBefore = false;
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
//        if(target instanceof Player player){
//            player.setForcedPose(Pose.);
//        }
        if(target.level().isClientSide()) return;
        ServerEffectVisualHandling.addMistEntity(target);
        if(fromLoading || !(target instanceof Player player)) return;
        wasFlyingBefore = player.getAbilities().flying;

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(cost);
        target.addEffect(new MobEffectInstance(ModEffects.MIST_EFFECT.get(), -1, 1, false, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 1, false, false, true));
        if(target instanceof Player player){
            player.setNoGravity(true);
            player.setArrowCount(0);
            if(!player.level().isClientSide() && !player.getAbilities().flying){
                player.getAbilities().flying = true;
                player.onUpdateAbilities();
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.INVISIBILITY);
        target.removeEffect(ModEffects.MIST_EFFECT.get());
        if(target instanceof Player player){
            player.getAbilities().flying = wasFlyingBefore;
            player.setNoGravity(false);
            player.setForcedPose(null);
        }
        ServerEffectVisualHandling.removeMistEntity(target);
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide() || !calledOnVictim) return false;
        return !event.getSource().is(PotioneerDamage.Tags.ABSOLUTE) && !event.getSource().is(PotioneerDamage.Tags.ANNIHILATION) && !event.getSource().is(PotioneerDamage.Tags.MENTAL)
                && (victim.getMobType() != MobType.UNDEAD || !event.getSource().is(PotioneerDamage.Tags.PURIFICATION));
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("flying", wasFlyingBefore);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        wasFlyingBefore = nbt.getBoolean("flying");
    }
}
