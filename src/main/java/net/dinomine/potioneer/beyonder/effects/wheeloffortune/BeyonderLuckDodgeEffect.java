package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class BeyonderLuckDodgeEffect extends BeyonderEffect {
    public static float dodgeChance = 0.2f;
    public static int luckCost = 10;
    public static int luckGain = 0;
    public static final double dodgeMag = 1d;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        if(victimCap.getLuckManager().passesLuckCheck(dodgeChance, luckCost, luckGain, victim.getRandom())){
            RandomSource random = victim.getRandom();
            victim.level().playSound(null, victim.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS, 0.6f, (float) random.triangle(1, 0.2));

            Vec3 orthogonal = new Vec3(attacker.getX() - victim.getX(), 0, attacker.getZ() - victim.getZ());
            if(orthogonal.length() < 0.1) orthogonal = victim.getLookAngle();
            double b1 = random.nextBoolean() ? 1 : -1;
            Vec3 dodgeDir = new Vec3(orthogonal.x*b1, 0, -1*b1*orthogonal.x/orthogonal.z).normalize().scale(dodgeMag).offsetRandom(random, 0.2f);
//            System.out.println("Client Side: " + victim.level().isClientSide());
//            System.out.println("b1: " + b1);
//            System.out.println("Orthogonal: " + orthogonal);
//            System.out.println("Dodge dir: " + dodgeDir);
//            System.out.println("Attacker pos: " + attacker.getPosition(0));
//            System.out.println("Victim pos: " + victim.getPosition(0));
            AbilityFunctionHelper.pushEntity(victim, dodgeDir);

            if(event.getSource().getDirectEntity().is(new Arrow(victim.level(), 0, 0, 0))) event.getSource().getDirectEntity().kill();
            return true;
        }
        return false;
    }
}
