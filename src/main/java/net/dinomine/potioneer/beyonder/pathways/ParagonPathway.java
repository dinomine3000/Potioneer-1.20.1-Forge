package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class ParagonPathway extends BeyonderPathway {

    public static int[] paragonSpirituality = new int[]{3500, 2744, 1960, 1400, 1000, 700, 500, 350, 250, 100};

    public ParagonPathway(int sequence){
        super(sequence, "Paragon");
        this.color = 0x908020;
        this.maxSpirituality = paragonSpirituality;
    }

    public static int getX(){
        return 0;
    }

    public static int getY(){
        return 64;
    }

    public static float[] getStatsFor(int sequence){
        return switch (sequence){
            case 9 -> new float[]{0, 0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 0, 0, 1};
            case 7 -> new float[]{1, 0, 0, 1, 5};
            case 6 -> new float[]{2, 0, 0, 2, 5};
            case 5 -> new float[]{2, 1, 0, 2, 5};
            default -> new float[]{4, 2, 2, 0, 10};
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
                XpCostReductionAbility xp = new XpCostReductionAbility(sequence);
                activeAbilities.add(xp);
                passiveAbilities.add(xp);
                activeAbilities.add(new RemoveEnchantmentAbility(sequence));
            case 8:
                activeAbilities.add(new AnvilGuiAbility(sequence));
                activeAbilities.add(new ParagonBoneMealAbility(sequence));
                activeAbilities.add(new ConjurerContainerAbility(sequence));
                activeAbilities.add(new EnderChestAbility(sequence));
            case 9:
                CraftingSpiritualityAbility craftingSpiritualityAbility = new CraftingSpiritualityAbility(sequence);
                activeAbilities.add(craftingSpiritualityAbility);
                passiveAbilities.add(craftingSpiritualityAbility);
                activeAbilities.add(new CraftingGuiAbility(sequence));
                activeAbilities.add(new FuelAbility(sequence));
                activeAbilities.add(new DurabilityRegenAbility(sequence));
                activeAbilities.add(new CogitationAbility(40 + sequence));
        }
//        CraftingBonusAbility abl = new CraftingBonusAbility(sequence);
        mng.setPathwayActives(activeAbilities);
        //mng.setPathwayPassives(passiveAbilities);
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
            case 8 -> "Conjurer";
            case 7 -> "Enchanter";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            default -> "";
        };
    }

    public static int getSequenceColor(int seq){
        return switch (seq) {
            case 9 -> 16770989;
            case 8 -> 28791;
            case 7 -> 10107903;
            default -> 0;
        };
    }

}
