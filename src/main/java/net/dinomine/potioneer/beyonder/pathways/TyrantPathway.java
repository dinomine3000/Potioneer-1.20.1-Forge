package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.DummyAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class TyrantPathway extends Beyonder {

    private static ArrayList<Ability> passiveAbilities9;
    private static ArrayList<Ability> activeAbilities9;

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 250, 100};

    }

    public static void init(){

    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        WaterAffinityAbility abl = new WaterAffinityAbility(sequence, true);
        passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(abl);
        activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(abl);
        ArrayList<Integer> hotbar = new ArrayList<>();
        hotbar.add(0);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
    }

    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
        passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(new WaterAffinityAbility(sequence, true));
        return passiveAbilities9;
    }

    public static ArrayList<Ability> getActiveAbilities(int sequence) {
        activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(new DummyAbility(sequence, true));
        return activeAbilities9;
    }

    @Override
    public int getId() {
        return 10 + this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Swimmer";
            case 8 -> "Water-Blessed";
            case 7 -> "Hydro_Shaman";
            case 6 -> "Tempest";
            case 5 -> "Punisher";
            case 4 -> "Ice_Duke";
            case 3 -> "Prosperous_Prince";
            case 2 -> "Nature_Dictator";
            case 1 -> "Cataclysm_King";
            case 0 -> "Tyrant";
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
                cap.requestSpiritualityCost(20);
                player.getFoodData().eat(1, 0);
            }
        }
    }

    public static boolean isInWater(Player player){
        return player.isInWater();
    }

}
