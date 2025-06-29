package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.*;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class TyrantPathway extends Beyonder {

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 1200, 800, 750, 250, 100};

    }

    public static int getX(){
        return 64;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{8, 1, 0, 0, 4};
            case 8 -> new float[]{10, 1, 0, 0, 4};
            case 7 -> new float[]{12, 2, 0, 0, 5};
            case 6 -> new float[]{15, 2, 0, 0, 5};
            case 5 -> new float[]{20, 3, 0, 0, 10};
            default -> new float[]{0, 0, 0, 0, 0};
        };
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        ArrayList<Ability> passiveAbilities = new ArrayList<>();
        ArrayList<Ability> activeAbilities = new ArrayList<>();
        switch(sequence){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
                activeAbilities.add(new WaterPrisonAbility(sequence));
                activeAbilities.add(new WaterCreateAbility(sequence));
                activeAbilities.add(new WaterRemoveAbility(sequence));
                activeAbilities.add(new WaterTrapAbility(sequence));
                activeAbilities.add(new DivinationAbility(sequence));
            case 9:
                WaterAffinityAbility water = new WaterAffinityAbility(sequence);
                activeAbilities.add(water);

                passiveAbilities.add(water);
        }

        mng.setPathwayActives(activeAbilities);
        mng.setPathwayPassives(passiveAbilities);
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
            case 7 -> "Waterborn";
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
