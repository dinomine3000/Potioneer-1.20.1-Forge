package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderTempLuckEffect extends BeyonderEffect {


    public BeyonderTempLuckEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        this.sequenceLevel = level;
        this.cost = cost;
        this.maxLife = time;
        this.ID = id;
        this.lifetime = 0;
        this.active = active;
        this.name = "Wheel of Fortune Temp Luck";
    }


    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
        cap.getLuckManager().grantLuck(51);
        target.sendSystemMessage(Component.literal("The cogs of fate favor you for now..."));
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
        cap.getLuckManager().consumeLuck(60);
        target.sendSystemMessage(Component.literal("All is not without its price. Your luck has been taken back."));
        if (cap.getLuckManager().getLuck() < 0){
            target.sendSystemMessage(Component.literal("Unlucky..."));
            cap.getLuckManager().instantlyCastEvent();
        }
    }
}
