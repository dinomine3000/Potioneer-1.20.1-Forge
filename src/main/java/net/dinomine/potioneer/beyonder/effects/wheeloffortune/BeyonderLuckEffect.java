package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.MarkedProjectile;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

import java.util.List;
import java.util.Optional;

public class BeyonderLuckEffect extends BeyonderEffect {
    public static final float dodgeChance = 0.2f;
    public static final float arrowDodgeMagnitude = 1f;
    public static final int dodgeLuckCost = 10;
    public static final int dodgeLuckGain = 0;
    private static final float critBaseChance = 0.3f;
    public static final double dodgeMag = 1d;
    public static final int critLuckCost = 5;

    private boolean crit = false;

    public BeyonderLuckEffect withCrit(){
        this.crit = true;
        return this;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel > 7) return;
        if(target.tickCount%10 == target.getId()%10){
            //arrows avoid target
            List<Entity> entities = AbilityFunctionHelper.getEntitiesAroundPredicate(target, 15, ent -> ent instanceof Projectile);
            if(entities.isEmpty()) return;
            for(Entity ent: entities){
                if(ent instanceof Projectile projectile && (projectile.getOwner() == null || projectile.getOwner().getId() != target.getId())){
//                    if(ent instanceof AbstractArrow absArrow)
//                        System.out.println(absArrow.inGround);
                    if(ent instanceof AbstractArrow absArrow && absArrow.inGround) continue;
                    if(projectile instanceof MarkedProjectile dProjectile){
                        Vec3 arrowMovement = projectile.getDeltaMovement().scale(15);
                        Vec3 arrowEndpoint = projectile.position().add(arrowMovement);
                        if(target.getBoundingBox().inflate(0.5).intersects(projectile.position(), arrowEndpoint)){
                            target.level().playSound(null, target.getOnPos(), ModSounds.ARROW_MISS.get(), SoundSource.PLAYERS, 0.4f, (float) target.getRandom().triangle(1, 0.2));
                            Vec3 orthogonal = PotioneerMathHelper.getRandomOrthogonalConstantY(arrowMovement.with(Direction.Axis.Y, 0),
                                    true, arrowDodgeMagnitude);
                            if(orthogonal.dot(target.position().subtract(projectile.position())) > 0)
                                orthogonal = orthogonal.scale(-1);
                            Vec3 newDelta = orthogonal.add(projectile.getDeltaMovement().scale(0.5));
                            projectile.setDeltaMovement(newDelta);
                            //                        projectile.addDeltaMovement(projectile.position().subtract(target.position()).normalize().scale(2));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public boolean onTakeDamage(LivingDamageEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, Optional<LivingEntityBeyonderCapability> optAttackerCap, boolean calledOnVictim) {
        //Crit Damage dealt
        if(optAttackerCap.isEmpty() || event.getAmount() == 0 || calledOnVictim || !crit) return false;
        LivingEntityBeyonderCapability attackerCap = optAttackerCap.get();
        if(attackerCap.getLuckManager().passesLuckCheck(critBaseChance, critLuckCost, 0, attacker.getRandom())){
            attackerCap.getCharacteristicManager().progressActing(WheelOfFortunePathway.LUCK_ACTING_INC, 6);
//            victimCap.getEffectsManager().removeEffect(BeyonderEffects.WHEEL_CRIT.getEffectId(), getSequenceLevel());
            BeyonderCritEffect eff = (BeyonderCritEffect) BeyonderEffects.WHEEL_CRIT.createInstance(getSequenceLevel(), 0, 15, true);
            eff.setValues(attacker.getId(), event.getAmount());
            victimCap.getEffectsManager().addOrReplaceEffect(eff, victimCap, victim);
        }
        return false;
    }

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, LivingEntity attacker, LivingEntityBeyonderCapability victimCap, LivingEntityBeyonderCapability attackerCap, boolean calledOnVictim) {
        //Dodge Damage received
        if(!calledOnVictim || event.getSource().is(PotioneerDamage.Tags.ABSOLUTE)) return false;
        if(victimCap.getLuckManager().passesLuckCheck(sequenceLevel < 5 ? 0.4f : dodgeChance, dodgeLuckCost, dodgeLuckGain, victim.getRandom())){
            victimCap.getCharacteristicManager().progressActing(WheelOfFortunePathway.LUCK_ACTING_INC, 6);
            RandomSource random = victim.getRandom();
            victim.level().playSound(null, victim.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS, 0.6f, (float) random.triangle(1, 0.2));

            Entity dmgSrc = event.getSource().getDirectEntity();
            if(dmgSrc == null) dmgSrc = attacker;
            Vec3 attackDirection = new Vec3(dmgSrc.getX() - victim.getX(), 0, dmgSrc.getZ() - victim.getZ());
            attackDirection = attackDirection.normalize();
//            Vec3 orthogonal = victim.getLookAngle();
            Vec3 dodgeDir = PotioneerMathHelper.getRandomOrthogonalConstantY(attackDirection, random.nextBoolean(), dodgeMag).offsetRandom(random, 0.2f);
            if(victim.getDeltaMovement().length() > 0.1){
                if(dodgeDir.dot(victim.getDeltaMovement().scale(1)) < 0)
                    dodgeDir = dodgeDir.scale(-1);
            }

            AbilityFunctionHelper.pushEntity(victim, dodgeDir);

            if(event.getSource().getDirectEntity().is(new Arrow(victim.level(), 0, 0, 0))) event.getSource().getDirectEntity().kill();
            return true;
        }
        return false;
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putBoolean("crit", crit);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.crit = nbt.getBoolean("crit");
    }
}
