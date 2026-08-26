package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.ZeroDamageEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class ZeroDamageAbility extends PassiveAbility {

    public ZeroDamageAbility() {
        super(BeyonderEffects.WHEEL_ZERO_DAMAGE, (level) -> "zero_damage_" + (level > 7 ? "1" : (level > 6 ? "2" : "3")));
    }

    @Override
    public void init() {
        enabledOnAcquire();
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("blocks", true);
        setDataSilent(tag);
    }

    @Override
    protected BeyonderEffect createEffectInstance() {
        if(getSequenceLevel() > 6) return super.createEffectInstance();
        ZeroDamageEffect eff = (ZeroDamageEffect) BeyonderEffects.WHEEL_ZERO_DAMAGE.createInstance(getSequenceLevel(), spiritualityCost, -1, true);
        CompoundTag tag = getData();
        eff.setBlock(tag.getBoolean("blocks"));
        return eff;
    }

    @Override
    protected boolean hasSecondary(int level) {
        return getSequenceLevel() < 6;
    }

    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        CompoundTag tag = getData();
        boolean newState = !tag.getBoolean("blocks");
        tag.putBoolean("blocks", newState);
        target.sendSystemMessage(Component.translatable("ability.potioneer.zero_damage_block_flip", newState));
        setData(tag, target);
        return true;
    }
}
