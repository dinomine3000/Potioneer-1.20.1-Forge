package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.abilityRelevant.BeyonderEffectSyncMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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
    private PriorityQueue<BeyonderEffect> passives = new PriorityQueue<>(Comparator.comparingInt(BeyonderEffect::getPriority));
    public BeyonderStats statsHolder;

    //called from victim perspective
    public void onAttackProposal(LivingAttackEvent event, BeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<BeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(CapProvider.BEYONDER_STATS).resolve();
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
    public void onAttackDamageCalculation(LivingHurtEvent event, BeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<BeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(CapProvider.BEYONDER_STATS).resolve();
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

    public void onTakeDamage(LivingDamageEvent event, BeyonderCapability cap){
        //1: get attacker
        LivingEntity attacker;
        if(event.getSource().getEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getEntity();
        } else if (event.getSource().getDirectEntity() instanceof LivingEntity){
            attacker = (LivingEntity) event.getSource().getDirectEntity();
        } else attacker = null;

        //2: get attacker cap
        LivingEntity victim = event.getEntity();
        Optional<BeyonderCapability> optAttackerCap = Optional.empty();
        if(attacker != null){
            optAttackerCap = attacker.getCapability(CapProvider.BEYONDER_STATS).resolve();
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


    public void onCraft(PlayerEvent.ItemCraftedEvent event, BeyonderCapability cap){
    }

    public boolean onPlayerDie(LivingDeathEvent event, LivingEntity entity, BeyonderCapability cap) {
        for(BeyonderEffect eff: passives){
            if(eff.onDie(event, cap, entity)){
                event.setCanceled(true);
                return true;
            }
        }
        for(BeyonderEffect eff: new ArrayList<>(passives)){
            if(!eff.shouldPersistInDeath()){
                removeEffectImmediately(eff, cap, entity);
            }
        }
        return false;
    }

    @Override
    public String toString(){
        String res = "";
        for(BeyonderEffect eff : passives){
            res = res.concat(eff.getId().concat(eff.getSequenceLevel() + " - Max Life: ").concat(String.valueOf(eff.getMaxLife())).concat("\n"));
        }
        return res;
    }

    public PlayerEffectsManager(){
        statsHolder = new BeyonderStats();
    }

    public void clearEffects(BeyonderCapability cap, LivingEntity target){
        sendUpdateToClient(passives.stream().toList(), BeyonderEffectSyncMessage.REMOVE, target);
        for(BeyonderEffect eff : passives){
            eff.stopEffects(cap, target);
        }
        this.passives.clear();
    }

    public boolean hasEffectOrBetter(BeyonderEffect testEffect){
        return passives.stream().anyMatch(iEffect -> iEffect.isSameOrBetter(testEffect));
    }

    public boolean hasBetterEffect(BeyonderEffect testEffect){
        return passives.stream().anyMatch(iEffect -> iEffect.isBetter(testEffect));
    }

    /**
     * this method will make sure that the effect you add is added as a single effect,
     * that is, if there is something worse or nothing, itll remove it and then add the incoming effect.
     * and otherwise, if there is something of equal level, itll call the refresh method
     * @param effect
     * @param cap
     * @param target
     * @return
     */
    public boolean addOrRefreshEffect(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        if(effect == null) return false;
        if(!effect.canAdd(cap, target)) return false;
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId());
            return addEffect(effect, cap, target, true);
        } else if(hasEffect(effect.getId(), effect.getSequenceLevel())){
            BeyonderEffect oldEffect = getEffect(effect.getId(), effect.getSequenceLevel());
            oldEffect.refreshTime(cap, target, effect);
            return true;
        }
        return false;
    }

    /**
     * instantly removes the effect if it already exists. dont call it if it originates from an effect.
     * @param effect
     * @param cap
     * @param target
     * @return
     */
    public boolean addOrReplaceEffect(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return false;
        if(!effect.canAdd(cap, target)) return false;
        if(!hasBetterEffect(effect)){
            removeEffectImmediately(effect.getId(), cap, target);
            return addEffect(effect, cap, target, true);
        }
        return false;
    }

    public boolean addEffectNoRefresh(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        if(!effect.canAdd(cap, target)) return false;
        if(!hasEffectOrBetter(effect)){
            removeEffect(effect.getId());
            return addEffect(effect, cap, target, true);
        }
        return false;
    }

    public boolean addEffectNoCheck(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return false;
        return addEffect(effect, cap, target, true);
    }

    private boolean addEffect(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target, boolean sync, boolean fromLoading){
        if(!effect.canAdd(cap, target)) return false;
        passives.add(effect);
        effect.onAcquire(cap, target, fromLoading);
        if(sync) sendUpdateToClient(List.of(effect), BeyonderEffectSyncMessage.ADD, target);
        return true;
    }

    private boolean addEffect(BeyonderEffect effect, BeyonderCapability cap, LivingEntity target, boolean sync){
        return addEffect(effect, cap, target, sync, false);
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

    public boolean instantRemoveEffect(BeyonderCapability cap, LivingEntity target, String effect){
        return passives.removeIf(eff -> {
            if(!eff.is(effect)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public boolean instantRemoveEffect(BeyonderCapability cap, LivingEntity target, String effect, int sequenceLevel){
        return passives.removeIf(eff -> {
            if(!eff.is(effect, sequenceLevel)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public boolean removeEffectImmediately(String effectId, BeyonderCapability cap, LivingEntity target){
        return passives.removeIf(eff -> {
            boolean flag = eff.is(effectId);
            if(flag){
                eff.stopEffects(cap, target);
            }
            return flag;
        });
    }

    public boolean removeEffectImmediately(BeyonderEffect eff, BeyonderCapability cap, LivingEntity target){
        for(BeyonderEffect effect: new ArrayList<>(passives)){
            if(effect == eff){
                effect.stopEffects(cap, target);
                passives.remove(eff);
                sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
                return true;
            }
        }
        return false;
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
            if (passive.isOrWorse(effect, seq)) {
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

    public void addEffectsOnClient(List<BeyonderEffect> effects, @NotNull BeyonderCapability cap, Player player) {
        for(BeyonderEffect eff: effects){
            if(eff == null) {
                System.out.println("Warning: Client received a null effect!");
                continue;
            }
            addEffect(eff, cap, player, false);
        }
    }

    public void removeEffectsOnClient(List<BeyonderEffect> effects, @NotNull BeyonderCapability cap, Player player) {
        for(BeyonderEffect eff: effects){
            removeEffect(eff.getId(), eff.getSequenceLevel());
        }
    }

    public void setEffectsOnClient(List<BeyonderEffect> effects, @NotNull BeyonderCapability cap, Player player) {
        clearEffects(cap, player);
        for(BeyonderEffect eff: effects){
            addEffect(eff, cap, player, false);
        }
    }

    public void updateEffectsOnClient(List<BeyonderEffect> effects, BeyonderCapability cap, LivingEntity target) {
        outer:
        for(BeyonderEffect incomingEffect: effects){
            for(BeyonderEffect existingEffect: passives){
                if(incomingEffect.is(existingEffect)) {
                    CompoundTag tag = new CompoundTag();
                    incomingEffect.toNbt(tag);
                    existingEffect.loadNBTData(tag);
                    existingEffect.onUpdateReceivedOnClient(cap, target);
                    continue outer;
                }
            }
        }
    }

    public void syncToClient(Player player) {
        sendUpdateToClient(new ArrayList<>(passives), BeyonderEffectSyncMessage.SET, player);
    }

    public void onTick(BeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()){
            if(!passives.isEmpty()){
                new ArrayList<>(passives).forEach(effect -> {
                    effect.effectTick(cap, target);
                });
            }
            sweepEffects(cap, target);
            statsHolder.resetStats();
            return;
        }
        statsHolder.resetStats();
        if(!passives.isEmpty()){
            new ArrayList<>(passives).forEach(effect -> {
                effect.effectTick(cap, target);
            });
        }
        sweepEffects(cap, target);
        cap.getBeyonderStats().setEffects(statsHolder, target);
        if(target instanceof Player player){
            cap.getBeyonderStats().applyEffects(player, statsHolder);
            cap.getBeyonderStats().addStatsAndApplyIfChanged(statsHolder, player);
        }
    }

    private void sweepEffects(BeyonderCapability cap, LivingEntity target){
        passives.removeIf(eff -> {
            if(!eff.endsWithin(0)) return false;
            eff.stopEffects(cap, target);
            sendUpdateToClient(List.of(eff), BeyonderEffectSyncMessage.REMOVE, target);
            return true;
        });
    }

    public void saveNBTData(CompoundTag nbt) {
        ListTag list = new ListTag();
        for (BeyonderEffect eff : passives) {
            CompoundTag effectTag = new CompoundTag();
            eff.toNbt(effectTag);
            list.add(effectTag);
        }
        nbt.put("effectData", list);
    }

    public void loadNBTData(CompoundTag nbt, BeyonderCapability cap, LivingEntity entity) {
        ListTag list = nbt.getList("effectData", ListTag.TAG_COMPOUND);

        for (int i = 0; i < list.size(); i++) {
            CompoundTag iterator = list.getCompound(i);
            BeyonderEffect eff = BeyonderEffect.readEffectFromNBTTag(iterator);
            if(eff == null) continue;
            addEffect(eff, cap, entity, false, true);
        }
    }

    /**
     * TODO: make passives that actually want to persist in death, like shepherd graze
     * @param otherEffects
     */
    public void copyFrom(PlayerEffectsManager otherEffects) {
        this.passives = otherEffects.passives;
    }

    public void clearCloneWeakEffects(BeyonderCapability cap, LivingEntity target) {
        for(BeyonderEffect eff: new ArrayList<>(passives)){
            if(eff.bypassesClones()) continue;
            removeEffectImmediately(eff, cap, target);
        }
    }
}
