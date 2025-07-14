package net.dinomine.potioneer.beyonder.effects.misc;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Random;
import java.util.UUID;

import static net.dinomine.potioneer.beyonder.abilities.mystery.PlagueAbility.PLAGUE_RANGE;

public class BeyonderHungerRegenEffect extends BeyonderEffect {
    public BeyonderHungerRegenEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Plagued";
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    protected void doTick(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getLuckManager().passesLuckCheck(0.1f, 0, 0, target.getRandom())){
            Player player = ((Player) target);
            player.getFoodData().eat(player.getRandom().nextInt(4), player.getRandom().nextInt(2));
        }
    }

    @Override
    public void stopEffects(EntityBeyonderManager cap, LivingEntity target) {
    }

}
