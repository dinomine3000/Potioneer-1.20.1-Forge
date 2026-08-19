package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptionsUtil;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.MiningSpeedEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class MiningSpeedAbility extends PassiveAbility {
    private AbilityOptions speedOptions = null;

    public static final Function<Integer, Float> levelToMaxSpeed = level -> 1.5f + 1.5f*(9-level);

    public MiningSpeedAbility(){
        super(BeyonderEffects.WHEEL_MINING, level -> "mining_" + (level < 8 ? "2" : "1"));
    }

    @Override
    public void init() {
        CompoundTag tag = new CompoundTag();
        float maxSpeed = levelToMaxSpeed.apply(getSequenceLevel()%10);
        tag.putFloat("speed", maxSpeed);
        setDataSilent(tag);
        updateOptions(maxSpeed);
    }

    @Override
    protected boolean hasSecondary(int level) {
        return level < 8;
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {
        float maxSpeed = levelToMaxSpeed.apply(newLevel);
        if(newLevel > 7 && oldLevel != newLevel){
            CompoundTag tag = getData();
            tag.putFloat("speed", maxSpeed);
            setData(tag, target);
        }
        updateOptions(maxSpeed);
    }

    @Override
    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target) {
        MiningSpeedEffect eff = (MiningSpeedEffect) effect.createInstance(getSequenceLevel(), 0, -1, true);
        CompoundTag tag = getData();
        if(!tag.contains("speed")){
            tag.putFloat("speed", levelToMaxSpeed.apply(getSequenceLevel()));
            setData(tag, target);
        }
        eff.miningSpeed = getData().getFloat("speed");
        return eff;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(getSequenceLevel() >= 8) {
            return false;
        }
        String option = AbilityOptionsUtil.validadeArguments(args, this, speedOptions, target.level().isClientSide(), false);
        if(option.isEmpty() || target.level().isClientSide()) return false;
        float newSpeed = Float.parseFloat(option);
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.WHEEL_MINING.getEffectId(), getSequenceLevel())){
            BeyonderEffect eff = cap.getEffectsManager().getEffect(BeyonderEffects.WHEEL_MINING.getEffectId(), getSequenceLevel());
            if(eff instanceof MiningSpeedEffect miningSpeedEffect){
                miningSpeedEffect.miningSpeed = newSpeed;
            }
        }

        CompoundTag data = getData();
        data.putFloat("speed", newSpeed);
        setData(data, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.mining_speed_set", Math.round(newSpeed*100)/100f));
        return true;
    }

    private void updateOptions(float maxSpeed){
        List<String> speeds = new ArrayList<>();
        float hold = 1;
        speeds.add(String.valueOf(hold));
        for(int i = 0; i < 4; i++){
            hold = AbilityFunctionHelper.incrementThroughRange(1, maxSpeed, 4, hold);
            speeds.add(String.valueOf(Math.round(100*hold)/100f));
        }
        int i = 0;
        this.speedOptions = new AbilityOptions()
                .addEmptyOption(speeds.get(i), Component.literal(speeds.get(i++)))
                .addEmptyOption(speeds.get(i), Component.literal(speeds.get(i++)))
                .addEmptyOption(speeds.get(i), Component.literal(speeds.get(i++)))
                .addEmptyOption(speeds.get(i), Component.literal(speeds.get(i++)))
                .addEmptyOption(speeds.get(i), Component.literal(speeds.get(i++)));
    }
}
