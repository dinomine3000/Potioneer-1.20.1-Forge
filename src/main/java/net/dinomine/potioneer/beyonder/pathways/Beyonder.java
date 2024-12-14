package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.world.entity.player.Player;

public class Beyonder {
    protected int sequence;

    public Beyonder(int sequence){
        this.sequence = sequence;
    }

    public int getId(){
        return -1;
    }

    public float getMiningSpeedMult(){
        return 1;
    }

    public int getSequence(){
        return this.sequence;
    }

    public String getName(int seq){
        return "";
    }


    public void onTick(Player player){

    }

    public void attackTarget(){

    }

    public void takeDamage(){

    }

    public void useAbility(int slot){

    }
}
