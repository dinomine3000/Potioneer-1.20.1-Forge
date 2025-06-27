package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderWeaponProficiencyEffect extends BeyonderEffect {

    public BeyonderWeaponProficiencyEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY);
    }

    public BeyonderWeaponProficiencyEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Red Priest Proficiency";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }
}
