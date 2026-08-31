package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.dinomine.potioneer.util.misc.ModNbtUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConceptualTheftAbility extends AbilityWithOptions {
    private static final int THEFT_DAMAGE = 6;
    private static final int MAX_SLOTS = 6;
    private static final int STOLEN_ABILITY_TIME = 20 * 5 * 60;

    @Override
    public void init() {
        super.init();
        updatePrimaryOptions();
        updateSecondOptions();
    }

    private void updatePrimaryOptions() {
        AbilityOptions options = new AbilityOptions()
                .addEmptyOption("health", Component.translatable("abilityoption.potioneer.steal_health"))
                .addEmptyOption("ability", Component.translatable("abilityoption.potioneer.steal_ability"))
                .addEmptyOption("effect", Component.translatable("abilityoption.potioneer.steal_effects"))
                .addEmptyOption("luck", Component.translatable("abilityoption.potioneer.steal_luck"));
        if (getSequenceLevel() < 6) {
            options.addEmptyOption("cooldown", Component.translatable("abilityoption.potioneer.steal_cooldowns"));
        }
        setPrimaryOptions(options);
    }

    @Override
    public void onUpgrade(int oldLevel, int newLevel, BeyonderCapability cap, LivingEntity target) {
        super.onUpgrade(oldLevel, newLevel, cap, target);
        updatePrimaryOptions();
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if (!getSlotManager().hasSpace()) return false;
        return super.primary(cap, target, args);
    }

    @Override
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        return switch (args) {
            case "health" -> stealHealth(cap, target);
            case "ability" -> stealAbility(cap, target);
            case "effect" -> stealEffect(cap, target);
            case "luck" -> stealLuck(cap, target);
            case "cooldown" -> stealCooldowns(cap, target);
            default -> false;
        };
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        if (!PotioneerMathHelper.isInteger(args)) return false;
        int slotIndex = Integer.parseInt(args);
        SlotManager slots = getSlotManager();
        CompoundTag slotTag = slots.getSlot(slotIndex);
        String name = slotTag.getString("name");
        if (name.isEmpty()) return false;
        return switch (name) {
            case "ability" -> returnAbility(cap, target, slotIndex);
            case "health" -> returnHealth(cap, target, slotIndex);
            case "luck" -> returnLuck(cap, target, slotIndex);
            case "effects" -> returnEffects(cap, target, slotIndex);
            case "cooldown" -> returnCooldowns(cap, target, slotIndex);
            default -> false;
        };
    }

    private Optional<LivingEntity> getTarget(LivingEntity caster, double reachOffset) {
        return AbilityFunctionHelper.getTargetEntity(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()) + reachOffset, true);
    }

    private LivingEntity getTargetOrDefaultToCaster(LivingEntity caster, double reachOffset) {
        return getTarget(caster, reachOffset).orElse(caster);
    }

    private CompoundTag getCooldownsTag(List<Integer> cooldowns) {
        CompoundTag res = new CompoundTag();
        res.putString("name", "cooldown");
        ListTag cooldownsTag = ModNbtUtils.toNumberListTag(cooldowns);
        res.put("cooldowns", cooldownsTag);
        return res;
    }

    private List<Integer> getCooldowns(CompoundTag cdTag){
        ListTag list = cdTag.getList("cooldowns", Tag.TAG_INT);
        return ModNbtUtils.fromIntListTag(list);
    }


    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if (target.level().isClientSide()) return;
        SlotManager slots = getSlotManager();
        List<UUID> removedAbilities = slots.clearExpiredSlots(target.level().getGameTime(), target);
        for (UUID instanceId : removedAbilities) {
            cap.getAbilitiesManager().removeAbility(instanceId, cap, target, true);
        }
    }

    private boolean returnHealth(BeyonderCapability cap, LivingEntity caster, int slotIndex) {
        LivingEntity target = caster;
        Optional<LivingEntity> looking = getTarget(caster, 0);
        if (caster.getHealth() >= caster.getMaxHealth() && looking.isEmpty()) return false;
        if (caster.getHealth() >= caster.getMaxHealth()) target = looking.get();

        CompoundTag slotTag = getSlotManager().removeSlot(slotIndex, caster);
        target.heal(slotTag.getFloat("amount"));
        updateSecondOptions();
        return true;
    }

    private boolean returnAbility(BeyonderCapability cap, LivingEntity caster, int slotIndex) {
        if (caster.level().isClientSide()) return true;
        CompoundTag slotTag = getSlotManager().removeSlot(slotIndex, caster);

        UUID instanceId = slotTag.getUUID("id");
        cap.getAbilitiesManager().removeAbility(instanceId, cap, caster, true);

        UUID originalId = slotTag.getUUID("original");
        Entity ent = ((ServerLevel)caster.level()).getEntity(originalId);
        if(ent != null){
            CapProvider.beyonder(ent).ifPresent(otherCap -> otherCap.getAbilitiesManager().getDisabledAbilitiesManager().enableAbility("stolen", otherCap, (LivingEntity) ent));
        }

        updateSecondOptions();
        return true;
    }

    private boolean returnLuck(BeyonderCapability cap, LivingEntity caster, int slotIndex) {
        if (caster.level().isClientSide()) return true;
        CompoundTag slotTag = getSlotManager().removeSlot(slotIndex, caster);

        int luck = slotTag.getInt("luck");
        LivingEntity target = getTargetOrDefaultToCaster(caster, 0);

        CapProvider.beyonder(target).ifPresent(otherCap -> otherCap.getLuckManager().grantLuck(target, luck, false));

        updateSecondOptions();
        return true;
    }

    private boolean returnEffects(BeyonderCapability cap, LivingEntity caster, int slotIndex) {
        if (caster.level().isClientSide()) return true;
        CompoundTag slotTag = getSlotManager().removeSlot(slotIndex, caster);

        List<MobEffectInstance> effs = loadEffectsTag(slotTag);
        LivingEntity target = getTargetOrDefaultToCaster(caster, 0);

        for (MobEffectInstance eff : effs) target.addEffect(eff);

        updateSecondOptions();
        return true;
    }

    private boolean returnCooldowns(BeyonderCapability cap, LivingEntity caster, int index) {
        Optional<LivingEntity> optTarget = getTarget(caster, 0);
        if(optTarget.isEmpty()) return false;
        if(caster.level().isClientSide()) return true;
        LivingEntity target = optTarget.get();
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if(optCap.isEmpty()) return false;
        BeyonderCapability targetCap = optCap.get();

        CompoundTag slotTag = getSlotManager().removeSlot(index, caster);

        List<Integer> cooldowns = getCooldowns(slotTag);
        List<Ability> abls = targetCap.getAbilitiesManager().getAllAbilities();
        for(Integer cd: cooldowns){
            Ability abl = abls.get(caster.getRandom().nextInt(abls.size()));
            abl.putOnCooldown(cd, target);
            abls.remove(abl);
        }
        updateSecondOptions();
        return true;
    }


    private boolean stealHealth(BeyonderCapability cap, LivingEntity caster) {
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1);
        if (target == null) return false;
        if (caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if (optCap.isEmpty()) return false;
        BeyonderCapability targetCap = optCap.get();
        if (TheftAbility.canSteal(new PlayerLuckManager(cap.getLuckManager(), targetCap.getLuckManager()), targetCap.getSequenceLevel(), getSequenceLevel())) {
            CompoundTag dataTag = getData();
            dataTag.putInt("dmgId", target.getId());
            setDataSilent(dataTag);
            target.hurt(PotioneerDamage.theft((ServerLevel) caster.level(), caster), THEFT_DAMAGE);
            dataTag = getData();
            dataTag.remove("dmgId");
            setData(dataTag, caster);
        }
        setNextCooldownAs(20 * 10);
        updateSecondOptions();
        return true;
    }

    private boolean stealAbility(BeyonderCapability cap, LivingEntity caster) {
        Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntityClosestToCrosshair(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1, true);
        if (optTarget.isEmpty()) return false;
        LivingEntity target = optTarget.get();
        if (caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if (optCap.isEmpty()) return false;

        BeyonderCapability targetCap = optCap.get();
        List<Ability> targetAbilities = targetCap.getAbilitiesManager().getAllAbilities();
        if (targetAbilities.isEmpty()) return false;
        Ability chosenAbility = targetAbilities.get(caster.getRandom().nextInt(targetAbilities.size()));
        ResourceLocation ablId = chosenAbility.getAbilityId();

        Ability toAdd = Abilities.getFactoryAndConstruct(ablId, getSequenceLevel(), AbilityInfo.Group.STOLEN);
        if (toAdd == null) return false;
        targetCap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("stolen", DisabledAbilitiesManager.DisabledAbilityProxy.byId(ablId, STOLEN_ABILITY_TIME), targetCap, target);
        cap.getAbilitiesManager().addAndInitializeAbility(toAdd, cap, caster, true, true);
        getSlotManager().add(getAbilityTag(toAdd, caster.level().getGameTime(), target.getUUID()), caster);
        setNextCooldownAs(20 * 10);
        updateSecondOptions();
        return true;
    }

    private boolean stealEffect(BeyonderCapability cap, LivingEntity caster) {
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 0);
        if (target == null || target.getActiveEffects().isEmpty()) target = caster;
        if (target.getActiveEffects().isEmpty()) return false;
        if (caster.level().isClientSide()) return true;

        List<CompoundTag> effectList = new ArrayList<>();
        for (MobEffectInstance eff : target.getActiveEffects()) {
            effectList.add(eff.save(new CompoundTag()));
        }
        getSlotManager().add(getEffectsTag(effectList), caster);
        target.removeAllEffects();
        updateSecondOptions();
        return true;
    }

    private boolean stealLuck(BeyonderCapability cap, LivingEntity caster) {
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1);
        if (target == null) target = caster;
        if (caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if (optCap.isEmpty()) return false;
        int luck = optCap.get().getLuckManager().getLuck();
        optCap.get().getLuckManager().setLuck(0);
        setNextCooldownAs(20 * 10);
        getSlotManager().add(getLuckTag(luck), caster);
        updateSecondOptions();
        return true;
    }

    private boolean stealCooldowns(BeyonderCapability cap, LivingEntity caster) {
        LivingEntity target = getTargetOrDefaultToCaster(caster, 0);
        if (target == null) return false;
        if (caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if (optCap.isEmpty()) return false;
        BeyonderCapability targetCap = optCap.get();
        List<Integer> cooldowns = new ArrayList<>();
        for (Ability abl : targetCap.getAbilitiesManager().getAllAbilities()) {
            if (abl.getCooldown() <= 0) continue;
            cooldowns.add(abl.getCooldown());
            abl.putOnCooldown(0, target);
        }
        if (cooldowns.isEmpty()) return true;

        setNextCooldownAs(20 * 10);
        getSlotManager().add(getCooldownsTag(cooldowns), caster);
        updateSecondOptions();
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "concept_theft";
    }

    public void onVictimTakeDamage(LivingEntity entity, float amount, LivingEntity attacker) {
        if (!getData().contains("dmgId")) return;
        int recordedId = getData().getInt("dmgId");
        if (entity.getId() != recordedId) return;
        getSlotManager().add(getHealthTag(amount), attacker);
    }

    private CompoundTag getHealthTag(float amount) {
        CompoundTag res = new CompoundTag();
        res.putString("name", "health");
        res.putFloat("amount", amount);
        return res;
    }

    private CompoundTag getAbilityTag(Ability abl, long timestamp, UUID originalId) {
        CompoundTag res = new CompoundTag();
        res.putString("name", "ability");
        res.putUUID("original", originalId);
        res.putString("ablId", abl.getAbilityId().toString());
        res.putInt("level", abl.getTrueSequenceLevel());
        res.putUUID("id", abl.getInstanceId());
        res.putLong("timestamp", timestamp);
        return res;
    }

    private Tag getEffectsTag(List<CompoundTag> effectList) {
        CompoundTag resTag = new CompoundTag();
        resTag.putString("name", "effects");
        ListTag effList = new ListTag();
        effList.addAll(effectList);
        resTag.put("effects", effList);
        return resTag;
    }

    private List<MobEffectInstance> loadEffectsTag(CompoundTag tag) {
        List<MobEffectInstance> effectList = new ArrayList<>();
        ListTag listTag = tag.getList("effects", Tag.TAG_COMPOUND);
        for (int i = 0; i < listTag.size(); i++) {
            CompoundTag effectCompound = listTag.getCompound(i);
            MobEffectInstance instance = MobEffectInstance.load(effectCompound);
            if (instance != null) {
                effectList.add(instance);
            }
        }
        return effectList;
    }

    private Tag getLuckTag(int luck) {
        CompoundTag res = new CompoundTag();
        res.putString("name", "luck");
        res.putInt("luck", luck);
        return res;
    }

    private void updateSecondOptions() {
        AbilityOptions options = new AbilityOptions();
        SlotManager slots = getSlotManager();
        for (int i = 0; i < MAX_SLOTS; i++) {
            Component nameComponent = getComponentForReturnSlot(slots.getSlot(i));
            if (nameComponent == null) continue;
            options.addEmptyOption(String.valueOf(i), nameComponent);
        }
        setSecondaryOptions(options);
    }

    @Override
    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {
        super.onClientUpdate(cap, target);
        updateSecondOptions();
    }

    private @Nullable Component getComponentForReturnSlot(CompoundTag slotTag) {
        if (slotTag.isEmpty()) return null;
        return switch (slotTag.getString("name")) {
            case "health" -> Component.translatable("abilityoption.potioneer.return_health", (int) slotTag.getFloat("amount"));
            case "luck" -> Component.translatable("abilityoption.potioneer.return_luck", slotTag.getInt("luck"));
            case "effects" -> Component.translatable("abilityoption.potioneer.return_effects", slotTag.getList("effects", Tag.TAG_COMPOUND).size());
            case "ability" -> Component.translatable("abilityoption.potioneer.return_ability", Ability.getNameComponent(new ResourceLocation(slotTag.getString("ablId")), slotTag.getInt("level")));
            case "cooldown" -> Component.translatable("abilityoption.potioneer.return_cooldown", slotTag.getList("cooldowns", Tag.TAG_INT).size());
            default -> null;
        };
    }

    private SlotManager getSlotManager() {
        return new SlotManager();
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        List<Ability> abls = getStolenAbilities();
        for (Ability abl : abls) {
            cap.getAbilitiesManager().addAndInitializeAbility(abl, cap, target, true, true);
        }
    }

    @Override
    public void onAbilityRemoved(BeyonderCapability cap, LivingEntity target) {
        List<Ability> abls = getStolenAbilities();
        for (Ability abl : abls) {
            cap.getAbilitiesManager().removeAbility(abl.getInstanceId(), cap, target, true);
        }
    }

    private List<Ability> getStolenAbilities() {
        List<CompoundTag> abls = getSlotManager().getSlotsByName("ability");
        return abls.stream().map(tag -> {
            Ability abl = Abilities.getFactoryAndConstruct(new ResourceLocation(tag.getString("ablId")), tag.getInt("level"), AbilityInfo.Group.STOLEN);
            if (abl == null) return null;
            abl.setInstanceId(tag.getUUID("id"));
            return abl;
        }).toList();
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }

    private class SlotManager {

        public boolean hasSpace() {
            return getNextAvailableSlotIndex() < MAX_SLOTS;
        }

        public int getNextAvailableSlotIndex() {
            CompoundTag dataTag = getData();
            for (int i = 0; i < MAX_SLOTS; i++) {
                if (!dataTag.contains("slot_" + i)) return i;
            }
            return MAX_SLOTS;
        }

        public CompoundTag getSlot(int index) {
            return getData().getCompound("slot_" + index);
        }

        public void add(Tag tag, LivingEntity entity) {
            int nextSlot = getNextAvailableSlotIndex();
            if (nextSlot >= MAX_SLOTS) return;
            CompoundTag dataTag = getData();
            dataTag.put("slot_" + nextSlot, tag);
            setData(dataTag, entity);
        }

        public CompoundTag removeSlot(int index, LivingEntity entity) {
            CompoundTag dataTag = getData();
            CompoundTag slotTag = dataTag.getCompound("slot_" + index);
            dataTag.remove("slot_" + index);
            setData(dataTag, entity);
            return slotTag;
        }

        public List<CompoundTag> getSlotsByName(String name) {
            CompoundTag dataTag = getData();
            List<CompoundTag> matches = new ArrayList<>();
            for (int i = 0; i < MAX_SLOTS; i++) {
                if (dataTag.contains("slot_" + i)) {
                    CompoundTag slotTag = dataTag.getCompound("slot_" + i);
                    if (slotTag.getString("name").equalsIgnoreCase(name)) {
                        matches.add(slotTag);
                    }
                }
            }
            return matches;
        }

        public List<UUID> clearExpiredSlots(long currentGameTime, LivingEntity entity) {
            CompoundTag dataTag = getData();
            List<String> keysToRemove = new ArrayList<>();
            List<UUID> removedAbilities = new ArrayList<>();

            for (int i = 0; i < MAX_SLOTS; i++) {
                String key = "slot_" + i;
                if (!dataTag.contains(key)) continue;
                CompoundTag slotTag = dataTag.getCompound(key);
                if (!slotTag.contains("timestamp")) continue;
                if (currentGameTime - slotTag.getLong("timestamp") > STOLEN_ABILITY_TIME) {
                    keysToRemove.add(key);
                    if (slotTag.hasUUID("id")) {
                        removedAbilities.add(slotTag.getUUID("id"));
                    }
                }
            }

            for (String key : keysToRemove) {
                dataTag.remove(key);
            }

            if (!keysToRemove.isEmpty()) {
                setData(dataTag, entity);
            }

            return removedAbilities;
        }
    }
}