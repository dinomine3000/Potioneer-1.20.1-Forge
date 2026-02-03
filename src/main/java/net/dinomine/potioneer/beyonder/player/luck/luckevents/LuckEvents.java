package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.List;
import java.util.function.Supplier;

public class LuckEvents {
    public static final HashMap<String, LuckEventConstructor> LUCK_EVENTS = new HashMap<>();

    public static final LuckEventConstructor DIGEST_POTION = register("digest", new LuckEventConstructor(DigestPotionLuckEvent::new, LuckEvent.Magnitude.VERY_LUCKY));
    public static final LuckEventConstructor REPAIR_ALL_EVENT = register("repair_all", new LuckEventConstructor(RepairAllLuckEvent::new, LuckEvent.Magnitude.VERY_LUCKY));
    public static final LuckEventConstructor REPAIR_ITEM_EVENT = register("repair_main", new LuckEventConstructor(RepairMainItemLuckEvent::new, LuckEvent.Magnitude.MILDLY_LUCKY));
    public static final LuckEventConstructor GAMBLING_SUCCEED = register("gambling_succeed", new LuckEventConstructor(GamblingSuccessLuckEvent::new, LuckEvent.Magnitude.MILDLY_LUCKY));
    public static final LuckEventConstructor MINING_EVENT = register("mining", new LuckEventConstructor(MiningLuckEvent::new, LuckEvent.Magnitude.MILDLY_LUCKY));
    public static final LuckEventConstructor REFRESH_EVENT = register("refresh", new LuckEventConstructor(RefreshLuckEvent::new, LuckEvent.Magnitude.MILDLY_LUCKY));
    public static final LuckEventConstructor DODGE_EVENT = register("luck_dodge", new LuckEventConstructor(DodgeLuckEvent::new, LuckEvent.Magnitude.MILDLY_LUCKY));
    public static final LuckEventConstructor ITEM_GEN_EVENT = register("item_gen", new LuckEventConstructor(ItemGenLuckEvent::new, LuckEvent.Magnitude.LUCKY));
    public static final LuckEventConstructor DROP_ITEM_EVENT = register("drop", new LuckEventConstructor(DropItemLuckEvent::new, LuckEvent.Magnitude.UNLUCKY));
    public static final LuckEventConstructor VANISHING_EVENT = register("vanishing", new LuckEventConstructor(VanishingLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor GAMBLING_FAIL = register("gambling_fail", new LuckEventConstructor(GamblingFailLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor DAMAGE_ARMOR_EVENT = register("hurt_armor", new LuckEventConstructor(DamageArmorLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor LIGHTNING_STRIKE_EVENT = register("lightning", new LuckEventConstructor(LightningStrikeLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor SHRINK_ITEM_EVENT = register("shrink_item", new LuckEventConstructor(ShrinkItemLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor SUMMON_MOB_EVENT = register("summon_mobs", new LuckEventConstructor(SummonMobLuckEvent::new, LuckEvent.Magnitude.MILDLY_UNLUCKY));
    public static final LuckEventConstructor ASTEROID_EVENT = register("asteroid", new LuckEventConstructor(AsteroidLuckEvent::new, LuckEvent.Magnitude.VERY_UNLUCKY));
    public static final LuckEventConstructor BINDING_EVENT = register("binding", new LuckEventConstructor(BindingLuckEvent::new, LuckEvent.Magnitude.VERY_UNLUCKY));
    public static final LuckEventConstructor DAMAGE_ITEMS_EVENT = register("hurt_tools", new LuckEventConstructor(DamageToolsLuckEvent::new, LuckEvent.Magnitude.VERY_UNLUCKY));
    public static final LuckEventConstructor PHANTOM_EVENT = register("phantom", new LuckEventConstructor(PhantomLuckEvent::new, LuckEvent.Magnitude.VERY_UNLUCKY));

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
        if(matches.isEmpty()) return ITEM_GEN_EVENT;
        return matches.get(random.nextInt(0, matches.size()));
    }

    public static LuckEventConstructor register(String eventId, LuckEventConstructor constructor){
        LUCK_EVENTS.put(eventId, constructor.withEventId(eventId));
        return constructor;
    }

    public static LuckEvent.Magnitude getMagnitudeOfEvent(LuckEvent event){
        return LUCK_EVENTS.get(event.id).mag;
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
