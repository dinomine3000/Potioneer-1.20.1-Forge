package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.mob_effects.ModEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class BeyonderAoJInfluenceEffect extends BeyonderEffect {
    private final HashMap<UUID, Integer> enforcerMap = new HashMap<>();

    public static BeyonderAoJInfluenceEffect getInstance(UUID enforcerId){
        BeyonderAoJInfluenceEffect eff = (BeyonderAoJInfluenceEffect) BeyonderEffects.TYRANT_AOJ_INFLUENCE.createInstance(0, 0, -1, true);
        eff.setEnforcer(enforcerId);
        return eff;
    }

    @Override
    public void refreshTime(LivingEntityBeyonderCapability cap, LivingEntity target, BeyonderEffect effect) {
        if(!(effect instanceof BeyonderAoJInfluenceEffect aojEffect)) return;
        this.enforcerMap.putAll(aojEffect.enforcerMap);
    }

    private void setEnforcer(UUID enforcer){
        this.enforcerMap.put(enforcer, 40);
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        target.addEffect(new MobEffectInstance(ModEffects.AOJ_INFLUENCE.get(), 20, 0, false, false, true));
        for(UUID id: new ArrayList<>(enforcerMap.keySet())){
            int time = enforcerMap.get(id);
            if(time < 0){
                enforcerMap.remove(id);
                if(enforcerMap.isEmpty()) endEffectWhenPossible();
            } else enforcerMap.put(id, time-1);
        }
    }

    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("mapSize", enforcerMap.size());
        int i = 0;
        for(UUID id: enforcerMap.keySet()){
            nbt.putUUID("enforcer_" + i, id);
            nbt.putInt("timeout_" + i, enforcerMap.get(id));
            i++;
        }
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        int size = nbt.getInt("mapSize");
        for(int i = 0; i < size; i++){
            UUID id = nbt.getUUID("enforcer_" + i);
            int time = nbt.getInt("timeout_" + i);
            enforcerMap.put(id, time);
        }
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    public boolean isEntityEnforcer(UUID id) {
        return enforcerMap.containsKey(id);
    }
}
