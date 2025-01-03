package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.pathways.powers.Ability;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;

public class PlayerAbilitiesManager {
    private ArrayList<Ability> pathwayPassives = new ArrayList<Ability>();
    private ArrayList<Ability> otherPassives = new ArrayList<Ability>();

    public void onTick(EntityBeyonderManager cap, LivingEntity target){
        pathwayPassives.forEach(ability -> {
            ability.passive(cap, target);
        });
        otherPassives.forEach(ability -> {
            ability.passive(cap, target);
        });
    }

    public void addPassiveAbility(Ability ability, boolean pathway){
        if(pathway) pathwayPassives.add(ability);
        else otherPassives.add(ability);
    }

    public void clear(boolean pathway, EntityBeyonderManager cap, LivingEntity target){
        if(pathway) {
            pathwayPassives.forEach(ability -> ability.deactivate(cap, target));
        }
        else {
            otherPassives.forEach(ability -> ability.deactivate(cap, target));
            otherPassives.clear();
        }
    }

    public void setPathwayPassives(ArrayList<Ability> abilities){
        pathwayPassives = abilities;
    }

    public void saveNBTData(CompoundTag nbt){

    }

    public void loadNBTData(CompoundTag nbt){}
}
