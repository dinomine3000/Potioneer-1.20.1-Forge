package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderVelocityEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class VelocityAbility extends PassiveAbility {

    public static final Function<Integer, Integer> levelToMaxMovement = level -> Math.max(1, 9-level);
    public static final Function<Integer, Integer> levelToMaxAttack = level -> Math.max(1, 9-level);

    public VelocityAbility(int sequence){
//        this.info = new AbilityInfo(5, 32, "Mining Speed", sequence, 0, this.getMaxCooldown(), "mining");
        super(sequence, BeyonderEffects.WHEEL_VELOCITY, level -> "velocity");
        CompoundTag tag = new CompoundTag();
        tag.putInt("movement", 1);
        tag.putInt("attack", 1);
        setDataSilent(tag);
    }

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int cost = cost();
        BeyonderVelocityEffect eff = (BeyonderVelocityEffect) effect.createInstance(sequenceLevel, cost, -1, true);
        CompoundTag tag = getData();
        boolean flag = false;
        if(!tag.contains("movement")){
            tag.putInt("movement", 1);
            flag = true;
        }
        if(!tag.contains("attack")){
            tag.putInt("attack", 1);
            flag = true;
        }
        if(flag)
            setData(tag, target);
        eff.attackSpeed = getData().getInt("attack");
        eff.movementSpeed = getData().getInt("movement");
        return eff;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        CompoundTag data = getData();
        int currentAttack = data.getInt("attack");
        int max = levelToMaxAttack.apply(getSequenceLevel());
        int newAttack = (int) AbilityFunctionHelper.incrementThroughRange(1, max, max - 1, currentAttack);
        if(cap.getEffectsManager().hasEffect(effect.getEffectId(), getSequenceLevel())){
            BeyonderEffect eff = cap.getEffectsManager().getEffect(effect.getEffectId(), getSequenceLevel());
            if(eff instanceof BeyonderVelocityEffect velocityEffect){
                velocityEffect.attackSpeed = newAttack;
            }
        }
        data.putInt("attack", newAttack);
        setData(data, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.attack_speed_set", newAttack));
        return true;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        CompoundTag data = getData();
        int currentMovement = data.getInt("movement");
        int max = levelToMaxMovement.apply(getSequenceLevel());
        int newMovement = (int) AbilityFunctionHelper.incrementThroughRange(1, max, max - 1, currentMovement);
        if(cap.getEffectsManager().hasEffect(effect.getEffectId(), getSequenceLevel())){
            BeyonderEffect eff = cap.getEffectsManager().getEffect(effect.getEffectId(), getSequenceLevel());
            if(eff instanceof BeyonderVelocityEffect velocityEffect){
                velocityEffect.movementSpeed = newMovement;
            }
        }
        data.putInt("movement", newMovement);
        setData(data, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.movement_speed_set", newMovement));
        return true;
    }
}
