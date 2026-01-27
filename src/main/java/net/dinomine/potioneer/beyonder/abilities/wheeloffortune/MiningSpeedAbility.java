package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderMiningSpeedEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class MiningSpeedAbility extends PassiveAbility {

    public static final Function<Integer, Float> levelToMaxSpeed = level -> 1.5f + 1.5f*(9-level);

    public MiningSpeedAbility(int sequence){
//        this.info = new AbilityInfo(5, 32, "Mining Speed", sequence, 0, this.getMaxCooldown(), "mining");
        super(sequence, BeyonderEffects.WHEEL_MINING, level -> "mining_" + (level < 8 ? "2" : "1"));
        CompoundTag tag = new CompoundTag();
        tag.putFloat("speed", levelToMaxSpeed.apply(sequence%10));
        setDataSilent(tag);
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(newLevel > 6 && oldLevel != newLevel){
            CompoundTag tag = getData();
            tag.putFloat("speed", levelToMaxSpeed.apply(newLevel));
            setData(tag, target);
        }
    }

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        BeyonderMiningSpeedEffect eff = (BeyonderMiningSpeedEffect) effect.createInstance(sequenceLevel, cost(), -1, true);
        CompoundTag tag = getData();
        if(!tag.contains("speed")){
            tag.putFloat("speed", levelToMaxSpeed.apply(getSequenceLevel()));
            setData(tag, target);
        }
        eff.miningSpeed = getData().getFloat("speed");
        return eff;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(getSequenceLevel() >= 8) {
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        if(target.level().isClientSide()) return false;
        CompoundTag data = getData();
        float currentSpeed = data.getFloat("speed");
        float newSpeed = AbilityFunctionHelper.incrementThroughRange(1, levelToMaxSpeed.apply(getSequenceLevel()), 5, currentSpeed);
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_MINING.getEffectId(), getSequenceLevel())){
            BeyonderEffect eff = cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_MINING.getEffectId(), getSequenceLevel());
            if(eff instanceof BeyonderMiningSpeedEffect miningSpeedEffect){
                miningSpeedEffect.miningSpeed = newSpeed;
            }
        }
        data.putFloat("speed", newSpeed);
        setData(data, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.mining_speed_set", Math.round(newSpeed*100)/100f));
        putOnCooldown(target);
        return true;
    }
}
