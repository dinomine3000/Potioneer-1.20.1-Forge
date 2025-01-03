package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.pathways.powers.Ability;
import net.dinomine.potioneer.beyonder.pathways.powers.wheeloffortune.MiningSpeedAbility;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.function.BiConsumer;

public class WheelOfFortunePathway extends Beyonder{

    public static ArrayList<Ability> passiveAbilities9;

    public WheelOfFortunePathway(int sequence) {
        super(sequence, "Wheel_of_Fortune");
        this.color = 0x808080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 500, 100};
    }

    public static void init(){
        passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(new MiningSpeedAbility(9, true));
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Gambling_Man";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            case 4 -> "Commander_of_Fate";
            case 3 -> "Abuser";
            case 0 -> "Wheel_of_Fortune";
            default -> "";
        };
    }

    @Override
    public int getId() {
        return sequence;
    }

    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
        return passiveAbilities9;
    }

    public static void giveNightVision(Player player, EntityBeyonderManager cap){
        int cost = 1;
        if(!player.level().isClientSide()){
            if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 230, 0, false, false));
                cap.requestSpiritualityCost(cost);
            } else if(player.getEffect(MobEffects.NIGHT_VISION).endsWithin(205)){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 230, 0, false, false));
                cap.requestSpiritualityCost(cost);
            }
        }
    }

}
