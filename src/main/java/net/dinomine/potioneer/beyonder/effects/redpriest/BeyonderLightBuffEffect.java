package net.dinomine.potioneer.beyonder.effects.redpriest;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLightBuffEffect extends BeyonderEffect {
    public BeyonderLightBuffEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Red Priest Light Buff";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.addEffect(new MobEffectInstance(ModEffects.LIGHT_BUFF.get(), (this.maxLife-lifetime)/2, 1, true, true));
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        if(target.hasEffect(ModEffects.LIGHT_BUFF.get())){
            target.removeEffect(ModEffects.LIGHT_BUFF.get());
        }
    }
}
