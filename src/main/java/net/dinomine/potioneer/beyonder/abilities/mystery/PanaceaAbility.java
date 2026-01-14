package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.List;

public class PanaceaAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "panacea";
    }

    public PanaceaAbility(int sequence){
//        this.info = new AbilityInfo(57, 272, "Cure All", 20 + sequence, 60 + 5*(9-sequence), 20*10, "panacea");
        super(sequence);
        setCost(level -> 60 + 5*(9-level));
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost() && !target.getActiveEffects().isEmpty()){
            boolean flag = false;

            List<MobEffectInstance> effectsToRemove = new ArrayList<>(target.getActiveEffects());

            for (MobEffectInstance effect : effectsToRemove) {
                if(effect.getEffect().getCategory() == MobEffectCategory.HARMFUL){
                    target.removeEffect(effect.getEffect());
                    flag = true;
                }
            }
            return flag;
        }
        return false;
    }
}
