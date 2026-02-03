package net.dinomine.potioneer.beyonder.effects.wheeloffortune;

import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.UUID;

public class BeyonderPatienceEffect extends BeyonderEffect {
    private int quantity = 0;
    private int luck_limit;
    private static final float a = 2;
    private int time;
    private static final UUID uuid = UUID.fromString("1323c552-fe64-45b6-b6a2-8cc0fbf152ac");
    private boolean acquired = false;

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if (target.level().isClientSide) return;
        cap.getLuckManager().getRange().setSuppress(true);
        cap.getLuckManager().chanceLuckEventChange(uuid, 9 - sequenceLevel);
        if (acquired) return;
        setLuckQuantity(cap.getLuckManager().getLuck());
        if (sequenceLevel > 6){
            cap.getLuckManager().setLuck(Math.min(cap.getLuckManager().getLuck(), 0));
            target.level().playSound(null, target.getOnPos(), ModSounds.UNLUCK.get(), SoundSource.PLAYERS, 1, (float) target.getRandom().triangle(1, 0.2));
        }
        target.level().playSound(null, target.getOnPos(), SoundEvents.BOTTLE_FILL_DRAGONBREATH, SoundSource.PLAYERS, 1, (float)target.getRandom().triangle(1, 0.2));
        acquired = true;
    }

    @Override
    public BeyonderEffect withParams(int sequence, int time, boolean active, int cost) {
        super.withParams(sequence, time, active, cost);
        //limit for the maximum luck you can get to by using the effect. changes with sequence
        //This is calculated such that it reaches this maximum after, at most, 20 minutes
        this.luck_limit = 1000 - 110 * sequenceLevel;
        //change this with sequence too. time in seconds to reach the maximum
        this.time = (int) ((300 + sequenceLevel * 130) * PotioneerCommonConfig.PATIENCE_TIME_LIMIT.get());
        return this;
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return;
        if(target.tickCount%20 == target.getId()%20){
            if(quantity < time) quantity += 1;
        }
    }

    public int getProjectedLuck(LivingEntityBeyonderCapability cap) {
        int currentLuck = cap.getLuckManager().getLuck();
        return Math.max(quantityToLuck(quantity), currentLuck) - currentLuck;
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return;
        cap.getLuckManager().getRange().setSuppress(false);
        int currentLuck = cap.getLuckManager().getLuck();
        int amm = Math.max(quantityToLuck(quantity), currentLuck) - currentLuck;
        cap.getLuckManager().grantLuck(amm);
        cap.getCharacteristicManager().progressActing(0.2f*Math.pow(amm/(float)luck_limit, 2.6f), 7);
        cap.getLuckManager().removeLuckEventModifier(uuid);
        target.level().playSound(null, target.getOnPos(), SoundEvents.BOTTLE_EMPTY, SoundSource.PLAYERS, 1, (float)target.getRandom().triangle(1, 0.2));
        target.sendSystemMessage(Component.translatableWithFallback("ability.potioneer.patience_release", "You have been granted %s luck", amm));
    }

    public void setLuckQuantity(int luck){
        this.quantity = luckToQuantity(luck);
    }

    //These functions can be visualized at https://www.desmos.com/calculator/3uoitj78qi
    private int luckToQuantity(int luck){
        try{
            return (int) ((time/a)*(Math.pow(10, Math.log10(a+1)*luck/luck_limit) - 1));
        } catch(ArithmeticException e){
            String message = "[Potioneer] Tried to convert luck to quantity in the beyonder patience effect, but the result was mathematically invalid. If you see this often, or if the effect is NOT sequence level 9, please mention it to the developer.\nEffect in question: " + this;
            Potioneer.LOGGER.error(message);
            System.err.println(message);
            return -500;
        }
    }

    private int quantityToLuck(float quantity){
        return (int)(luck_limit * Math.log10(a*quantity/time + 1) / Math.log10(a+1));
    }

    @Override
    public String toString() {
        CompoundTag tag = new CompoundTag();
        toNbt(tag);
        return tag.toString();
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("quantity", this.quantity);
        nbt.putBoolean("acquired", this.acquired);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        this.quantity = nbt.getInt("quantity");
        this.acquired = nbt.getBoolean("acquired");
    }
}
