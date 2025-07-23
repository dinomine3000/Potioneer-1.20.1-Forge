package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderFigurineEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderInvisibilityEffect;
import net.dinomine.potioneer.beyonder.effects.mystery.BeyonderRegenEffect;
import net.dinomine.potioneer.beyonder.effects.wheeloffortune.BeyonderLuckReduceDamageEffect;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.ModTags;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;

public class PlayerEffectsManager {
    private ArrayList<BeyonderEffect> passives = new ArrayList<BeyonderEffect>();
    public BeyonderStats statsHolder;

    public void onAttackDamageCalculation(LivingHurtEvent event, LivingEntityBeyonderCapability cap){
        Entity attacker = event.getSource().getEntity();
        float dmg = event.getAmount();
        //TODO change this to account for multiple instances of similar effects
        if(attacker instanceof Player player){
            //additions
            if(hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION) && player.getMainHandItem().is(ModTags.Items.ELECTRIFICATION_WEAPONS)){
                player.level().playSound(null, player, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1, 0.5f);
                dmg += 3;
            }
            if(hasEffect(BeyonderEffects.EFFECT.RED_LIGHT_BUFF)){
                dmg += 3;
            }
            if(hasEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF)){
                if(player.isOnFire() && cap.getSpirituality() >= getEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF).getCost()){
                    dmg += 2;
                    cap.requestActiveSpiritualityCost(getEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF).getCost());
                }
            }
            if(hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY)){
                BeyonderEffect eff = getEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY);
                InteractionHand hand = player.getUsedItemHand();

                boolean arrow = false;
                if(event.getSource().getDirectEntity() != null){
                    arrow = event.getSource().getDirectEntity().getType().is(EntityType.ARROW.getTags().toList().get(0));
                }
                if(player.getItemInHand(hand).is(ModTags.Items.WEAPON_PROFICIENCY) || arrow){
                    dmg *= ((10-eff.getSequenceLevel()) * 0.3f + 1f);
                }
            }
            if(hasEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN)){
                BeyonderRegenEffect eff = (BeyonderRegenEffect) getEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN);
                if(attacker == event.getEntity().getLastAttacker()){
                    eff.combo = Math.min(3, eff.combo+1);
                } else {
                    eff.combo = 0;
                }
                int max = cap.getMaxSpirituality();
                cap.requestActiveSpiritualityCost(-1 * (0.0125f*max*(eff.combo+1)));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (1+eff.combo), true, true, true));
                player.getFoodData().eat(1, 0);
            }

            //mults
            if(hasEffectOrBetter(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, 7)){
                if(player.isInWater() || player.level().isRaining() || player.level().isThundering()){
                    dmg *=2;
                }
            }
            if(hasEffect(BeyonderEffects.EFFECT.RED_PURIFICATION)
                    && (event.getEntity().getMobType() == MobType.UNDEAD || event.getEntity().isOnFire())){
                BeyonderEffect effe = getEffect(BeyonderEffects.EFFECT.RED_PURIFICATION);
                dmg *= 1.2f + 0.1f*(8 - effe.getSequenceLevel());
            }
        }
        event.setAmount(dmg);
    }

    public void onTakeDamage(LivingDamageEvent event, LivingEntityBeyonderCapability cap){
        LivingEntity entity = event.getEntity();
        if(hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION)){
            if(event.getSource().is(DamageTypeTags.IS_LIGHTNING)){
                event.setAmount(0);
            }
        }
        if(hasEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE)){
            BeyonderEffect reductionEffect = getEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE, cap.getSequenceLevel());
            if(cap.getLuckManager().passesLuckCheck(
                    BeyonderLuckReduceDamageEffect.reduceChance,
                    BeyonderLuckReduceDamageEffect.luckCost,
                    BeyonderLuckReduceDamageEffect.luckGain,
                    entity.getRandom())){
                cap.requestActiveSpiritualityCost(reductionEffect.getCost());
                if(reductionEffect.getSequenceLevel() < BeyonderLuckReduceDamageEffect.sequenceForNegation){
                    event.setAmount(0);
                } else {
                    event.setAmount(event.getAmount()*BeyonderLuckReduceDamageEffect.damageReduction);
                }
            }
        }
        if(hasEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE) && event.getAmount() >= entity.getHealth()){
            BeyonderFigurineEffect effect = (BeyonderFigurineEffect) getEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE);
            Iterable<ItemStack> items = entity.getAllSlots();
            if(entity instanceof Player player) items = player.getInventory().items;
            for(ItemStack testItem: items){
                if(testItem.is(ModItems.VOODOO_DOLL.get()) && cap.getSpirituality() > effect.getCost()){
                    if(entity instanceof Player player)
                        entity.level().playSound(player, entity.getOnPos(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
                    BlockPos pos = entity.getOnPos();
                    cap.requestActiveSpiritualityCost(effect.getCost());
                    entity.setHealth(3);
                    event.setAmount(0);
                    ItemEntity drop = new ItemEntity(entity.level(), pos.getX(), pos.getY(), pos.getZ(), testItem.copyWithCount(1));
                    testItem.setCount(testItem.getCount() - 1);
                    // TODO: Drop unusable copy of voodoo doll
                    //entity.level().addFreshEntity(drop);
                    entity.playSound(SoundEvents.TOTEM_USE, 1, 1);
                    addEffect(new BeyonderInvisibilityEffect(effect.getSequenceLevel(), 0, 1200, true, BeyonderEffects.EFFECT.MYSTERY_INVIS), cap, entity);
                    break;
                }
            }
        }
    }


    public void onCraft(PlayerEvent.ItemCraftedEvent event, LivingEntityBeyonderCapability cap){
        //TODO change this to account for multiple instances of similar effects
        if(hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY)){
            cap.changeSpirituality(cap.getMaxSpirituality() * 0.01f);
        }
    }

    public void onPlayerDie(LivingDeathEvent event, LivingEntityBeyonderCapability cap) {
    }

    @Override
    public String toString(){
        String res = "";
        for(BeyonderEffect eff : passives){
            res = res.concat(eff.name.concat("\n"));
        }
        return res;
    }

    public PlayerEffectsManager(){
        statsHolder = new BeyonderStats();
    }

    public void clearEffects(LivingEntityBeyonderCapability cap, Player player){
        for(BeyonderEffect eff : passives){
            eff.stopEffects(cap, player);
        }
        this.passives = new ArrayList<>();
    }

    public boolean hasEffectOrBetter(BeyonderEffect effect){
        return passives.stream().anyMatch(effect::isBetter);
    }

    /**
     * this method will make sure that the effect you add is added as a single effect,
     * that is, it wont do anything if there already something like it or better,
     * and if it does add it it will make sure the previous effect, if it existed, is replaced
     * @param effect
     * @param cap
     * @param target
     * @return
     */
    public boolean addOrReplaceEffect(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId(), cap, target);
            addEffect(effect, cap, target);
            return true;
        } else if(hasEffect(effect.getId(), effect.getSequenceLevel())){
            getEffect(effect.getId(), effect.getSequenceLevel()).refreshTime();
            return true;
        }
        return false;
    }

    public boolean addEffectNoRefresh(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId(), cap, target);
            addEffect(effect, cap, target);
            return true;
        }
        return false;
    }

    public boolean addEffectNoCheck(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        return addEffect(effect, cap, target);
    }

    private boolean addEffect(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        passives.add(effect);
        effect.onAcquire(cap, target);
        return true;
    }

    /**
     * Add an effect without specifying a capability or target,
     * Warning: It does NOT trigger the onAcquire() method for the effect. for that, use cap
     * @param effect
     * @return
     */
//    public boolean addEffectNoNotify(BeyonderEffect effect){
//        if(hasEffect(effect.getId(), effect.getSequenceLevel())){
//            getEffect(effect.getId()).refreshTime();
//            return true;
//        } else if (!hasEffectOrBetter(effect)){
//            //doesnt deactivate previous effect bc a capability or entity were not given, so it cant call the method
//            passives.add(effect);
//            return true;
//        }
//        return false;
//    }

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

    /**
     * returns true if it finds an effect of the same ID and sequence
     * @param effect
     * @param seq
     * @return
     */
    public boolean hasEffect(BeyonderEffects.EFFECT effect, int seq){
        return passives.stream().anyMatch(eff -> eff.is(effect, seq));
    }

    public boolean hasEffectOrBetter(BeyonderEffects.EFFECT effect, int seq){
        return passives.stream().anyMatch(eff -> eff.isOrBetter(effect, seq));
    }

    public boolean hasEffect(BeyonderEffects.EFFECT effect){
        return passives.stream().anyMatch(eff -> eff.is(effect));
    }

    public BeyonderEffect getEffect(BeyonderEffects.EFFECT effect){
        return passives.get(indexOf(effect));
    }

    public BeyonderEffect getEffect(BeyonderEffects.EFFECT effect, int seq){
        return passives.get(indexOf(effect, seq));
    }

    public boolean removeEffect(BeyonderEffects.EFFECT effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect)) {
                passives.get(i).stopEffects(cap, target);
                passives.remove(i);
                return true;
            };
        }
        return false;
    }

    public boolean removeEffect(BeyonderEffects.EFFECT effect, int seq, LivingEntityBeyonderCapability cap, LivingEntity target){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect, seq)) {
                passives.get(i).stopEffects(cap, target);
                passives.remove(i);
                return true;
            };
        }
        return false;
    }

    public void onTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        statsHolder.resetStats();
        if(!passives.isEmpty()){
            passives.forEach(effect -> {
                effect.effectTick(cap, target);
            });
        }
        sweepEffects(cap, target);
        cap.getBeyonderStats().setStats(statsHolder, target);
        if(target instanceof Player player) cap.getBeyonderStats().applyEffects(player, statsHolder);
    }

    private void sweepEffects(LivingEntityBeyonderCapability cap, LivingEntity target){
        for (int i = passives.size()-1; i >= 0; i--) {
            if(passives.get(i).endsWithin(0)){
                passives.get(i).stopEffects(cap, target);
                passives.remove(i);
            }
        }
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

    public void loadNBTData(CompoundTag nbt, LivingEntityBeyonderCapability cap, LivingEntity entity){
        CompoundTag effectsTag = nbt.getCompound("effectData");
        int size = effectsTag.getInt("size");
        for(int i = 0; i < size; i++){
            CompoundTag iterator = effectsTag.getCompound(String.valueOf(i));
            BeyonderEffect effect = BeyonderEffects.byId(
                    BeyonderEffects.EFFECT.valueOf(iterator.getString("ID")),
                    iterator.getInt("level"),
                    iterator.getFloat("cost"),
                    iterator.getInt("maxLife"),
                    iterator.getBoolean("active"));
            effect.setLifetime(iterator.getInt("lifetime"));
            effect.loadNBTData(iterator);
            addEffect(effect, cap, entity);
        }
    }

    /**
     * TODO: make passives that actually want to persist in death, like shepherd graze
     * @param otherEffects
     * @param cap
     * @param player
     */
    public void copyFrom(PlayerEffectsManager otherEffects, LivingEntityBeyonderCapability cap, Player player) {
        for (BeyonderEffect passive : otherEffects.passives) {
            if(passive.shouldPersistInDeath()){
                addOrReplaceEffect(passive, cap, player);
            }
        }
    }
}
