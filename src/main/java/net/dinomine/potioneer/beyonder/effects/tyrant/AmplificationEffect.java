package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class AmplificationEffect extends BeyonderEffect {
    private int amplificationsLeft = 0;
    private boolean amplifyAbilities = false;
    private Set<AbilityKey> affectedInstances = new HashSet<>();

    @Override
    public boolean canBeCleansed() {
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(!fromLoading) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            abl.upgradeToLevelSilently(abl.getSequenceLevel() - 1, cap, target);
        }
    }

    public int canAmplify(AbilityKey ablKey){
        if(amplificationsLeft < 1) return -1;
        if(affectedInstances.contains(ablKey)) return -1;
        affectedInstances.add(ablKey);
        amplificationsLeft--;
        return sequenceLevel;
    }

    public void setAmplificationsLeft(int newMax){this.amplificationsLeft = newMax;amplifyAbilities =true;}

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(amplifyAbilities) return;
        cap.getEffectsManager().statsHolder.addDamage(1 + Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addHealth(2 + 4*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addArmor(4 + 2*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addToughness(4 + 2*Math.max(0, 7 - sequenceLevel));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!amplifyAbilities) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            if(abl == null) continue;
            abl.upgradeToLevelSilently(abl.getSequenceLevel() + 1, cap, target);
        }
    }
}
