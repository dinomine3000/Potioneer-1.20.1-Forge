package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.DummyAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.AnvilGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.*;
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
        this.maxSpirituality = new int[]{0, 0, 0, 0, 0, 0, 0, 1000, 500, 100};
    }

    public static int getX(){
        return 0;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 4, 0, 2};
            case 7 -> new float[]{5, 0, 8, 2, 2};
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
                GamblingAbility gambl = new GamblingAbility(sequence);
                LuckDamageReductionAbility luckDmgRed = new LuckDamageReductionAbility(sequence);
                CalamityIncreaseAbility calamity = new CalamityIncreaseAbility(sequence);

                activeAbilities.add(new WheelBoneMealAbility(sequence));
                activeAbilities.add(gambl);
                activeAbilities.add(luckDmgRed);
                activeAbilities.add(calamity);

                passiveAbilities.add(gambl);
                passiveAbilities.add(luckDmgRed);
                //passiveAbilities.add(calamity);
            case 8:
                FortuneAbility fortune = new FortuneAbility(sequence);
                SilkTouchAbility silk = new SilkTouchAbility(sequence);
                activeAbilities.add(new BlockSniffAbility(sequence));
                activeAbilities.add(new LuckBoostAbility(sequence));
                activeAbilities.add(new CheckLuckAbility(sequence));
                activeAbilities.add(fortune);
                activeAbilities.add(silk);

                passiveAbilities.add(fortune);
                passiveAbilities.add(silk);
            case 9:
                MiningSpeedAbility mining = new MiningSpeedAbility(sequence);
                activeAbilities.add(new MinerLightAbility(sequence));
                activeAbilities.add(new ConjurePickaxeAbility(sequence));
                activeAbilities.add(mining);
                passiveAbilities.add(mining);
        }

        mng.setPathwayActives(activeAbilities);
        mng.setPathwayPassives(passiveAbilities);
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Miner";
            case 8 -> "Appraiser";
            case 7 -> "Gambler";
            case 6 -> "Lucky_One";
            case 5 -> "Source_of_Misfortune";
            default -> "";
        };
    }

    public static int getSequenceColor(int seq){
        return switch (seq) {
            case 9 -> 10724259;
            case 8 -> 16383885;
            case 7 -> 14989311;
            default -> 0;
        };
    }

    @Override
    public int getId() {
        return sequence;
    }

}
