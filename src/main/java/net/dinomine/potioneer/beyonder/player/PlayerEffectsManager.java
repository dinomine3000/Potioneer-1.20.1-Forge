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

import java.util.*;

public class PlayerEffectsManager {
    private final PriorityQueue<BeyonderEffect> passives = new PriorityQueue<>(Comparator.comparingInt(BeyonderEffect::getPriority));
    public BeyonderStats statsHolder;

    //called from victim perspective
    public void onAttackProposal(LivingAttackEvent event, LivingEntityBeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<LivingEntityBeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        }

        if(optAttackerCap.isPresent()){
            for(BeyonderEffect effect: optAttackerCap.get().getEffectsManager().passives){
                if(effect.onDamageProposal(event, victim, attacker, cap, optAttackerCap, false)){
                    if(event.isCancelable()) event.setCanceled(true);
                    return;
                }
            }
        }
        for(BeyonderEffect effect: passives){
            if(effect.onDamageProposal(event, victim, attacker, cap, optAttackerCap, true)){
                if(event.isCancelable()) event.setCanceled(true);
                return;
            }
        }
    }

    /**
     * called from the attackers perspective -> cap is the attackers capability
     * @param event
     * @param cap
     */
    public void onAttackDamageCalculation(LivingHurtEvent event, LivingEntityBeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<LivingEntityBeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        }

        if(optAttackerCap.isPresent()) {
            for(BeyonderEffect effect: optAttackerCap.get().getEffectsManager().passives){
                if(effect.onDamageCalculation(event, victim, attacker, cap, optAttackerCap, false)){
                    if(event.isCancelable()) event.setCanceled(true);
                    return;
                }
            }
        }
        for(BeyonderEffect effect: passives){
            if(effect.onDamageCalculation(event, victim, attacker, cap, optAttackerCap, true)){
                if(event.isCancelable()) event.setCanceled(true);
                return;
            }
        }
    }

    public void onTakeDamage(LivingDamageEvent event, LivingEntityBeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<LivingEntityBeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        }

        if(optAttackerCap.isPresent()){
            for(BeyonderEffect effect: optAttackerCap.get().getEffectsManager().passives){
                if(effect.onTakeDamage(event, victim, attacker, cap, optAttackerCap, false)){
                    if(event.isCancelable()) event.setCanceled(true);
                    return;
                }
            }
        }
        for(BeyonderEffect effect: passives){
            if(effect.onTakeDamage(event, victim, attacker, cap, optAttackerCap, true)){
                if(event.isCancelable()) event.setCanceled(true);
                return;
            }
        }
    }


    public void onCraft(PlayerEvent.ItemCraftedEvent event, LivingEntityBeyonderCapability cap){
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
        this.passives.clear();
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
    public boolean hasEffect(BeyonderEffects.BeyonderEffectType effect){
        return hasEffect(effect.getEffectId());
    }

    public BeyonderEffect getEffect(String effect){
        for (BeyonderEffect eff : passives) {
            if(eff.is(effect)) return eff;
        }
        return null;
    }

    public BeyonderEffect getEffect(String effect, int seq){
        for (BeyonderEffect eff : passives) {
            if(eff.is(effect, seq)) return eff;
        }
        return null;
    }

    public boolean instantRemoveEffect(LivingEntityBeyonderCapability cap, LivingEntity target, String effect){
        return passives.removeIf(eff -> {
            if(!eff.is(effect)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public boolean instantRemoveEffect(LivingEntityBeyonderCapability cap, LivingEntity target, String effect, int sequenceLevel){
        return passives.removeIf(eff -> {
            if(!eff.is(effect, sequenceLevel)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public boolean removeEffect(String effect){
        boolean flag = false;
        for (BeyonderEffect passive : passives) {
            if (passive.is(effect)) {
                passive.endEffectWhenPossible();
                flag = true;
            }
        }
        return flag;
    }

    public boolean removeEffect(String effect, int seq){
        boolean flag = false;
        for (BeyonderEffect passive : passives) {
            if (passive.is(effect, seq)) {
                passive.endEffectWhenPossible();
                flag = true;
            }
        }
        return flag;
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
        sendUpdateToClient(new ArrayList<>(passives), BeyonderEffectSyncMessage.SET, player);
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
        passives.removeIf(eff -> {
            if(!eff.endsWithin(0)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag effectsNbt = new CompoundTag();
        effectsNbt.putInt("size", passives.size());
        int i = 0;
        for(BeyonderEffect eff: passives){
            CompoundTag iterator = new CompoundTag();
            eff.toNbt(iterator);
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
