package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.pathways.powers.Ability;
import net.dinomine.potioneer.beyonder.pathways.powers.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class TyrantPathway extends Beyonder{

    private static ArrayList<Ability> passiveAbilities9;

    public TyrantPathway(int sequence){
        super(sequence, "Tyrant");
        this.color = 0x404080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 250, 100};

    }

    public static void init(){
        passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(new WaterAffinityAbility(9, true));
    }

    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
        return passiveAbilities9;
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
