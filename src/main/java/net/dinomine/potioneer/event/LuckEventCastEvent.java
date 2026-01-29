package net.dinomine.potioneer.event;

import net.dinomine.potioneer.beyonder.player.luck.luckevents.LuckEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

public abstract class LuckEventCastEvent extends LivingEvent {
    private final int luck;
    private final LuckEvent luckEvent;
    public LuckEventCastEvent(LivingEntity entity, int luck, LuckEvent luckEvent) {
        super(entity);
        this.luck = luck;
        this.luckEvent = luckEvent;
    }

    public long getCountdown(){
        return luckEvent.getTimer();
    }

    public int getLuck(){
        return luck;
    }

    public LuckEvent getLuckEvent(){
        return this.luckEvent;
    }

    @Cancelable
    public static class Pre extends LuckEventCastEvent{
        public Pre(LivingEntity entity, int luck, LuckEvent luckEvent) {
            super(entity, luck, luckEvent);
        }
    }

    public static class Post extends LuckEventCastEvent{
        public Post(LivingEntity entity, int luck, LuckEvent luckEvent) {
            super(entity, luck, luckEvent);
        }
    }

    @Cancelable
    public static class TriggeredPre extends LuckEventCastEvent{
        public TriggeredPre(LivingEntity entity, int luck, LuckEvent luckEvent) {
            super(entity, luck, luckEvent);
        }
    }

    public static class TriggeredPost extends LuckEventCastEvent{
        public TriggeredPost(LivingEntity entity, int luck, LuckEvent luckEvent) {
            super(entity, luck, luckEvent);
        }
    }

}
