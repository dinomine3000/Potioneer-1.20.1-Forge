package net.dinomine.potioneer.beyonder.pathways;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potions;

public class TyrantPathway extends Beyonder{

    public TyrantPathway(int sequence){
        super(sequence);
    }

    @Override
    public int getId() {
        return this.sequence;
    }

    @Override
    public String getName(int seq){
        return switch (seq) {
            case 9 -> "Swimmer";
            case 8 -> "Water-Blessed";
            default -> "";
        };
    }

    @Override
    public void onTick(Player player) {
        super.onTick(player);
        switch(this.sequence){
            case 8:
                onTick8();
            case 9:
                onTick9(player);

        }

    }

    private void onTick8() {
        //if touching water, reset buff cooldown to 200 ticks
        //add more buffs and let play fly.
    }

    private void onTick9(Player player){
        //add effects
        //check if underwater, if so add more effects
        if(player.isInWater()){
            if(player.getFoodData().needsFood() && Math.random() < 0.01){
                player.getFoodData().eat(1, 0);
                player.sendSystemMessage(Component.literal("Replenished a bit of hunger"));
            }
            if(!player.hasEffect(MobEffects.WATER_BREATHING)){
                player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, -1, 0, false, false));
            }
        }
    }
}
