package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class DodgeEffect extends BeyonderEffect {
    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        cap.requestPassiveSpiritualityCost((cost/100f)*cap.getMaxSpirituality());
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {

    }

    public static boolean doDodge(LivingEntity victim, LivingEntity attacker, LivingAttackEvent event, double dodgeMag){
        Entity dmgSrc = event.getSource().getDirectEntity();
        if(dmgSrc == null) dmgSrc = attacker;
        if(dmgSrc == null) return false;

        RandomSource random = victim.getRandom();
        victim.level().playSound(null, victim.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS, 0.6f, (float) random.triangle(1, 0.2));
        Vec3 attackDirection = new Vec3(dmgSrc.getX() - victim.getX(), 0, dmgSrc.getZ() - victim.getZ());
        attackDirection = attackDirection.normalize();
//            Vec3 orthogonal = victim.getLookAngle();
        Vec3 dodgeDir = PotioneerMathHelper.getRandomOrthogonalConstantY(attackDirection, random.nextBoolean(), dodgeMag).offsetRandom(random, 0.2f);
        if(victim.getDeltaMovement().length() > 0.1){
            if(dodgeDir.dot(victim.getDeltaMovement().scale(1)) < 0)
                dodgeDir = dodgeDir.scale(-1);
        }

        AbilityFunctionHelper.pushEntity(victim, dodgeDir);

        if(dmgSrc.is(new Arrow(victim.level(), 0, 0, 0))) dmgSrc.kill();
        return true;
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        if(event.getSource().is(PotioneerDamage.Tags.ABSOLUTE) || event.getSource().is(PotioneerDamage.Tags.MENTAL) || event.getSource().is(PotioneerDamage.Tags.ANNIHILATION) ) return false;
        return doDodge(victim, attacker, event, 0.3d);
    }
}
