package net.dinomine.potioneer.beyonder.player;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;

import java.util.ArrayList;

public class PlayerAbilityInfoManager {
    public ArrayList<AbilityInfo> infos = new ArrayList<>();

    public void updateInfosOnAdvancement(ArrayList<Ability> abilities){
        ArrayList<Ability> temp = new ArrayList<>(abilities);
        for(AbilityInfo i : infos){
            for(int j = temp.size()-1; j >= 0; j--){
                if(temp.get(j).getInfo().name().equals(i.name())) temp.remove(j);
            }
        }
        if(temp.isEmpty()) return;
        else{
            temp.forEach(abl -> infos.add(abl.getInfo()));
        }
    }

}
