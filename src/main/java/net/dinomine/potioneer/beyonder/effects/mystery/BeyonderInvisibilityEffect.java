package net.dinomine.potioneer.beyonder.effects.mystery;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class BeyonderInvisibilityEffect extends BeyonderEffect {
    public BeyonderInvisibilityEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.MYSTERY_INVIS);
    }

    public BeyonderInvisibilityEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Mystery Invis";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(!target.hasEffect(MobEffects.INVISIBILITY)){
            target.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, (this.maxLife-lifetime)/2, 1, false, false));
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        if(target.hasEffect(MobEffects.INVISIBILITY)){
            target.removeEffect(MobEffects.INVISIBILITY);
        }
    }

}
