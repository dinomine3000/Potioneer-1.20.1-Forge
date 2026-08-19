package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.Optional;

public class LightBuffAbility extends Ability {

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "light_buff";
    }

    /*@Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() >= cost();

        ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, target.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + 0.5f);
        hits.sort((a, b) -> (int) (a.position().distanceTo(target.position()) - b.position().distanceTo(target.position())));
        for(LivingEntity livingEntity: hits){
            Optional<BeyonderCapability> otherCap = livingEntity.getCapability(CapProvider.BEYONDER_STATS).resolve();
            if(otherCap.isPresent()){
                otherCap.get().getEffectsManager().addOrRefreshEffect(BeyonderEffects.byId(BeyonderEffects.RED_LIGHT_BUFF.getEffectId(), getSequenceLevel(), 0, livingEntity != target ? 2*20*60*5 : 2*20*60, true)
                        , otherCap.get(), livingEntity);
                cap.requestActiveSpiritualityCost(cost());
                System.out.println("Applied strength buff to " + livingEntity.getName());
                return true;
            }
        }
        return false;
    }*/
}
