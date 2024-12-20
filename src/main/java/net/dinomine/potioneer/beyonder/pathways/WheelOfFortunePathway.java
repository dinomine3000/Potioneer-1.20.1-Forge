package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.function.Consumer;

public class WheelOfFortunePathway extends Beyonder{

    public static ArrayList<Consumer<Player>> passiveAbilities = new ArrayList<>();

    public WheelOfFortunePathway(int sequence) {
        super(sequence, "Wheel_of_Fortune");
        this.color = 0x808080;
    }

    public static void init(){
        passiveAbilities = new ArrayList<>();
        passiveAbilities.add(WheelOfFortunePathway::giveNightVision);
        passiveAbilities.add(WheelOfFortunePathway::miningSpeedIncrease);
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
        return 10 + sequence;
    }

    public static ArrayList<Consumer<Player>> getPassiveAbilities(int sequence) {
        return passiveAbilities;
    }

    public static void miningSpeedIncrease(Player player){
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            cap.multMiningSpeed(2f+0.7f*(9-cap.getSequenceLevel()));
        });
    }

    public static void giveNightVision(Player player){
        if(!player.level().isClientSide()){
            if(!player.hasEffect(MobEffects.NIGHT_VISION)){
                player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, -1, 0, false, false));
            }
        }
    }

}
