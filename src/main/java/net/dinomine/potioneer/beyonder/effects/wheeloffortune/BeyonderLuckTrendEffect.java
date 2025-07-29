package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

public class BeyonderLuckTrendEffect extends BeyonderEffect {

    public BeyonderLuckTrendEffect(){
        this(0, 0f, 0, false, BeyonderEffects.EFFECT.WHEEL_MINING);
    }

    private int tick = 0;
    public BeyonderLuckTrendEffect(int level, float cost, int time, boolean active, BeyonderEffects.EFFECT id){
        super(level, cost, time, active, id);
        this.name = "Wheel of Fortune Luck Trend";
    }


    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(tick++ > 120){
            System.out.println("Ticking up");
            cap.getLuckManager().grantLuck(1);
            tick = 0;
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("timeTick", tick);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        tick = nbt.getInt("timeTick");
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }
}
