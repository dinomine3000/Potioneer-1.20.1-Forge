package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.mystery.AirBulletAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.DoorOpeningAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.InvisibilityAbility;
import net.dinomine.potioneer.beyonder.abilities.mystery.ReachAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.DurabilityRegenAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.ConjurePickaxeAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MinerLightAbility;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;
import java.util.List;

public class DevPathway extends BeyonderPathway {

    public DevPathway(){
        super("Dev", 0xFFFFFF, new int[]{0, 0, 0, 0, 1000, 1000, 1000, 1000, 500, 100});
    }

    public List<Ability> getAbilities(int sequence, PlayerAbilitiesManager mng){
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
                activeAbilities.add(new AirBulletAbility(sequence));
            case 9:
                activeAbilities.add(new MinerLightAbility(sequence));
                activeAbilities.add(new ConjurePickaxeAbility(sequence));
                activeAbilities.add(new ReachAbility(sequence));
                activeAbilities.add(new DoorOpeningAbility(sequence));
                activeAbilities.add(new InvisibilityAbility(sequence));
                activeAbilities.add(new CraftingGuiAbility(sequence));
                activeAbilities.add(new DurabilityRegenAbility(sequence));
        }
        return activeAbilities;
//        mng.setPathwayAbilities(activeAbilities);
        //mng.setPathwayPassives(passiveAbilities);
    }


    @Override
    public int getId() {
        return -1;
    }

    @Override
    public int getX() {
        return 0;
    }

    @Override
    public int getY() {
        return 0;
    }

    @Override
    public int getAbilityX() {
        return 0;
    }

    @Override
    public int getSequenceColorFromLevel(int sequenceLevel) {
        return 0;
    }

    @Override
    public List<Ability> getAbilities(int sequenceLevel) {
        return List.of();
    }

    @Override
    public String getSequenceNameFromId(int sequenceLevel, boolean show) {
        return "";
    }

    @Override
    public float[] getStatsFor(int sequence) {
        return new float[0];
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return String.valueOf(seq);
    }

}
