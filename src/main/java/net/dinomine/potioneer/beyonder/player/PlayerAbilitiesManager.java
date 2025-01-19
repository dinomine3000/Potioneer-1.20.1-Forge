package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerCastAbilityMessageCTS;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;

public class PlayerAbilitiesManager {
    private ArrayList<Ability> pathwayActives = new ArrayList<Ability>();

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
        if(tar.level().isClientSide()){
            PacketHandler.INSTANCE.sendToServer(new PlayerCastAbilityMessageCTS(caret));
        } else {
            System.out.println(cap.getEffectsManager());
            System.out.println(pathwayActives);
            System.out.println(pathwayActives.get(caret));
            pathwayActives.get(caret).active(cap, tar);
        }
    }


    public void setPathwayActives(ArrayList<Ability> abilities){
        pathwayActives = abilities;
    }

    public ArrayList<Ability> getPathwayActives() {
        return pathwayActives;
    }

    public void setPathwayPassives(ArrayList<Ability> abilities){
        pathwayPassives = abilities;
    }

    public void saveNBTData(CompoundTag nbt){

    }

    public void loadNBTData(CompoundTag nbt){}

}
