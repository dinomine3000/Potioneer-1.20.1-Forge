package net.dinomine.potioneer.beyonder.abilities;

import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.messages.abilityRelevant.AbilitySyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public class DisabledAbilitiesManager {
    private final Map<String, DisabledAbilityProxy> disabledAbilities = new HashMap<>();

    public void disableAbility(String responsibilityKey, DisabledAbilityProxy proxy, BeyonderCapability cap, LivingEntity target){
        disabledAbilities.put(responsibilityKey, proxy);
        ensureDisabledAbilities(cap, target);
    }
    public void enableAbility(String responsibilityKey, BeyonderCapability cap, LivingEntity target){
        if(disabledAbilities.remove(responsibilityKey) == null) return;
        ensureDisabledAbilities(cap, target);
    }
    public void tickDisabledAbilities(BeyonderCapability cap, LivingEntity target){
        if(disabledAbilities.isEmpty()) return;
        List<String> toRemove = new ArrayList<>();
        for(Map.Entry<String, DisabledAbilityProxy> entry: disabledAbilities.entrySet())
            if(entry.getValue().tick()) toRemove.add(entry.getKey());
        if(toRemove.isEmpty()) return;
        for(String key: toRemove) if(disabledAbilities.get(key).shouldRemoveFromTicking()) disabledAbilities.remove(key);
        ensureDisabledAbilities(cap, target);
    }
    public void onAbilityGained(Ability abl, BeyonderCapability cap, LivingEntity target){
        ensureDisabledAbility(abl, cap, target);
    }
    public void abilityChangedLevel(Ability abl, BeyonderCapability cap, LivingEntity target){
        ensureDisabledAbility(abl, cap, target);
    }

    private void ensureDisabledAbilities(BeyonderCapability cap, LivingEntity target){
        for(Ability abl: cap.getAbilitiesManager().getAbilities()){
            ensureDisabledAbility(abl, cap, target);
        }
        if(target instanceof Player player) cap.getAbilitiesManager().updateClientAbilityInfo(player, AbilitySyncMessage.UPDATE);
    }

    private void ensureDisabledAbility(Ability abl, BeyonderCapability cap, LivingEntity target){
        boolean disableFlag = false;
        for(DisabledAbilityProxy proxy: disabledAbilities.values()){
            if(!proxy.is(abl)) continue;
            disableFlag = true;
            break;
        }
        if(disableFlag) abl.revoke(cap, target);
        else abl.undoRevoke(cap, target);
    }

    // --- NBT Serialization ---

    public CompoundTag saveNbt() {
        CompoundTag tag = new CompoundTag();
        ListTag entries = new ListTag();
        for (Map.Entry<String, DisabledAbilityProxy> entry : disabledAbilities.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("key", entry.getKey());
            entryTag.put("proxy", entry.getValue().saveNbt());
            entries.add(entryTag);
        }
        tag.put("disabledAbilities", entries);
        return tag;
    }

    public void loadNbt(CompoundTag ownTag, BeyonderCapability cap, LivingEntity target) {
        disabledAbilities.clear();
        if (ownTag.contains("disabledAbilities", Tag.TAG_LIST)) {
            ListTag entries = ownTag.getList("disabledAbilities", Tag.TAG_COMPOUND);
            for (int i = 0; i < entries.size(); i++) {
                CompoundTag entryTag = entries.getCompound(i);
                String key = entryTag.getString("key");
                DisabledAbilityProxy proxy = DisabledAbilityProxy.fromNbt(entryTag.getCompound("proxy"));
                disabledAbilities.put(key, proxy);
            }
        }
        ensureDisabledAbilities(cap, target);
    }

    public void reset(BeyonderCapability cap, LivingEntity target) {
        this.disabledAbilities.clear();
        ensureDisabledAbilities(cap, target);
    }

    public static class DisabledAbilityProxy {
        private String ablId = "";
        private int ablLevel = -1;
        private UUID instanceId = null;
        private int time = -1;
        private boolean all = false;
        private String group = "";
        //if true, disables abilities under the above group
        //if false, disables abilities not under the above group
        private boolean disableInGroup = false;
        private List<DisabledAbilityProxy> children = Collections.emptyList();

        /**
         * disables abilities of that id, up to the given level (from 9 to level)
         */
        public DisabledAbilityProxy(String ablId, int level, int time){
            this.ablId = ablId;
            this.ablLevel = level;
            this.time = time;
        }

        /**
         * disables specific ability
         */
        public DisabledAbilityProxy(UUID instanceId, int time){
            this.instanceId = instanceId;
            this.time = time;
        }

        /**
         * disables all abilities
         */
        public DisabledAbilityProxy(int time){
            this.all = true;
            this.time = time;
        }

        /**
         * disables all abilities except the given one
         */
        public DisabledAbilityProxy(String ablId, int time){
            this.all = true;
            this.ablId = ablId;
            this.time = time;
        }

        public DisabledAbilityProxy(String groupId, boolean belongsToGroup, int time){
            this.group = groupId;
            this.disableInGroup = belongsToGroup;
            this.time = time;
        }

        /**
         * represents a group of multiple proxies
         */
        public DisabledAbilityProxy(List<DisabledAbilityProxy> proxies){
            this.children = new ArrayList<>(proxies);
        }

        public static DisabledAbilityProxy of(DisabledAbilityProxy... proxies){
            return new DisabledAbilityProxy(Arrays.asList(proxies));
        }

        public boolean is(Ability abl, int level){
            if (!children.isEmpty()) {
                for (DisabledAbilityProxy child : children) {
                    if (child.is(abl, level)) return true;
                }
                return false;
            }
            if (instanceId != null) return instanceId.equals(abl.getInstanceId());
            if (all) return !abl.is(ablId);
            if(!group.isEmpty()) return disableInGroup == abl.getType().equalsIgnoreCase(group);
            return abl.is(ablId) && level >= ablLevel;
        }
        public boolean is(Ability abl){
            return is(abl, abl.getSequenceLevel());
        }

        public boolean tick(){
            if (!children.isEmpty()) {
                boolean removed = children.removeIf(DisabledAbilityProxy::tick);
                if(children.isEmpty()) time = 0;
                return removed;
            }
            if(time < 0) return false;
            return --time == 0;
        }

        public boolean shouldRemoveFromTicking() {
            return time == 0;
        }

        // --- Proxy NBT Serialization ---

        public CompoundTag saveNbt() {
            CompoundTag tag = new CompoundTag();
            tag.putString("ablId", ablId);
            tag.putInt("ablLevel", ablLevel);
            if (instanceId != null) {
                tag.putUUID("instanceId", instanceId);
            }
            tag.putInt("time", time);

            if (!children.isEmpty()) {
                ListTag childrenTag = new ListTag();
                for (DisabledAbilityProxy child : children) {
                    childrenTag.add(child.saveNbt());
                }
                tag.put("children", childrenTag);
            }

            tag.putBoolean("all", all);
            tag.putBoolean("groupType", disableInGroup);
            tag.putString("group", group);
            return tag;
        }

        public static DisabledAbilityProxy fromNbt(CompoundTag tag) {
            DisabledAbilityProxy proxy = new DisabledAbilityProxy(
                    tag.getString("ablId"),
                    tag.getInt("ablLevel"),
                    tag.getInt("time")
            );

            if (tag.hasUUID("instanceId")) {
                proxy.instanceId = tag.getUUID("instanceId");
            }

            if (tag.contains("children", Tag.TAG_LIST)) {
                ListTag childrenTag = tag.getList("children", Tag.TAG_COMPOUND);
                proxy.children = new ArrayList<>();
                for (int i = 0; i < childrenTag.size(); i++) {
                    proxy.children.add(fromNbt(childrenTag.getCompound(i)));
                }
            }

            proxy.disableInGroup =  tag.getBoolean("groupType");
            proxy.group = tag.getString("group");
            proxy.all = tag.getBoolean("all");
            return proxy;
        }

        // --- Builder Methods ---

        /**
         * Disables all abilities for the given duration.
         */
        public static DisabledAbilityProxy all(int time) {
            return new DisabledAbilityProxy(time);
        }
        /**
         * Disables all abilities for the given duration, except the given one.
         */
        public static DisabledAbilityProxy all(int time, String ablIdToIgnore) {
            return new DisabledAbilityProxy(ablIdToIgnore, time);
        }

        public static DisabledAbilityProxy ofGroup(int time, String groupId) {
            return new DisabledAbilityProxy(groupId, true, time);
        }

        public static DisabledAbilityProxy notOfGroup(int time, String groupId) {
            return new DisabledAbilityProxy(groupId, false, time);
        }

        /**
         * Disables a specific ability instance by its UUID for the given duration.
         */
        public static DisabledAbilityProxy byInstance(UUID instanceId, int time) {
            return new DisabledAbilityProxy(instanceId, time);
        }

        /**
         * Disables an ability type by ID above a minimum sequence level threshold.
         */
        public static DisabledAbilityProxy byId(String ablId, int maxLevel, int time) {
            return new DisabledAbilityProxy(ablId, maxLevel, time);
        }

        /**
         * Disables an ability type by ID regardless of sequence level.
         */
        public static DisabledAbilityProxy byId(String ablId, int time) {
            return new DisabledAbilityProxy(ablId, -1, time);
        }

        /**
         * Disables a specific ability object instance.
         */
        public static DisabledAbilityProxy byAbility(Ability abl, int time) {
            return new DisabledAbilityProxy(abl.getInstanceId(), time);
        }

        /**
         * Helper class for building composite proxies dynamically.
         */
        public static Builder composite() {
            return new Builder();
        }

        public static class Builder {
            private final List<DisabledAbilityProxy> proxies = new ArrayList<>();

            public Builder addAll(int time) {
                proxies.add(DisabledAbilityProxy.all(time));
                return this;
            }

            public Builder addInstance(UUID instanceId, int time) {
                proxies.add(DisabledAbilityProxy.byInstance(instanceId, time));
                return this;
            }

            public Builder addAbility(Ability abl, int time) {
                proxies.add(DisabledAbilityProxy.byAbility(abl, time));
                return this;
            }

            public Builder addId(String ablId, int time) {
                proxies.add(DisabledAbilityProxy.byId(ablId, time));
                return this;
            }

            public Builder addId(String ablId, int maxLevel, int time) {
                proxies.add(DisabledAbilityProxy.byId(ablId, maxLevel, time));
                return this;
            }

            public Builder add(DisabledAbilityProxy proxy) {
                proxies.add(proxy);
                return this;
            }

            public DisabledAbilityProxy build() {
                return new DisabledAbilityProxy(proxies);
            }
        }
    }
}