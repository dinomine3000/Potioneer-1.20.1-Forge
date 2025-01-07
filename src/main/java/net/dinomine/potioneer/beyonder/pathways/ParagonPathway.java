package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingBonusAbility;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.CraftingMenu;

import java.util.ArrayList;

public class ParagonPathway extends Beyonder {

    private static ArrayList<Ability> passiveAbilities9;

    public ParagonPathway(int sequence){
        super(sequence, "Paragon");
        this.color = 0x908020;
    }

    public static void init(){
        passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(new CraftingBonusAbility(9, true));
    }

    public static ArrayList<Ability> getPassiveAbilities(int sequence) {
        return passiveAbilities9;
    }

    @Override
    public int getId() {
        return 40 + this.sequence;
    }

    @Override
    public String getSequenceName(int seq){
        return switch (seq) {
            case 9 -> "Crafter";
            case 8 -> "Enchanter";
            case 7 -> "Conjurer";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            case 4 -> "Mechanical_Creationist";
            case 3 -> "Knowledge_Magister";
            case 2 -> "";
            case 1 -> "";
            case 0 -> "Paragon";
            default -> "";
        };
    }

}
