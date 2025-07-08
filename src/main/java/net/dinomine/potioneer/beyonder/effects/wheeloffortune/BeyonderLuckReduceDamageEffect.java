package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLuckReduceDamageEffect extends BeyonderEffect {
    public static float reduceChance = 0.1f;
    public static int luckCost = 10;
    public static int luckGain = 0;
    public static int sequenceForNegation = 5;
    public static float damageReduction = 0.5f;

    public BeyonderLuckReduceDamageEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.WHEEL_TEMP_LUCK);
    }

    public BeyonderLuckReduceDamageEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Wheel of Fortune Damage Reduction";
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
