package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingBonusAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.DurabilityRegenAbility;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;

import java.util.ArrayList;

public class ParagonPathway extends Beyonder {

    public ParagonPathway(int sequence){
        super(sequence, "Paragon");
        this.color = 0x908020;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 1000, 1000, 1000, 1000, 500, 100};
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 0, 0, 2};
            case 8 -> new float[]{0, 0, 0, 0, 3};
            case 7 -> new float[]{0, 0, 0, 0, 4};
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
                activeAbilities.add(new CraftingGuiAbility(sequence));
                activeAbilities.add(new DurabilityRegenAbility(sequence));
        }
//        CraftingBonusAbility abl = new CraftingBonusAbility(sequence);
        mng.setPathwayActives(activeAbilities);
        mng.setPathwayPassives(passiveAbilities);
    }

    @Override
    public int getId() {
        return 40 + this.sequence;
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Crafter";
            case 8 -> "Enchanter";
            case 7 -> "Conjurer";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            default -> "";
        };
    }

}
