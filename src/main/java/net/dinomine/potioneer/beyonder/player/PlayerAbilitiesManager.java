package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.pathways.RedPriestPathway;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAbilityCooldownSTC;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerCastAbilityMessageCTS;
import net.dinomine.potioneer.network.messages.PlayerSyncHotbarMessage;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
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

    public ArrayList<Integer> clientHotbar = new ArrayList<>();

    public void copyFrom(PlayerAbilitiesManager mng){
        this.enabledDisabled = new ArrayList<>(mng.enabledDisabled);
        this.clientHotbar = mng.clientHotbar;
    }

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
                pathwayActives.get(caret).active(cap, tar);
            } else {
//                System.out.println(cap.getEffectsManager());
//                System.out.println(pathwayActives);
//                System.out.println(pathwayActives.get(caret));
                if(pathwayActives.get(caret).active(cap, tar)){
                    putOnCooldown(player, caret);
                }
            }

        }
    }

    public void setEnabledList(ArrayList<Boolean> list){
        if(this.enabledDisabled.size() > list.size()){
            for (int i = 0; i < list.size(); i++) {
                this.enabledDisabled.set(i, list.get(i));
            }
        } else {
            this.enabledDisabled = list;
        }
        if(enabledDisabled.size() != pathwayActives.size()){
            System.out.println("WARNING: EnabledDisabled list is NOT the same size as the pathwayActives list.\nProceed with caution!!!");
//            System.out.println("List size: " + list.size());
//            System.out.println("EnabledDisabled list size: " + enabledDisabled.size());
//            System.out.println("pathwayActives list size: " + pathwayActives.size());
        }
        for (int i = 0; i < Math.min(enabledDisabled.size(), pathwayActives.size()); i++) {
            if(pathwayActives.get(i).isActive) enabledDisabled.set(i, true);
        }
    }

    public void setEnabled(Ability abl, boolean bol, EntityBeyonderManager cap, LivingEntity target){
        if(!pathwayActives.contains(abl)) return;
        if(enabledDisabled.get(pathwayActives.indexOf(abl)) && !bol){
            abl.deactivate(cap, target);
        } else if(!enabledDisabled.get(pathwayActives.indexOf(abl)) && bol){
            abl.activate(cap, target);
        }
        enabledDisabled.set(pathwayActives.indexOf(abl), bol);
    }

    public boolean isEnabled(Ability abl){
        if(enabledDisabled.isEmpty()) return false;
        return enabledDisabled.get(pathwayActives.indexOf(abl) % enabledDisabled.size());
    }

    public int getCaretForAbility(Ability abl){
        return pathwayActives.indexOf(abl);
    }

    public void putOnCooldown(Player player, int caret){
        putOnCooldown(player, caret, pathwayActives.get(caret).getCooldown());
    }

    public void putOnCooldown(Player player, int caret, int cd){
        putOnCooldown(player, caret, cd, pathwayActives.get(caret).getInfo().maxCooldown());
    }

    public void putOnCooldown(Player player, int caret, int cd, int maxCd){
        activeCooldowns.set(caret, cd);
        if(cd != 0){
            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) player),
                    new PlayerAbilityCooldownSTC(caret, cd, maxCd));
        }
    }

    public void setPathwayActives(ArrayList<Ability> abilities){
//        System.out.println("Active abilities updates. Size is: " + abilities.size());
        pathwayActives = new ArrayList<>(abilities);
        activeCooldowns = new ArrayList<>(abilities.stream().map(c -> 0).toList());
        enabledDisabled = new ArrayList<>(abilities.stream().map(c -> true).toList());
    }

    public void onAcquireAbilities(EntityBeyonderManager cap, LivingEntity target){
        //only going through actives. if you want for passives, youll need to add it
        for (int i = 0; i < pathwayActives.size(); i++) {
            if(enabledDisabled.get(i)) pathwayActives.get(i).onAcquire(cap, target);
        }
    }

    public ArrayList<Ability> getPathwayActives() {
        return pathwayActives;
    }

    public void setPathwayPassives(ArrayList<Ability> abilities){
//        System.out.println("Passive abilities updates. Size is: " + abilities.size());
        pathwayPassives = abilities;
    }

    public void saveNBTData(CompoundTag nbt){
        CompoundTag enabled = new CompoundTag();
        enabled.putInt("size", enabledDisabled.size());
        for(int i = 0; i < enabledDisabled.size(); i++){
            enabled.putBoolean(String.valueOf(i), enabledDisabled.get(i));
        }
        nbt.put("enabled_abilities", enabled);

        CompoundTag cooldowns = new CompoundTag();
        cooldowns.putInt("size", activeCooldowns.size());
        for(int i = 0; i < activeCooldowns.size(); i++){
            cooldowns.putInt(String.valueOf(i), activeCooldowns.get(i));
        }
        nbt.put("cooldowns", cooldowns);

        CompoundTag hotbar = new CompoundTag();
        hotbar.putInt("size", clientHotbar.size());
        for(int i = 0; i < clientHotbar.size(); i++){
            hotbar.putInt(String.valueOf(i), clientHotbar.get(i));
        }
        nbt.put("hotbar", hotbar);
    }

    public void loadEnabledListFromTag(CompoundTag nbt){
        CompoundTag enabledAbilities = nbt.getCompound("enabled_abilities");
        int size = enabledAbilities.getInt("size");
        ArrayList<Boolean> enabled = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                enabled.add(enabledAbilities.getBoolean(String.valueOf(i)));
            }
            //syncing with the abilities you had from pathway
            //this is important if pathway abilities change between world loads,
            //so itll at least try to keep the info on what abilities were on or off
//            System.out.println(enabled.size());
            setEnabledList(enabled);
        }
    }

    public void loadNBTData(CompoundTag nbt, LivingEntity target){
        CompoundTag enabledAbilities = nbt.getCompound("enabled_abilities");
        int size = enabledAbilities.getInt("size");
        ArrayList<Boolean> enabled = new ArrayList<>();
        if(size != 0){
            for(int i = 0; i < size; i++){
                enabled.add(true);
            }
            //syncing with the abilities you had from pathway
            //this is important if pathway abilities change between world loads,
            //so itll at least try to keep the info on what abilities were on or off
//            System.out.println(enabled.size());
            setEnabledList(enabled);
        }

        CompoundTag cds = nbt.getCompound("cooldowns");
        int sizeCd = cds.getInt("size");
        ArrayList<Integer> cooldowns = new ArrayList<>();
        if(sizeCd != 0){
            for (int i = 0; i < sizeCd; i++) {
                cooldowns.add(cds.getInt(String.valueOf(i)));
            }
        }

        CompoundTag hot = nbt.getCompound("hotbar");
        int sizeHot = hot.getInt("size");
        if(sizeHot != 0){
            for(int i = 0; i < sizeHot; i++){
                clientHotbar.add(hot.getInt(String.valueOf(i)));
            }
        }
    }

}
