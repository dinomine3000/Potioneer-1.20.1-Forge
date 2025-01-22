package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAbilityCooldownSTC;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;

public class PlayerAbilitiesManager {
    private ArrayList<Ability> pathwayActives = new ArrayList<Ability>();
    private ArrayList<Integer> activeCooldowns = new ArrayList<>();
    public ArrayList<Boolean> enabledDisabled = new ArrayList<>();

    private ArrayList<Ability> pathwayPassives = new ArrayList<Ability>();
    private ArrayList<Ability> otherPassives = new ArrayList<Ability>();

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        if(!pathwayPassives.isEmpty()){
            pathwayPassives.forEach(ability -> {
                ability.passive(cap, target);
            });
        }
        if(!otherPassives.isEmpty()){
            otherPassives.forEach(ability -> {
                ability.passive(cap, target);
            });
        }
        //TODO check if this should be removed, since the CD could be counted only in client side
        for(int i = 0; i < activeCooldowns.size(); i++){
            if(activeCooldowns.get(i) > 0) activeCooldowns.set(i, activeCooldowns.get(i)-1);
        }
    }

    public void addPassiveAbility(Ability ability, boolean pathway){
        if(pathway) pathwayPassives.add(ability);
        else otherPassives.add(ability);
    }

    public void clear(boolean pathway, EntityBeyonderManager cap, LivingEntity target){
        if(pathway) {
            pathwayPassives.forEach(ability -> ability.deactivate(cap, target));
            pathwayPassives = new ArrayList<>();
            pathwayActives.forEach(ability -> ability.deactivate(cap, target));
            pathwayActives = new ArrayList<>();
        }
        else {
            otherPassives.forEach(ability -> ability.deactivate(cap, target));
            otherPassives = new ArrayList<>();
        }
    }

    public void useAbility(EntityBeyonderManager cap, LivingEntity tar, int caret){
        if(pathwayActives.isEmpty()){
            System.out.println("active abilities are non existent");
            return;
        }
        if(tar instanceof Player player){
            if(tar.level().isClientSide()){
                PacketHandler.INSTANCE.sendToServer(new PlayerCastAbilityMessageCTS(caret));
            } else {
                System.out.println(cap.getEffectsManager());
                System.out.println(pathwayActives);
                System.out.println(pathwayActives.get(caret));
                if(pathwayActives.get(caret).active(cap, tar)){
                    putOnCooldown(player, caret);
                }
            }

        }
    }

    public void setEnabledList(ArrayList<Boolean> list){
        this.enabledDisabled = list;
    }

    public void setEnabled(Ability abl, boolean bol){
        enabledDisabled.set(pathwayActives.indexOf(abl), bol);
    }

    public boolean isEnabled(Ability abl){
        if(enabledDisabled.isEmpty()) return false;
        return enabledDisabled.get(pathwayActives.indexOf(abl) % enabledDisabled.size());
    }

    public void putOnCooldown(Player player, int caret){
        putOnCooldown(player, caret, pathwayActives.get(caret).getCooldown());
    }

    public void putOnCooldown(Player player, int caret, int cd){
        activeCooldowns.set(caret, cd);
        if(cd != 0){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityCooldownSTC(caret, cd));
        }
    }

    public void setPathwayActives(ArrayList<Ability> abilities){
        pathwayActives = abilities;
        activeCooldowns = new ArrayList<>(abilities.stream().map(c -> 0).toList());
        enabledDisabled = new ArrayList<>(abilities.stream().map(c -> true).toList());
    }

    public ArrayList<Ability> getPathwayActives() {
        return pathwayActives;
    }

    public void setPathwayPassives(ArrayList<Ability> abilities){
        pathwayPassives = abilities;
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag enabled = new CompoundTag();
        enabled.putInt("size", enabledDisabled.size());
        for(int i = 0; i < enabledDisabled.size(); i++){
            enabled.putBoolean(String.valueOf(i), enabledDisabled.get(i));
        }
        nbt.put("enabled_abilities", enabled);
    }

    public void loadNBTData(CompoundTag nbt){
        CompoundTag enabledAbilities = nbt.getCompound("enabled_abilities");
        int size = enabledAbilities.getInt("size");
        ArrayList<Boolean> enabled = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                enabled.add(enabledAbilities.getBoolean(String.valueOf(i)));
            }
            this.enabledDisabled = enabled;
        }
    }

}
