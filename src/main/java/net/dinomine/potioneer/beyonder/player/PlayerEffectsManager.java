package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.BeyonderEffectSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlayerEffectsManager {
    private ArrayList<BeyonderEffect> passives = new ArrayList<>();
    public BeyonderStats statsHolder;

    public void onAttackProposal(LivingAttackEvent event, LivingEntityBeyonderCapability cap){
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;
        LivingEntity victim = event.getEntity();
        LivingEntityBeyonderCapability victimCap = victim.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();

        for(BeyonderEffect effect: passives){
            if(effect.onDamageProposal(event, victim, attacker, victimCap, cap, false)){
                if(event.isCancelable()) event.setCanceled(true);
                return;
            }
        }
        for(BeyonderEffect effect: victimCap.getEffectsManager().passives){
            if(effect.onDamageProposal(event, victim, attacker, victimCap, cap, true)){
                if(event.isCancelable()) event.setCanceled(true);
                return;
            }
        }
    }

    public void onAttackDamageCalculation(LivingHurtEvent event, LivingEntityBeyonderCapability cap){
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;
        LivingEntity victim = event.getEntity();
        LivingEntityBeyonderCapability victimCap = victim.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve().get();

        for(BeyonderEffect effect: passives){
            if(effect.onDamageCalculation(event, victim, attacker, victimCap, cap, false)){
                if(event.isCancelable()) event.setCanceled(true);
                else event.setAmount(0);
                return;
            }
        }
        for(BeyonderEffect effect: victimCap.getEffectsManager().passives){
            if(effect.onDamageCalculation(event, victim, attacker, victimCap, cap, true)){
                if(event.isCancelable()) event.setCanceled(true);
                else event.setAmount(0);
                return;
            }
        }
//        if(attacker instanceof Player player){
            //additions
//            if(hasEffect(BeyonderEffects.TYRANT_ELECTRIFICATION) && player.getMainHandItem().is(ModTags.Items.ELECTRIFICATION_WEAPONS)){
//                player.level().playSound(null, player, SoundEvents.LIGHTNING_BOLT_IMPACT, SoundSource.PLAYERS, 1, 0.5f);
//                dmg += 5;
//            }
//            if(hasEffect(BeyonderEffects.RED_LIGHT_BUFF)){
//                dmg += 4;
//            }
//            if(hasEffect(BeyonderEffects.RED_FIRE_BUFF)){
//                if(player.isOnFire() && cap.getSpirituality() >= getEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF).getCost()){
//                    dmg += 2;
//                    cap.requestActiveSpiritualityCost(getEffect(BeyonderEffects.EFFECT.RED_FIRE_BUFF).getCost());
//                }
//            }
//            if(hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY)){
//                BeyonderEffect eff = getEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY);
//                InteractionHand hand = player.getUsedItemHand();
//
//                boolean arrow = false;
//                if(event.getSource().getDirectEntity() != null){
//                    arrow = event.getSource().getDirectEntity().getType().is(EntityType.ARROW.getTags().toList().get(0));
//                }
//                if(player.getItemInHand(hand).is(ModTags.Items.WEAPON_PROFICIENCY) || arrow){
//                    dmg *= ((10-eff.getSequenceLevel()) * 0.3f + 1f);
//                }
//            }
//            if(hasEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN)
//                    && attacker.position().distanceTo(event.getEntity().position()) < MysteryPathway.MAX_SAP_DISTANCE){
//                BeyonderRegenEffect eff = (BeyonderRegenEffect) getEffect(BeyonderEffects.EFFECT.MYSTERY_REGEN);
//                if(attacker == event.getEntity().getLastAttacker()){
//                    eff.combo = Math.min(3, eff.combo+1);
//                } else {
//                    eff.combo = 0;
//                }
//                int max = cap.getMaxSpirituality();
//                cap.requestActiveSpiritualityCost(-1 * (0.0125f*max*(eff.combo+1)));
//                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, (1+eff.combo), true, true, true));
//                player.getFoodData().eat(1, 0);
//            }
//
//            //mults
//            if(hasEffectOrBetter(BeyonderEffects.EFFECT.TYRANT_WATER_AFFINITY, 7)){
//                if(player.isInWater() || player.level().isRaining() || player.level().isThundering()){
//                    dmg *=2;
//                }
//            }
//            if(hasEffect(BeyonderEffects.EFFECT.RED_PURIFICATION)
//                    && (event.getEntity().getMobType() == MobType.UNDEAD || event.getEntity().isOnFire())){
//                BeyonderEffect effe = getEffect(BeyonderEffects.EFFECT.RED_PURIFICATION);
//                dmg *= 1.2f + 0.1f*(8 - effe.getSequenceLevel());
//            }
//        }
    }

    public void onTakeDamage(LivingDamageEvent event, LivingEntityBeyonderCapability cap){
        LivingEntity victim = event.getEntity();
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;
        for(BeyonderEffect effect: passives){
            if(effect.onTakeDamage(event, victim, attacker, cap, attacker == null ? Optional.empty() : attacker.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve())){
                if(event.isCancelable()) event.setCanceled(true);
                else event.setAmount(0);
                return;
            }
        }
//        if(hasEffect(BeyonderEffects.EFFECT.TYRANT_ELECTRIFICATION)){
//            if(event.getSource().is(DamageTypeTags.IS_LIGHTNING)){
//                event.setAmount(0);
//            }
//        }
//        if(hasEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE)){
//            BeyonderEffect reductionEffect = getEffect(BeyonderEffects.EFFECT.WHEEL_DAMAGE_REDUCE, cap.getSequenceLevel());
//            if(cap.getLuckManager().passesLuckCheck(
//                    BeyonderLuckReduceDamageEffect.reduceChance,
//                    BeyonderLuckReduceDamageEffect.luckCost,
//                    BeyonderLuckReduceDamageEffect.luckGain,
//                    entity.getRandom())){
//                cap.requestActiveSpiritualityCost(reductionEffect.getCost());
//                if(reductionEffect.getSequenceLevel() < BeyonderLuckReduceDamageEffect.sequenceForNegation){
//                    event.setAmount(0);
//                } else {
//                    event.setAmount(event.getAmount()*BeyonderLuckReduceDamageEffect.damageReduction);
//                }
//            }
//        }
//        if(hasEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE) && event.getAmount() >= entity.getHealth()){
//            BeyonderFigurineEffect effect = (BeyonderFigurineEffect) getEffect(BeyonderEffects.EFFECT.MYSTERY_FIGURINE);
//            Iterable<ItemStack> items = entity.getAllSlots();
//            if(entity instanceof Player player) items = player.getInventory().items;
//            for(ItemStack testItem: items){
//                if(testItem.is(ModItems.VOODOO_DOLL.get()) && cap.getSpirituality() > effect.getCost()){
//                    if(entity instanceof Player player)
//                        entity.level().playSound(player, entity.getOnPos(), SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 1, 1);
//                    BlockPos pos = entity.getOnPos();
//                    cap.requestActiveSpiritualityCost(effect.getCost());
//                    entity.setHealth(3);
//                    event.setAmount(0);
//                    ItemEntity drop = new ItemEntity(entity.level(), pos.getX(), pos.getY(), pos.getZ(), testItem.copyWithCount(1));
//                    testItem.setCount(testItem.getCount() - 1);
//                    // TODO: Drop unusable copy of voodoo doll
//                    //entity.level().addFreshEntity(drop);
//                    entity.playSound(SoundEvents.TOTEM_USE, 1, 1);
//                    addEffect(new BeyonderInvisibilityEffect(effect.getSequenceLevel(), 0, 1200, true, BeyonderEffects.EFFECT.MYSTERY_INVIS), cap, entity);
//                    break;
//                }
//            }
//        }
    }


    public void onCraft(PlayerEvent.ItemCraftedEvent event, LivingEntityBeyonderCapability cap){
//        if(hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_SPIRITUALITY)){
//            cap.changeSpirituality(cap.getMaxSpirituality() * 0.01f);
//        }
    }

    public void onPlayerDie(LivingDeathEvent event, LivingEntityBeyonderCapability cap) {
    }

    @Override
    public String toString(){
        String res = "";
        for(BeyonderEffect eff : passives){
            res = res.concat(eff.getId().concat(String.valueOf(eff.getSequenceLevel())).concat("\n"));
        }
        return res;
    }

    public PlayerEffectsManager(){
        statsHolder = new BeyonderStats();
    }

    public void clearEffects(LivingEntityBeyonderCapability cap, LivingEntity target){
        for(BeyonderEffect eff : passives){
            eff.stopEffects(cap, target);
        }
        this.passives = new ArrayList<>();
    }

    public boolean hasEffectOrBetter(BeyonderEffect effect){
        return passives.stream().anyMatch(effect::isBetter);
    }

    /**
     * this method will make sure that the effect you add is added as a single effect,
     * that is, it wont do anything if there already something like it or better,
     * and if it does add it it will make sure the previous effect, if it existed, is removed, and then this one is added
     * @param effect
     * @param cap
     * @param target
     * @return
     */
    public boolean addOrReplaceEffect(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        if(effect == null) return false;
        if(!effect.canAdd(cap, target)) return false;
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId());
            addEffect(effect, cap, target, true);
            return true;
        } else if(hasEffect(effect.getId(), effect.getSequenceLevel())){
            BeyonderEffect oldEffect = getEffect(effect.getId(), effect.getSequenceLevel());
            oldEffect.refreshTime(cap, target, effect);
            return true;
        }
        return false;
    }

    public boolean addEffectNoRefresh(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId());
            addEffect(effect, cap, target, true);
            return true;
        }
        return false;
    }

    public boolean addEffectNoCheck(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        return addEffect(effect, cap, target, true);
    }

    private boolean addEffect(BeyonderEffect effect, LivingEntityBeyonderCapability cap, LivingEntity target, boolean sync){
        passives.add(effect);
        effect.onAcquire(cap, target);
        if(sync) sendUpdateToClient(List.of(effect), BeyonderEffectSyncMessage.ADD, target);
        return true;
    }

    public int indexOf(String effectId){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effectId)) return i;
        }
        return -1;
    }

    public int indexOf(String effect, int seq){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect, seq)) return i;
        }
        return -1;
    }

    /**
     * returns true if it finds an effect of the same ID and sequence
     * @param effectId
     * @param seq
     * @return
     */
    public boolean hasEffect(String effectId, int seq){
        return passives.stream().anyMatch(eff -> eff.is(effectId, seq));
    }

    public boolean hasEffectOrBetter(String effectId, int seq){
        return passives.stream().anyMatch(eff -> eff.isOrBetter(effectId, seq));
    }

    public boolean hasEffect(String effectId){
        return passives.stream().anyMatch(eff -> eff.is(effectId));
    }

    public BeyonderEffect getEffect(String effect){
        int idx = indexOf(effect);
        if(idx < 0) return null;
        return passives.get(idx);
    }

    public BeyonderEffect getEffect(String effect, int seq){
        int idx = indexOf(effect, seq);
        if(idx < 0) return null;
        return passives.get(idx);
    }

    public boolean removeEffect(String effect){
        for (BeyonderEffect passive : passives) {
            if (passive.is(effect)) {
                passive.endEffectWhenPossible();
                return true;
            }
        }
        return false;
    }

    public boolean removeEffect(String effect, int seq){
        for (BeyonderEffect passive : passives) {
            if (passive.is(effect, seq)) {
                passive.endEffectWhenPossible();
                return true;
            }
        }
        return false;
    }

    private void sendUpdateToClient(List<BeyonderEffect> effects, int operation, LivingEntity target){
        if(target instanceof Player player && !target.level().isClientSide())
            PacketHandler.sendMessageSTC(new BeyonderEffectSyncMessage(effects, operation), player);
    }

    public void addEffectsOnClient(List<BeyonderEffect> effects, @NotNull LivingEntityBeyonderCapability cap, Player player) {
        for(BeyonderEffect eff: effects){
            addEffect(eff, cap, player, false);
        }
    }

    public void removeEffectsOnClient(List<BeyonderEffect> effects, @NotNull LivingEntityBeyonderCapability cap, Player player) {
        for(BeyonderEffect eff: effects){
            removeEffect(eff.getId(), eff.getSequenceLevel());
        }
    }

    public void setEffectsOnClient(List<BeyonderEffect> effects, @NotNull LivingEntityBeyonderCapability cap, Player player) {
        clearEffects(cap, player);
        for(BeyonderEffect eff: effects){
            addEffect(eff, cap, player, false);
        }
    }

    public void syncToClient(Player player) {
        sendUpdateToClient(passives, BeyonderEffectSyncMessage.SET, player);
    }

    public void onTick(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()){
            if(!passives.isEmpty()){
                passives.forEach(effect -> {
                    effect.effectTick(cap, target);
                });
            }
            sweepEffects(cap, target);
            statsHolder.resetStats();
            return;
        }
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
                BeyonderEffect eff = passives.get(i);
                eff.stopEffects(cap, target);
                passives.remove(i);
                sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
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
            BeyonderEffects.BeyonderEffectType type = BeyonderEffects.getEffect(iterator.getString("ID"));
            if(type == null) {
                System.out.println("Warning: read NBT data of a null effect: " + iterator);
                continue;
            }
            BeyonderEffect effect = type.createInstance(
                        iterator.getInt("level"),
                        iterator.getInt("maxLife"),
                        iterator.getInt("cost"),
                        iterator.getBoolean("active"));
            effect.setLifetime(iterator.getInt("lifetime"));
            effect.loadNBTData(iterator);
            addEffect(effect, cap, entity, false);
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
