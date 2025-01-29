package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
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

    public static void getAbilities(int sequence, PlayerAbilitiesManager mng){
//        CraftingBonusAbility abl = new CraftingBonusAbility(sequence);
        CraftingGuiAbility craft = new CraftingGuiAbility(sequence);
        DurabilityRegenAbility durability = new DurabilityRegenAbility(sequence);

        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
//        passiveAbilities9.add(abl);
        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
//        activeAbilities9.add(abl);
        activeAbilities9.add(craft);
        activeAbilities9.add(durability);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
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
