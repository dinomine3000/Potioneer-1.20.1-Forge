package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.DurabilityRegenAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.StatBonusAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.effects.redpriest.BeyonderWeaponProficiencyEffect;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;

public class RedPriestPathway extends Beyonder {

    public RedPriestPathway(int sequence){
        super(sequence, "Red_Priest");
        this.color = 0x804040;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 1000, 1000, 1000, 1000, 500, 100};
    }

    public static int getX(){
        return 192;
    }

    public static int getY(){
        return 0;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{5, 1, 0, 0, 1};
            case 8 -> new float[]{8, 2, 0, 0, 2};
            case 7 -> new float[]{12, 3, 5, 0, 4};
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
            case 9:
                WeaponProficiencyAbility weapon = new WeaponProficiencyAbility(sequence);
                activeAbilities.add(weapon);
                passiveAbilities.add(weapon);
        }

        mng.setPathwayActives(activeAbilities);
        mng.setPathwayPassives(passiveAbilities);
    }

    @Override
    public int getId() {
        return 30 + this.sequence;
    }


    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Warrior";
            case 8 -> "Pyromaniac";
            case 7 -> "Priest_of_Light";
            case 6 -> "Sun-Blessed";
            case 5 -> "Hunter";
            default -> "";
        };
    }

}
