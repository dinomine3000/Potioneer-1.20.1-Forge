package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.PhasingEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class MistEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
//        if(target instanceof Player player){
//            player.setForcedPose(Pose.);
//        }
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost(cost);
        target.addEffect(new MobEffectInstance(ModEffects.MIST_EFFECT.get(), -1, 1, false, false, true));
        target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, -1, 1, false, false, true));
        if(target instanceof Player player){
            player.setNoGravity(true);
            player.setArrowCount(0);
            player.getAbilities().flying = true;
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.removeEffect(MobEffects.INVISIBILITY);
        target.removeEffect(ModEffects.MIST_EFFECT.get());
        if(target instanceof Player player){
            player.getAbilities().flying = player.getAbilities().mayfly;
            player.setNoGravity(false);
            player.setForcedPose(null);
        }
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        if(victim.level().isClientSide() || !calledOnVictim) return false;
        return !event.getSource().is(PotioneerDamage.Tags.ABSOLUTE) && !event.getSource().is(PotioneerDamage.Tags.ANNIHILATION) && !event.getSource().is(PotioneerDamage.Tags.MENTAL)
                && (victim.getMobType() != MobType.UNDEAD || !event.getSource().is(PotioneerDamage.Tags.PURIFICATION));
    }
}
