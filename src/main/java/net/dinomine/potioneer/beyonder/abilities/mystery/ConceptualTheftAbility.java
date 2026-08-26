package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.*;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ConceptualTheftAbility extends AbilityWithOptions {
    private static final int THEFT_DAMAGE = 6;
    private static final int MAX_SLOTS = 6;
    private static final int STOLEN_ABILITY_TIME = 20*5*60;
    @Override
    public void init() {
        super.init();
        setPrimaryOptions(new AbilityOptions()
                .addEmptyOption("health", Component.translatable("abilityoption.potioneer.steal_health"))
                .addEmptyOption("ability", Component.translatable("abilityoption.potioneer.steal_ability"))
                .addEmptyOption("effect", Component.translatable("abilityoption.potioneer.steal_effects"))
                .addEmptyOption("luck", Component.translatable("abilityoption.potioneer.steal_luck")));
        updateSecondOptions();
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(!hasSpace()) return false;
        return super.primary(cap, target, args);
    }

    @Override
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        return switch (args){
            case "health" -> stealHealth(cap, target);
            case "ability" -> stealAbility(cap, target);
            case "effect" -> stealEffect(cap, target);
            case "luck" -> stealLuck(cap, target);
            default -> false;
        };
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        if(!PotioneerMathHelper.isInteger(args)) return false;
        CompoundTag slotTag = getData().getCompound("slot_" + args);
        String name = slotTag.getString("name");
        if(name.isEmpty()) return false;
        return switch(name){
            case "ability" -> returnAbility(cap, target, args);
            case "health" -> returnHealth(cap, target, args);
            case "luck" -> returnLuck(cap, target, args);
            case "effects" -> returnEffects(cap, target, args);
            default -> false;
        };
    }

    @Override
    public void passive(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return;
        List<String> keysToRemove = new ArrayList<>();
        CompoundTag dataTag = getData();
        for(int i = 0; i < MAX_SLOTS; i++){
            if(!dataTag.contains("slot_" + i)) continue;
            CompoundTag slotTag = dataTag.getCompound("slot_" + i);
            if(!slotTag.contains("timestamp")) continue;
            if(target.level().getGameTime() - slotTag.getLong("timestamp") > STOLEN_ABILITY_TIME) keysToRemove.add("slot_" + i);
        }
        for(String key: keysToRemove) {
            UUID instanceId = dataTag.getCompound(key).getUUID("instanceId");
            dataTag.remove(key);
            cap.getAbilitiesManager().removeAbility(instanceId, cap, target, true);
        }
        if(!keysToRemove.isEmpty()) setData(dataTag, target);
    }

    private boolean returnHealth(BeyonderCapability cap, LivingEntity caster, String index){
        LivingEntity target = caster;
        Optional<LivingEntity> looking = AbilityFunctionHelper.getTargetEntity(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), true);
        if(caster.getHealth() >= caster.getMaxHealth() && looking.isEmpty()) return false;
        if(caster.getHealth() >= caster.getMaxHealth()) target = looking.get();
        CompoundTag dataTag = getData();
        CompoundTag slotTag = dataTag.getCompound("slot_" + index);
        target.heal(slotTag.getFloat("amount"));
        dataTag.remove("slot_" + index);
        setData(dataTag, caster);
        updateSecondOptions();
        return true;
    }
    private boolean returnAbility(BeyonderCapability cap, LivingEntity caster, String index){
        if(caster.level().isClientSide()) return true;
        CompoundTag dataTag = getData();
        CompoundTag slotTag = dataTag.getCompound("slot_" + index);

        UUID instanceId = slotTag.getUUID("instanceId");
        cap.getAbilitiesManager().removeAbility(instanceId, cap, caster, true);

        dataTag.remove("slot_" + index);
        setData(dataTag, caster);
        updateSecondOptions();
        return true;
    }
    private boolean returnLuck(BeyonderCapability cap, LivingEntity caster, String index){
        if(caster.level().isClientSide()) return true;
        CompoundTag dataTag = getData();
        CompoundTag slotTag = dataTag.getCompound("slot_" + index);

        int luck = slotTag.getInt("luck");
        LivingEntity target;
        Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntity(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), true);
        target = optTarget.orElse(caster);

        CapProvider.beyonder(target).ifPresent(otherCap -> otherCap.getLuckManager().grantLuck(target, luck, false));

        dataTag.remove("slot_" + index);
        setData(dataTag, caster);
        updateSecondOptions();
        return true;
    }
    private boolean returnEffects(BeyonderCapability cap, LivingEntity caster, String index){
        if(caster.level().isClientSide()) return true;
        CompoundTag dataTag = getData();
        CompoundTag slotTag = dataTag.getCompound("slot_" + index);


        List<MobEffectInstance> effs = loadEffectsTag(slotTag);

        LivingEntity target;
        Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntity(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), true);
        target = optTarget.orElse(caster);

        for(MobEffectInstance eff: effs) target.addEffect(eff);

        dataTag.remove("slot_" + index);
        setData(dataTag, caster);
        updateSecondOptions();
        return true;
    }
    private boolean stealHealth(BeyonderCapability cap, LivingEntity caster){
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1);
        if(target == null) return false;
        if(caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if(optCap.isEmpty()) return false;
        BeyonderCapability targetCap = optCap.get();
        if(TheftAbility.canSteal(new PlayerLuckManager(cap.getLuckManager(), targetCap.getLuckManager()), targetCap.getSequenceLevel(), getSequenceLevel())){
            CompoundTag dataTag = getData();
            dataTag.putInt("dmgId", target.getId());
            setDataSilent(dataTag);
            //to proceed to storing the theft, we need to wait for the damage to be calculated. for that, we instead record in Data the ID of the victim,
            //and later, an event for receiving/taking damage will find this ability and update the actual damage dealt.
            target.hurt(PotioneerDamage.theft((ServerLevel) caster.level(), caster), THEFT_DAMAGE);
            dataTag = getData();
            dataTag.remove("dmgId");
            setData(dataTag, caster);
        }
        setNextCooldownAs(20*10);
        updateSecondOptions();
        return true;
    }
    private boolean stealAbility(BeyonderCapability cap, LivingEntity caster){
        Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntityClosestToCrosshair(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1, true);
        if(optTarget.isEmpty()) return false;
        LivingEntity target = optTarget.get();
        if(caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if(optCap.isEmpty()) return false;

        BeyonderCapability targetCap = optCap.get();
        List<Ability> targetAbilities = targetCap.getAbilitiesManager().getAllAbilities();
        if(targetAbilities.isEmpty()) return false;
        Ability chosenAbility = targetAbilities.get(caster.getRandom().nextInt(targetAbilities.size()));
        ResourceLocation ablId = chosenAbility.getAbilityId();

        Ability toAdd = Abilities.getFactoryAndConstruct(ablId, getSequenceLevel(), AbilityInfo.Group.STOLEN);
        if(toAdd == null) return false;
        targetCap.getAbilitiesManager().getDisabledAbilitiesManager().disableAbility("stolen", DisabledAbilitiesManager.DisabledAbilityProxy.byId(ablId, STOLEN_ABILITY_TIME), targetCap, target);
        cap.getAbilitiesManager().addAndInitializeAbility(toAdd, cap, caster, true, true);
        setDataInNextSlot(getAbilityTag(toAdd, caster.level().getGameTime()), caster);
        setNextCooldownAs(20*10);
        updateSecondOptions();
        return true;
    }
    private boolean stealEffect(BeyonderCapability cap, LivingEntity caster){
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 0);
        if(target == null) target = caster;
        if(target.getActiveEffects().isEmpty()) target = caster;
        if(target.getActiveEffects().isEmpty()) return false;
        if(caster.level().isClientSide()) return true;

        List<CompoundTag> effectList = new ArrayList<>();
        for(MobEffectInstance eff: target.getActiveEffects()){
            effectList.add(eff.save(new CompoundTag()));
        }
        setDataInNextSlot(getEffectsTag(effectList), caster);
        target.removeAllEffects();
        updateSecondOptions();
        return true;
    }
    private boolean stealLuck(BeyonderCapability cap, LivingEntity caster){
        LivingEntity target = AbilityFunctionHelper.getLivingEntityLooking(caster, TheftAbility.getTheftReach(caster, getSequenceLevel()), 1);
        if(target == null) target = caster;
        if(caster.level().isClientSide) return true;
        Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if(optCap.isEmpty()) return false;
        int luck = optCap.get().getLuckManager().getLuck();
        optCap.get().getLuckManager().setLuck(0);
        setNextCooldownAs(20*10);
        setDataInNextSlot(getLuckTag(luck), caster);
        updateSecondOptions();
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "concept_theft";
    }

    public void onVictimTakeDamage(LivingEntity entity, float amount, LivingEntity attacker) {
        if(!getData().contains("dmgId")) return;
        int recordedId = getData().getInt("dmgId");
        if(entity.getId() != recordedId) return;
        setDataInNextSlot(getHealthTag(amount), attacker);
    }

    private void setDataInNextSlot(Tag tag, LivingEntity target){
        int nextSlot = getNextAvailableSlotIndex();
        if(nextSlot >= MAX_SLOTS) return;
        CompoundTag dataTag = getData();
        dataTag.put("slot_" + nextSlot, tag);
        setData(dataTag, target);
    }

    private CompoundTag getHealthTag(float amount){
        CompoundTag res = new CompoundTag();
        res.putString("name", "health");
        res.putFloat("amount", amount);
        return res;
    }

    private CompoundTag getAbilityTag(Ability abl, long timestamp){
        CompoundTag res = new CompoundTag();
        res.putString("name", "ability");
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

    private void updateSecondOptions(){
        AbilityOptions options = new AbilityOptions();
        for(int i = 0; i < MAX_SLOTS; i++){
            Component nameComponent = getComponentForReturnSlot(i);
            if(nameComponent == null) continue;
            options.addEmptyOption(String.valueOf(i), nameComponent);
        }
        setSecondaryOptions(options);
    }

    @Override
    protected void onClientUpdate(BeyonderCapability cap, LivingEntity target) {
        super.onClientUpdate(cap, target);
        updateSecondOptions();
    }

    private @Nullable Component getComponentForReturnSlot(int i){
        CompoundTag slotTag = getData().getCompound("slot_" + i);
        if(slotTag.isEmpty()) return null;
        return switch(slotTag.getString("name")){
            case "health" -> Component.translatable("abilityoption.potioneer.return_health", (int) slotTag.getFloat("amount"));
            case "luck" -> Component.translatable("abilityoption.potioneer.return_luck", slotTag.getInt("luck"));
            case "effects" -> Component.translatable("abilityoption.potioneer.return_effects", i);
            case "ability" -> Component.translatable("abilityoption.potioneer.return_ability", Ability.getNameComponent(new ResourceLocation(slotTag.getString("ablId")), slotTag.getInt("level")));
            default -> null;
        };
    }

    private boolean hasSpace(){
        return getNextAvailableSlotIndex() < MAX_SLOTS;
    }

    private int getNextAvailableSlotIndex(){
        for(int i = 0; i < MAX_SLOTS; i++){
            if(!getData().contains("slot_" + i)) return i;
        }
        return MAX_SLOTS;
    }

    @Override
    public void onAcquire(BeyonderCapability cap, LivingEntity target) {
        List<Ability> abls = getStolenAbilities();
        for(Ability abl: abls){
            cap.getAbilitiesManager().addAndInitializeAbility(abl, cap, target, true, true);
        }
    }

    @Override
    public void onAbilityRemoved(BeyonderCapability cap, LivingEntity target) {
        List<Ability> abls = getStolenAbilities();
        for(Ability abl: abls){
            cap.getAbilitiesManager().removeAbility(abl.getInstanceId(), cap, target, true);
        }
    }

    private List<Ability> getStolenAbilities(){
        CompoundTag dataTag = getData();
        List<CompoundTag> abls = new ArrayList<>();
        for(int i = 0; i < MAX_SLOTS; i++){
            if(dataTag.contains("slot_" + i) && dataTag.getCompound("slot_" + i).getString("name").equalsIgnoreCase("ability")) abls.add(dataTag.getCompound("slot_" + i));
        }
        return abls.stream().map(tag -> {
            Ability abl = Abilities.getFactoryAndConstruct(new ResourceLocation(tag.getString("ablId")), tag.getInt("level"), AbilityInfo.Group.STOLEN);
            if(abl == null) return null;
            abl.setInstanceId(tag.getUUID("id"));
            return abl;
        }).toList();
    }

    @Override
    protected boolean hasSecondary(int level) {
        return true;
    }
}
