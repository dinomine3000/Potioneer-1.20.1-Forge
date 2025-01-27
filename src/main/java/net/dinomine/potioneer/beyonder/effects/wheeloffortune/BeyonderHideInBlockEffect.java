package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.server.commands.GameModeCommand;
import net.minecraft.server.commands.SpectateCommand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;

public class BeyonderHideInBlockEffect extends BeyonderEffect {


    public BeyonderHideInBlockEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Wheel of Fortune Hide";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        target.horizontalCollision = false;
        target.verticalCollision = false;
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        target.horizontalCollision = true;
        target.verticalCollision = true;
    }
}
