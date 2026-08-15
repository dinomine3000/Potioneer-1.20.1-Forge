package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.ModAttributes;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class GymnasticsEffect extends BeyonderEffect {
    private int jumpCount = 0;
    private static final UUID JUMP_UUID = UUID.fromString("2cbabd6d-5371-4586-80a1-52e1f1753ee2");

    public boolean canJump(){return jumpCount > 0;}

    public int onJump(LivingEntity target, BeyonderCapability cap){
        if(AbilityFunctionHelper.isEntityStandingOnGround(0, target, false)) {
            return jumpCount;
        }
        if(jumpCount < 1) return 0;
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.MYSTERY_INVISIBLE)) return --jumpCount;
        PacketHandler.sendMessageToClientsAround(target, 16, new GeneralAreaEffectMessage(
                ParticleMaker.Preset.WHOOOSH,
                target.getOnPos().getCenter().toVector3f(),
                new Vector3f(0, -0.2f, 0), 0));
        target.level().playSound(target, target.getOnPos(), ModSounds.JUMP.get(), SoundSource.PLAYERS, 1, (float) target.getRandom().triangle(1, 0.2f));
        return --jumpCount;
    }
    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        AbilityFunctionHelper.addAttributeTo(target, JUMP_UUID, "jump boost", getJumpAdd(), AttributeModifier.Operation.ADDITION, Attributes.JUMP_STRENGTH);
    }

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        //if(target.level().isClientSide() && canJump()
        //        && (!(target instanceof Player player) || !player.getAbilities().flying)) target.setOnGround(true);
        if(AbilityFunctionHelper.isEntityStandingOnGround(0, target, false)) jumpCount = getMaxJump();

    }

    private int getMaxJump(){return 1 + (9 - sequenceLevel);}

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        AbilityFunctionHelper.removeAttribute(target, JUMP_UUID, "jump boost", getJumpAdd(), AttributeModifier.Operation.ADDITION, ModAttributes.JUMP.get());
    }

    private double getJumpAdd(){
        return 0.2;
    }

    private int getResistance(){return sequenceLevel < 6 ? Integer.MAX_VALUE : 1 + 2*(9-sequenceLevel);}

    private float getMultiplier(){return sequenceLevel < 6 ? 0f : 0.1f + (sequenceLevel - 6)*0.15f;}

    @Override
    public boolean onDamageProposal(LivingAttackEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        if(!event.getSource().is(DamageTypes.FALL)) return false;
        return (!(victim instanceof Player player) || player.isCrouching()) && event.getAmount() < getResistance();
    }

    @Override
    public boolean onDamageCalculation(LivingHurtEvent event, LivingEntity victim, @Nullable LivingEntity attacker, BeyonderCapability victimCap, Optional<BeyonderCapability> attackerCap, boolean calledOnVictim) {
        if(!calledOnVictim) return false;
        if(!event.getSource().is(DamageTypes.FALL)) return false;
        event.setAmount(event.getAmount() * getMultiplier());
        return false;
    }
}
