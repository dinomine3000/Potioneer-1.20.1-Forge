package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class PlayerEffectsManager {
    private ArrayList<BeyonderEffect> passives = new ArrayList<BeyonderEffect>();
    public BeyonderStats statsHolder;

    public PlayerEffectsManager(){
        statsHolder = new BeyonderStats();
    }

    public void clearEffects(){
        this.passives.clear();
    }

    public boolean hasEffectOrBetter(BeyonderEffect effect){
        return passives.stream().anyMatch(effect::isBetter);
    }

    public boolean addEffect(BeyonderEffect effect){
        passives.add(effect);
        return true;
    }

    public int indexOf(BeyonderEffects.EFFECT effect){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect)) return i;
        }
        return -1;
    }

    public int indexOf(BeyonderEffects.EFFECT effect, int seq){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect, seq)) return i;
        }
        return -1;
    }

    public boolean hasEffect(BeyonderEffects.EFFECT effect, int seq){
        return passives.stream().anyMatch(eff -> eff.is(effect, seq));
    }

    public boolean hasEffect(BeyonderEffects.EFFECT effect){
        return passives.stream().anyMatch(eff -> eff.is(effect));
    }

    public BeyonderEffect getEffect(BeyonderEffects.EFFECT effect, int seq){
        return passives.get(indexOf(effect, seq));
    }

    public boolean removeEffect(BeyonderEffects.EFFECT effect, int seq, EntityBeyonderManager cap, LivingEntity target){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect, seq)) {
                passives.get(i).stopEffects(cap, target);
                passives.remove(i);
                return true;
            };
        }
        return false;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        statsHolder.resetStats();
        passives.forEach(effect -> {
            effect.effectTick(cap, target);
        });
        cap.getBeyonderStats().setStats(statsHolder);
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag effectsNbt = new CompoundTag();
        effectsNbt.putInt("size", passives.size());
        for(int i = 0; i < passives.size(); i++){
            CompoundTag iterator = new CompoundTag();
            passives.get(i).toNbt(iterator);
            effectsNbt.put(String.valueOf(i), iterator);
        }
        nbt.put("effectData", effectsNbt);
    }

    public void loadNBTData(CompoundTag nbt){
        CompoundTag effectsTag = nbt.getCompound("effectData");
        int size = effectsTag.getInt("size");
        for(int i = 0; i < size; i++){
            CompoundTag iterator = effectsTag.getCompound(String.valueOf(i));
            BeyonderEffect effect = BeyonderEffects.byId(
                    BeyonderEffects.EFFECT.valueOf(iterator.getString("ID")),
                    iterator.getInt("level"),
                    iterator.getInt("cost"),
                    iterator.getInt("maxLife"),
                    iterator.getBoolean("active"));
            effect.setDuration(iterator.getInt("lifetime"));
            addEffect(effect);
        }
    }
}
