package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.PlayerBeyonderStats;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.function.Consumer;

public class Beyonder {
    protected int sequence;
    protected String name;
    protected int color;

    public Beyonder(int sequence){
        this(sequence, "Beyonderless");
    }

    public Beyonder(int sequence, String path){
        this.sequence = sequence;
        this.name = path;
        this.color = 0x404040;
    }

    public static void init(){
        TyrantPathway.init();
        WheelOfFortunePathway.init();
    }


    public int getId(){
        return -1;
    }

    public int getColor(){
        return this.color;
    }

    public int getSequence(){
        return this.sequence;
    }

    public String getSequenceName(boolean show){
        return getSequenceName(getSequence(), show);
    }

    /**
     * returns the id string of the sequence. it should be written like "Duke_of_Entropy", with capitalized words and underscores for spaces
     * this way the names can be procedurally gotten
     * @param seq
     * @return
     */
    public String getSequenceName(int seq){
        return "None";
    }

    public String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(this.sequence).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }
    public String getPathwayName(){
        return this.name;
    }

}
