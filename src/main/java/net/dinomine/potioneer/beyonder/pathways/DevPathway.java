package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.paragon.DurabilityRegenAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.StatBonusAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.ConjurePickaxeAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MinerLightAbility;
import net.dinomine.potioneer.beyonder.abilities.wheeloffortune.MiningSpeedAbility;
import net.dinomine.potioneer.beyonder.player.PlayerAbilitiesManager;

import java.util.ArrayList;

public class DevPathway extends Beyonder {

    public DevPathway(int sequence){
        super(sequence, "Dev");
        this.color = 0xFFFFFF;
        this.maxSpirituality = new int[]{0, 0, 0, 0, 1000, 1000, 1000, 1000, 500, 100};
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
                activeAbilities.add(new AirBulletAbility(sequence));
            case 9:
                MiningSpeedAbility mining = new MiningSpeedAbility(sequence);
                WaterAffinityAbility water = new WaterAffinityAbility(sequence);
                SpiritualityRegenAbility regen = new SpiritualityRegenAbility(sequence);
                WeaponProficiencyAbility weapon = new WeaponProficiencyAbility(sequence);

                passiveAbilities.add(mining);
                passiveAbilities.add(water);
                passiveAbilities.add(regen);
                passiveAbilities.add(weapon);

                activeAbilities.add(new MinerLightAbility(sequence));
                activeAbilities.add(new ConjurePickaxeAbility(sequence));
                activeAbilities.add(mining);
                activeAbilities.add(water);
                activeAbilities.add(new ReachAbility(sequence));
                activeAbilities.add(new DoorOpeningAbility(sequence));
                activeAbilities.add(regen);
                activeAbilities.add(new InvisibilityAbility(sequence));
                activeAbilities.add(weapon);
                activeAbilities.add(new CraftingGuiAbility(sequence));
                activeAbilities.add(new DurabilityRegenAbility(sequence));
        }

        mng.setPathwayActives(activeAbilities);
        mng.setPathwayPassives(passiveAbilities);
    }


    @Override
    public int getId() {
        return 50 + this.sequence;
    }

    public static String getSequenceName(int seq, boolean show){
        return show ? getSequenceName(seq).replace("_", " ") : getSequenceName(seq).toLowerCase();
    }

    public static String getSequenceName(int seq){
        return String.valueOf(seq);
    }

}
