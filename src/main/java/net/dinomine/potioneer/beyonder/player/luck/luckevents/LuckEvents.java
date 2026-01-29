package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class LuckEvents {
    public static final HashMap<String, LuckEventConstructor> LUCK_EVENTS = new HashMap<>();

    public static final LuckEventConstructor ASTEROID_EVENT = register("asteroid", new LuckEventConstructor(AsteroidLuckEvent::new, LuckEvent.Magnitude.VERY_UNLUCKY));

    public static LuckEventConstructor getRandomEventFromLuck(int luck, RandomSource randomSource){
        if(luck < -PotioneerCommonConfig.LUCK_LV3_THRESHOLD.get()) return getRandomEventOfMagnitude(LuckEvent.Magnitude.VERY_UNLUCKY, randomSource);
        if(luck > PotioneerCommonConfig.LUCK_LV3_THRESHOLD.get()) return getRandomEventOfMagnitude(LuckEvent.Magnitude.VERY_LUCKY, randomSource);
        if(luck < -PotioneerCommonConfig.LUCK_LV2_THRESHOLD.get()) return getRandomEventOfMagnitude(LuckEvent.Magnitude.MILDLY_UNLUCKY, randomSource);
        if(luck > PotioneerCommonConfig.LUCK_LV2_THRESHOLD.get()) return getRandomEventOfMagnitude(LuckEvent.Magnitude.MILDLY_LUCKY, randomSource);
        if(luck < 0) return getRandomEventOfMagnitude(LuckEvent.Magnitude.UNLUCKY, randomSource);
        return getRandomEventOfMagnitude(LuckEvent.Magnitude.LUCKY, randomSource);
    }

    public static LuckEventConstructor getRandomEventOfMagnitude(LuckEvent.Magnitude magnitude, RandomSource random){
        List<LuckEventConstructor> matches = LUCK_EVENTS.values().stream().filter(constructor -> constructor.isMagnitude(magnitude)).toList();
        if(matches.isEmpty()) return ASTEROID_EVENT;
        return matches.get(random.nextInt(0, matches.size()));
    }

    public static LuckEventConstructor register(String eventId, LuckEventConstructor constructor){
        LUCK_EVENTS.put(eventId, constructor.withEventId(eventId));
        return constructor;
    }
    
    public static LuckEventConstructor getEventById(String id){
        return LUCK_EVENTS.get(id);
    }
    
    public static class LuckEventConstructor{
        private final Supplier<LuckEvent> factory;
        private String eventId;
        private final LuckEvent.Magnitude mag;

        public LuckEventConstructor(Supplier<LuckEvent> factory, LuckEvent.Magnitude magnitude) {
            this.factory = factory;
            this.mag = magnitude;
        }

        public boolean isMagnitude(LuckEvent.Magnitude magnitude){
            return this.mag.equals(magnitude);
        }

        public String getEventId(){
            return this.eventId;
        }

        public LuckEventConstructor withEventId(String eventId){
            this.eventId = eventId;
            return this;
        }

        public LuckEvent createInstance(long countdown) {
            return factory.get().withParams(countdown, eventId, mag);
        }
    }
}
