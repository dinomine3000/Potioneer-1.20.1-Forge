package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.misc.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderZeroDamageEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

public class ZeroDamageAbility extends PassiveAbility {

    public ZeroDamageAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.WHEEL_ZERO_DAMAGE, (level) -> "zero_damage_" + (level > 7 ? "1" : (level > 6 ? "2" : "3")));
        enabledOnAcquire();
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("blocks", true);
        setDataSilent(tag);
    }

    @Override
    protected BeyonderEffect createEffectInstance(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel > 6) return super.createEffectInstance(cap, target);
        BeyonderZeroDamageEffect eff = (BeyonderZeroDamageEffect) BeyonderEffects.WHEEL_ZERO_DAMAGE.createInstance(getSequenceLevel(), cost(), -1, true);
        CompoundTag tag = getData();
        if(!tag.contains("blocks")){
            tag.putBoolean("blocks", true);
            setData(tag, target);
        }
        eff.setBlock(tag.getBoolean("blocks"));
        return eff;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(sequenceLevel > 6){
            if(target.level().isClientSide())
                target.sendSystemMessage(Component.translatableWithFallback("message.potioneer.outdated_secondary", "It doesn't do anything... yet"));
            return false;
        }
        if(target.level().isClientSide()) return true;
        CompoundTag tag = getData();
        boolean newState = !tag.getBoolean("blocks");
        tag.putBoolean("blocks", newState);
        target.sendSystemMessage(Component.translatable("ability.potioneer.zero_damage_block_flip", newState));
        setData(tag, target);
        return true;
    }
}
