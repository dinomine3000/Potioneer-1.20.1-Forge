package net.dinomine.potioneer.beyonder.effects.paragon;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderCraftingBonusEffect extends BeyonderEffect {

    public BeyonderCraftingBonusEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS);
    }

    public BeyonderCraftingBonusEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Paragon Crafting";
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
