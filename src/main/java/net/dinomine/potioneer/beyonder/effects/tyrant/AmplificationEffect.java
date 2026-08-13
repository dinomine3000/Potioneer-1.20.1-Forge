package net.dinomine.potioneer.beyonder.effects.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.*;

public class AmplificationEffect extends BeyonderEffect {
    private int amplificationsLeft = 0;
    private boolean amplifyAbilities = false;
    private final Set<UUID> affectedInstances = new HashSet<>();
    private static final UUID uuid = UUID.fromString("863e9aa0-1036-498f-9818-51bdb282290d");

    @Override
    public boolean canBeCleansed() {
        return false;
    }

    public Set<UUID> getAffectedInstances(){return affectedInstances;}

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target, boolean fromLoading) {
        applyExistingAmplifications(cap, target);
    }

    @Override
    public void onUpdateReceivedOnClient(BeyonderCapability cap, LivingEntity target) {
        applyExistingAmplifications(cap, target);
    }

    private void applyExistingAmplifications(BeyonderCapability cap, LivingEntity target){
        for(UUID instanceId: new ArrayList<>(affectedInstances)){
            Ability abl = getAbilityInstance(cap.getAbilitiesManager(), instanceId);
            if(abl == null) {
                affectedInstances.remove(instanceId);
                continue;
            }
            abl.temporarilyUpgradeToLevel( uuid, -1, cap, target);
        }
    }

    public void tryAmplify(Ability abl, BeyonderCapability cap, LivingEntity target){
        if(amplificationsLeft < 1) return;
        if(affectedInstances.contains(abl.getInstanceId())) return;
        affectedInstances.add(abl.getInstanceId());
        amplificationsLeft--;
        abl.temporarilyUpgradeToLevel(uuid, -1, cap, target);
        if(target instanceof ServerPlayer player) sendDataToClient(player);
    }

    public void setAmplificationsLeft(int newMax){this.amplificationsLeft = newMax;amplifyAbilities =true;}

    @Override
    protected void doTick(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        if(amplifyAbilities) return;
        cap.getEffectsManager().statsHolder.addDamage(1 + Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addHealth(2 + 4*Math.max(0, 7 - sequenceLevel));
        //cap.getEffectsManager().statsHolder.addDefense(4 + 2*Math.max(0, 7 - sequenceLevel));
        cap.getEffectsManager().statsHolder.addResistance(4 + 2*Math.max(0, 7 - sequenceLevel));
    }

    @Override
    public void stopEffects(BeyonderCapability cap, LivingEntity target) {
        if(!amplifyAbilities) return;
        for(UUID instanceId: new ArrayList<>(affectedInstances)){
            Ability abl = getAbilityInstance(cap.getAbilitiesManager(), instanceId);
            if(abl == null) {
                affectedInstances.remove(instanceId);
                continue;
            }
            abl.removeTemporaryUpgrade(uuid, cap, target);
        }
    }

    public static Ability getAbilityInstance(PlayerAbilitiesManager ablManager, UUID instanceId){
        return ablManager.getAbilityInstance(instanceId);
    }


    @Override
    public void toNbt(CompoundTag nbt) {
        super.toNbt(nbt);
        nbt.putInt("weakeningsLeft", this.amplificationsLeft);
        nbt.putBoolean("weakenAbilities", this.amplifyAbilities);

        ListTag affectedList = new ListTag();
        for (UUID instanceId : this.affectedInstances) {
            CompoundTag keyTag = new CompoundTag();
            keyTag.putUUID("instanceId", instanceId);
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
                CompoundTag idTag = affectedList.getCompound(i);
                UUID instanceId = idTag.getUUID("instanceId");
                this.affectedInstances.add(instanceId);
            }
        }
    }

    public void abilityRemoved(UUID ablId) {
        affectedInstances.remove(ablId);
    }

    public void artifactRemoved(List<Ability> abilities) {
        for(Ability abl: abilities) abilityRemoved(abl.getInstanceId());
    }
}
