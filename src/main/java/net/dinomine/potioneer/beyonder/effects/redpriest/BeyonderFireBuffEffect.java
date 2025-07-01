package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderFireBuffEffect extends BeyonderEffect {

    public BeyonderFireBuffEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Red Priest Fire Buff";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.FIRE_RESISTANCE))
            target.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, -1, 1));
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.FIRE_RESISTANCE))
            target.removeEffect(MobEffects.FIRE_RESISTANCE);
    }
}
