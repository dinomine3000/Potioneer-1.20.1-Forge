package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ForgeItemTagsProvider;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;

public class PlayerEffectsManager {
    private ArrayList<BeyonderEffect> passives = new ArrayList<BeyonderEffect>();
    public BeyonderStats statsHolder;

    public void onAttack(LivingDamageEvent event){
        Entity attacker = event.getSource().getEntity();
        //TODO change this to account for multiple instances of similar effects
        if(attacker instanceof Player player && hasEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY)){
            BeyonderEffect eff = getEffect(BeyonderEffects.EFFECT.RED_WEAPON_PROFICIENCY);
            InteractionHand hand = player.getUsedItemHand();
            if(player.getItemInHand(hand).is(Tags.Items.TOOLS)){
                System.out.println(event.getAmount());
                System.out.println(eff.getSequenceLevel());
                float dmg = event.getAmount()*((10-eff.getSequenceLevel()) * 0.4f + 1f);
                System.out.println(dmg);
                event.setAmount(dmg);
            }
        }
    }


    public void onCraft(PlayerEvent.ItemCraftedEvent event){
        //TODO change this to account for multiple instances of similar effects
        if(hasEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS)){
            BeyonderEffect eff = getEffect(BeyonderEffects.EFFECT.PARAGON_CRAFTING_BONUS);
            int og = event.getCrafting().getCount();
            ItemStack stack = event.getCrafting().copy();
            stack.setCount((int)(Math.round(og*((10-eff.getSequenceLevel())*0.4))));
            event.getEntity().addItem(stack);
        }
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

    public void clearEffects(EntityBeyonderManager cap, Player player){
        for(BeyonderEffect eff : passives){
            eff.stopEffects(cap, player);
        }
        this.passives = new ArrayList<>();
    }

    public boolean hasEffectOrBetter(BeyonderEffect effect){
        return passives.stream().anyMatch(effect::isBetter);
    }

    public boolean addEffect(BeyonderEffect effect){
        passives.add(effect);
        return true;
    }

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

    public boolean hasEffect(BeyonderEffects.EFFECT effect, int seq){
        return passives.stream().anyMatch(eff -> eff.is(effect, seq));
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

    public boolean removeEffect(BeyonderEffects.EFFECT effect, int seq, EntityBeyonderManager cap, LivingEntity target){
        for(int i = 0; i < passives.size(); i++){
            if(passives.get(i).is(effect, seq)) {
                passives.get(i).stopEffects(cap, target);
                passives.remove(i);
                return true;
            };
        }
        return false;
    }

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        statsHolder.resetStats();
        if(!passives.isEmpty()){
            passives.forEach(effect -> {
                effect.effectTick(cap, target);
            });
        }
        cap.getBeyonderStats().setStats(statsHolder);
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

    public void loadNBTData(CompoundTag nbt){
        CompoundTag effectsTag = nbt.getCompound("effectData");
        int size = effectsTag.getInt("size");
        for(int i = 0; i < size; i++){
            CompoundTag iterator = effectsTag.getCompound(String.valueOf(i));
            BeyonderEffect effect = BeyonderEffects.byId(
                    BeyonderEffects.EFFECT.valueOf(iterator.getString("ID")),
                    iterator.getInt("level"),
                    iterator.getInt("cost"),
                    iterator.getInt("maxLife"),
                    iterator.getBoolean("active"));
            effect.setDuration(iterator.getInt("lifetime"));
            addEffect(effect);
        }
    }
}
