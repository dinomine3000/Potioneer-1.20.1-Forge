package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.DummyAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MiningSpeedAbility;
import net.dinomine.potioneer.beyonder.client.ClientAbilitiesData;
import net.dinomine.potioneer.beyonder.client.ClientStatsData;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;

public class WheelOfFortunePathway extends Beyonder {

    public WheelOfFortunePathway(int sequence) {
        super(sequence, "Wheel_of_Fortune");
        this.color = 0x808080;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 500, 100};
    }

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
        MiningSpeedAbility abl = new MiningSpeedAbility(sequence);
        DummyAbility dummy = new DummyAbility(sequence);
        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(abl);
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(dummy);
        activeAbilities9.add(abl);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);

    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
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
