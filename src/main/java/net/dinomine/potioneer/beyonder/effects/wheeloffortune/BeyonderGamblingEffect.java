package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderGamblingEffect extends BeyonderEffect {
    private int quantity = 0;
    private int tick = 0;
    private int luck_limit;
    private static final float a = 2;
    private int time;

    public BeyonderGamblingEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);

        //limit for the maximum luck you can get to by using the effect. changes with sequence
        //This is calculated such that it reaches this maximum after, at most, 20 minutes
        this.luck_limit = 775 - 75 * sequenceLevel;
        //change this with sequence too. time in seconds to reach the maximum
        this.time = 300 + sequenceLevel * 130;
        this.name = "Wheel of Fortune Gambling";
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().getRange().setSuppress(true);
        cap.getLuckManager().chanceLuckEventChange(9-sequenceLevel);
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(tick++ > 40){
            tick = 0;
            if(quantity < time) quantity += 1;
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.getLuckManager().getRange().setSuppress(false);
        int currentLuck = cap.getLuckManager().getLuck();
        int amm = Math.max(quantityToLuck(quantity), currentLuck) - currentLuck;
        cap.getLuckManager().grantLuck(amm);
        cap.getActingManager().progressActing(0.2f*Math.pow(amm/(float)luck_limit, 2.6f), 7);
        cap.getLuckManager().chanceLuckEventChange(-9+sequenceLevel);
        target.sendSystemMessage(Component.literal("You have been granted " + amm + " luck"));
    }

    public void setLuckQuantity(int luck){
        this.quantity = luckToQuantity(luck);
    }

    //These functions can be visualized at https://www.desmos.com/calculator/3uoitj78qi
    private int luckToQuantity(int luck){
        return (int) ((time/a)*(Math.pow(10, Math.log10(a+1)*luck/ luck_limit) - 1));
    }

    private int quantityToLuck(float quantity){
        return (int)(luck_limit * Math.log10(a*quantity/time + 1) / Math.log10(a+1));
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("quantity", this.quantity);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.quantity = nbt.getInt("quantity");
    }
}
