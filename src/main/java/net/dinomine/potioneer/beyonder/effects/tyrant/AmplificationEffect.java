package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityKey;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashSet;
import java.util.Set;

public class AmplificationEffect extends BeyonderEffect {
    private int amplificationsLeft = 0;
    private boolean amplifyAbilities = false;
    private final Set<AbilityKey> affectedInstances = new HashSet<>();

    @Override
    public boolean canBeCleansed() {
        return false;
    }

    public Set<AbilityKey> getAffectedInstances(){return affectedInstances;}

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        if(!fromLoading) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            abl.upgradeToLevelSilently(abl.getSequenceLevel() - 1, cap, target);
        }
    }

    public int canAmplify(AbilityKey ablKey, LivingEntity target){
        if(amplificationsLeft < 1) return -1;
        if(affectedInstances.contains(ablKey)) return -1;
        affectedInstances.add(ablKey);
        amplificationsLeft--;
        if(target instanceof ServerPlayer player) sendDataToClient(player);
        return sequenceLevel;
    }

    public void setAmplificationsLeft(int newMax){this.amplificationsLeft = newMax;amplifyAbilities =true;}

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(amplifyAbilities) return;
        cap.getEffectsManager().statsHolder.addDamage(1 + Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addHealth(2 + 4*Math.max(0, 7 - sequenceLevel));
        //cap.getEffectsManager().statsHolder.addDefense(4 + 2*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addResistance(4 + 2*Math.max(0, 7 - sequenceLevel));
    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(!amplifyAbilities) return;
        for(AbilityKey key: affectedInstances){
            Ability abl = cap.getAbilitiesManager().getAbility(key);
            if(abl == null) continue;
            abl.upgradeToLevelSilently(abl.getSequenceLevel() + 1, cap, target);
        }
    }


    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("weakeningsLeft", this.amplificationsLeft);
        nbt.putBoolean("weakenAbilities", this.amplifyAbilities);

        ListTag affectedList = new ListTag();
        for (AbilityKey key : this.affectedInstances) {
            CompoundTag keyTag = new CompoundTag();
            keyTag.putString("key", key.toString());
            affectedList.add(keyTag);
        }
        nbt.put("affectedInstances", affectedList);
    }

    @Override
    public void loadNBTData(CompoundTag nbt) {
        super.loadNBTData(nbt);
        if (nbt.contains("weakeningsLeft")) {
            this.amplificationsLeft = nbt.getInt("weakeningsLeft");
        }
        if (nbt.contains("weakenAbilities")) {
            this.amplifyAbilities = nbt.getBoolean("weakenAbilities");
        }

        this.affectedInstances.clear();
        if (nbt.contains("affectedInstances", Tag.TAG_LIST)) {
            ListTag affectedList = nbt.getList("affectedInstances", Tag.TAG_COMPOUND);
            for (int i = 0; i < affectedList.size(); i++) {
                CompoundTag keyTag = affectedList.getCompound(i);
                AbilityKey key = AbilityKey.fromString(keyTag.getString("key"));
                this.affectedInstances.add(key);
            }
        }
    }
}
