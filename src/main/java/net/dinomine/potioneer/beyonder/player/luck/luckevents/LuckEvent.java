package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.event.LuckEventCastEvent;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;

public abstract class LuckEvent {

    public void saveNbt(CompoundTag luck) {
        luck.putLong("timer", timer);
        luck.putString("mag", mag.name());
    }

    public LuckEvent loadNbt(CompoundTag tag) {
        this.mag = Magnitude.valueOf(tag.getString("mag"));
        this.timer = tag.getLong("timer");
        return this;
    }

    public enum Magnitude{
        VERY_UNLUCKY,
        MILDLY_UNLUCKY,
        UNLUCKY,
        LUCKY,
        MILDLY_LUCKY,
        VERY_LUCKY
    }
    long timer;
    Magnitude mag;
    String id;

    public LuckEvent(){
    }

    public String getId(){
        return id;
    }

    public long getTimer() {
        return timer;
    }

    protected LuckEvent withParams(long countdown, String id, Magnitude mag) {
        this.timer = countdown;
        this.id = id;
        this.mag = mag;
        return this;
    }

    public boolean timeUp(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(this.timer-- < 1){
            boolean cancelledCheck = MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.TriggeredPre(target, cap.getLuckManager().getLuck(), this));
            if(cancelledCheck) return true;

            MinecraftForge.EVENT_BUS.post(new LuckEventCastEvent.TriggeredPost(target, cap.getLuckManager().getLuck(), this));

            target.sendSystemMessage(Component.translatable("potioneer.luck.trigger_" + this.mag.toString().toLowerCase()));
            target.level().playSound(null, target.getOnPos(), isPositive(mag) ? ModSounds.LUCK.get(): ModSounds.UNLUCK.get(), SoundSource.NEUTRAL, 1, 1);
            triggerEvent(cap, cap.getLuckManager(), target);
            return true;
        }
        return false;
    }

    public static boolean isPositive(Magnitude mag){
        return mag == Magnitude.LUCKY || mag == Magnitude.MILDLY_LUCKY || mag == Magnitude.VERY_LUCKY;
    }

    public void forceCast() {
        this.timer = 0;
    }

    public Component getForecast(){
        return Component.translatable("luckevent.potioneer." + getId(), timer/20);
    }
    public abstract void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target);
}
