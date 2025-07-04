package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.Optional;

public class LightBuffAbility extends Ability {

    public LightBuffAbility(int sequence){
        this.info = new AbilityInfo(83, 224, "Light Buff", 30 + sequence, 5, this.getCooldown(), "light_buff");
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return cap.getSpirituality() >= info.cost();

        ArrayList<Entity> hits = AbilityFunctionHelper.getLivingEntitiesLooking(target, target.getAttributeValue(ForgeMod.ENTITY_REACH.get()) + 0.5f);
        hits.sort((a, b) -> (int) (a.position().distanceTo(target.position()) - b.position().distanceTo(target.position())));
        for(Entity ent: hits){
            if(ent instanceof LivingEntity livingEntity){
                Optional<EntityBeyonderManager> otherCap = livingEntity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(otherCap.isPresent()){
                    otherCap.get().getEffectsManager().addEffect(BeyonderEffects.byId(BeyonderEffects.EFFECT.RED_LIGHT_BUFF, getSequence(), 0, 2*20*60*5, true)
                            , otherCap.get(), livingEntity);
                    cap.requestActiveSpiritualityCost(info.cost());
                    System.out.println("Applied strength buff to " + livingEntity.getName());
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
