package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class WeakeningEffect extends BeyonderEffect {
    private int weakeningsLeft = 0;
    private boolean weakenAbilities = false;
    private Set<AbilityKey> affectedInstances = new HashSet<>();

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(!fromLoading) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            abl.upgradeToLevelSilently(abl.getSequenceLevel() + 1, cap, target);
        }
    }

    public int canWeaken(AbilityKey ablKey){
        if(weakeningsLeft < 1) return -1;
        if(affectedInstances.contains(ablKey)) return -1;
        affectedInstances.add(ablKey);
        weakeningsLeft--;
        return sequenceLevel;
    }

    public void setWeakeningsLeft(int newMax){this.weakeningsLeft = newMax;weakenAbilities =true;}

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(weakenAbilities) return;
        cap.getEffectsManager().statsHolder.addDamage(-1 - Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addArmor(-2 - 2*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addHealth(-2 - 4*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addToughness(-4 - 2*Math.max(0, 7 - sequenceLevel));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!weakenAbilities) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            abl.upgradeToLevelSilently(abl.getSequenceLevel() - 1, cap, target);
        }
    }
}
