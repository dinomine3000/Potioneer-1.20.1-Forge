package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class TyrantPathway extends Beyonder {

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 250, 100};

    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        WaterAffinityAbility abl = new WaterAffinityAbility(sequence);

        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(abl);
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(abl);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
    }

//    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
//        passiveAbilities9 = new ArrayList<>();
//        passiveAbilities9.add(new WaterAffinityAbility(sequence, true));
//        return passiveAbilities9;
//    }
//
//    public static ArrayList<Ability> getActiveAbilities(int sequence) {
//        activeAbilities9 = new ArrayList<>();
//        activeAbilities9.add(new DummyAbility(sequence));
//        return activeAbilities9;
//    }

    @Override
    public int getId() {
        return 10 + this.sequence;
    }


    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Swimmer";
            case 8 -> "Hydro_Shaman";
            case 7 -> "Water-Born";
            case 6 -> "Tempest";
            case 5 -> "Punisher";
            default -> "";
        };
    }

    public static void miningSpeedIncrease(Player player, EntityBeyonderManager cap) {
        float f = 1;
        //doesnt cost spirituality
    }


    public static void giveWaterEffects(Player player, EntityBeyonderManager cap){
        //On average, this calls the cost once a second
        float cost = 4f;
    }

    private static void replenishStatsWhileUnderwater(Player player, EntityBeyonderManager cap){
        if(isInWater(player)){
            //cap.requestSpiritualityCost(-4);
            if(player.getFoodData().needsFood() && Math.random() < 0.02){
                cap.requestPassiveSpiritualityCost(20);
                player.getFoodData().eat(1, 0);
            }
        }
    }

    public static boolean isInWater(Player player){
        return player.isInWater();
    }

}
