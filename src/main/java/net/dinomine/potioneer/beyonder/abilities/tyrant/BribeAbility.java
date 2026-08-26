package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptionsUtil;
import net.dinomine.potioneer.beyonder.abilities.PassiveAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.BribeSourceEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

import java.util.LinkedHashSet;

public class BribeAbility extends PassiveAbility {
    private final AbilityOptions options;
    public BribeAbility() {
        super(BeyonderEffects.TYRANT_BRIBE, ign -> "bribe");
        options = new AbilityOptions()
                .addEmptyOption("truce", Component.literal("Truce"))
                .addEmptyOption("disorder", Component.literal("Disorder"))
                .addEmptyOption("weakening", Component.literal("Weakening"));
    }

    @Override
    public void init() {
        canFlip();
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    @Override
    protected LinkedHashSet<String> getAllDescId(int sequenceLevel) {
        LinkedHashSet<String> res = new LinkedHashSet<>();
        res.add("bribe_truce");
        res.add("bribe_weakening");
        res.add("bribe_disorder");
        return res;
    }

    @Override
    protected BeyonderEffect createEffectInstance() {
        BribeSourceEffect eff = (BribeSourceEffect) super.createEffectInstance();
        eff.setup(getData().getString("type"));
        return eff;
    }


    @Override
    protected boolean secondary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        String option = AbilityOptionsUtil.validadeArguments(args, this, options, target.level().isClientSide(), false);
        if(option.isEmpty() || target.level().isClientSide()) return false;

        if(cap.getEffectsManager().hasEffect(effect.getEffectId(), getSequenceLevel())){
            BeyonderEffect eff = cap.getEffectsManager().getEffect(effect.getEffectId(), getSequenceLevel());
            if(eff instanceof BribeSourceEffect bribeEffect){
                bribeEffect.setup(option);
            }
        }
        CompoundTag data = getData();
        data.putString("type", option);
        setData(data, target);
        target.sendSystemMessage(Component.translatable("ability.potioneer.bribe_set_" + option));
        return true;
    }
}
