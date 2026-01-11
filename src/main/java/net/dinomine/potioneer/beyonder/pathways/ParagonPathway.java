package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.misc.CogitationAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.*;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;
import java.util.List;

public class ParagonPathway extends BeyonderPathway {

    public ParagonPathway(){
        super("Paragon", 0x908020, new int[]{3500, 2744, 1960, 1400, 1000, 700, 500, 350, 250, 100});
    }

    @Override
    public int getX(){
        return 0;
    }

    @Override
    public int getY(){
        return 64;
    }

    @Override
    public int getAbilityX(){
        return 109;
    }

    @Override
    public float[] getStatsFor(int sequence){
        return switch (sequence%10){
            case 9 -> new float[]{0, 0, 0, 0, 0};
            case 8 -> new float[]{0, 0, 0, 0, 1};
            case 7 -> new float[]{1, 0, 0, 1, 5};
            case 6 -> new float[]{2, 0, 0, 2, 5};
            case 5 -> new float[]{2, 1, 0, 2, 5};
            default -> new float[]{4, 2, 2, 0, 10};
        };
    }

    @Override
    public List<Ability> getAbilities(int sequence){
        ArrayList<Ability> activeAbilities = new ArrayList<>();

        switch(sequence%10){
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
                activeAbilities.add(Abilities.XP_COST_REDUCE.create(sequence));
                activeAbilities.add(Abilities.REMOVE_ENCHANTMENT.create(sequence));
            case 8:
                activeAbilities.add(Abilities.ANVIL_GUI.create(sequence));
                activeAbilities.add(Abilities.CONJURER_CONTAINER.create(sequence));
                activeAbilities.add(Abilities.CRAFTER_BONE_MEAL.create(sequence));
                activeAbilities.add(Abilities.ENDER_CHEST.create(sequence));
            case 9:
                activeAbilities.add(Abilities.CRAFTING_SPIRITUALITY.create(sequence));
                activeAbilities.add(Abilities.CRAFTING_GUI.create(sequence));
                activeAbilities.add(Abilities.FUEL_CREATE.create(sequence));
                activeAbilities.add(Abilities.DURABILITY_REGEN.create(sequence));
        }
//        CraftingBonusAbility abl = new CraftingBonusAbility(sequence);
//        mng.setPathwayAbilities(activeAbilities);
        return activeAbilities;
        //mng.setPathwayPassives(passiveAbilities);
    }

    @Override
    public int getId() {
        return 4;
    }

    @Override
    public String getSequenceNameFromId(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    private String getSequenceName(int seq){
        return switch (seq%10) {
            case 9 -> "Crafter";
            case 8 -> "Conjurer";
            case 7 -> "Enchanter";
            case 6 -> "Artisan";
            case 5 -> "Alchemist";
            default -> "";
        };
    }

    @Override
    public int getSequenceColorFromLevel(int seq){
        return switch (seq%10) {
            case 9 -> 16770989;
            case 8 -> 28791;
            case 7 -> 10107903;
            default -> 0;
        };
    }

}
