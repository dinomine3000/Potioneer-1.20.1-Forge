package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.AuraSourceEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Function;

public class AuraAbility extends PassiveAbility {
    public AuraAbility(int sequenceLevel) {
        super(sequenceLevel, BeyonderEffects.TYRANT_AURA_SOURCE, ign -> "");

        canFlip().withThreshold(0.1f).withCost(10);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return sequenceLevel < 7 ? (sequenceLevel < 6 ? "aoj_aura_3" : "aoj_aura_2") : "aoj_aura";
    }

    @Override
    protected LinkedHashSet<String> getAllDescId(int sequenceLevel) {
        if(sequenceLevel > 5) return super.getAllDescId(sequenceLevel);
        LinkedHashSet<String> res = new LinkedHashSet<>(Set.of("aura_execution", "aura_purification", "aura_stand_back"));
        res.addAll(super.getAllDescId(sequenceLevel));
        return res;
    }

    public void storeCooldown(int cooldown, LivingEntity target){
        CompoundTag tag = getData();
        tag.putInt("aura_cooldown", cooldown);
        setData(tag, target);
    }

    @Override
    protected BeyonderEffect createEffectInstance(BeyonderCapability cap, LivingEntity target) {
        AuraSourceEffect eff = (AuraSourceEffect) effect.createInstance(sequenceLevel, cost(), -1, true);
        eff.setCooldown(getData().getInt("aura_cooldown"), getInstanceId());
        return eff;
    }
}
