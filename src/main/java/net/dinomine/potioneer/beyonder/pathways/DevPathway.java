package net.dinomine.potioneer.beyonder.pathways;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.Beyonder;
import net.dinomine.potioneer.beyonder.abilities.mystery.*;
import net.dinomine.potioneer.beyonder.abilities.paragon.CraftingGuiAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.StatBonusAbility;
import net.dinomine.potioneer.beyonder.abilities.redpriest.WeaponProficiencyAbility;
import net.dinomine.potioneer.beyonder.abilities.tyrant.WaterAffinityAbility;
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
        MiningSpeedAbility mining = new MiningSpeedAbility(sequence);
        MinerLightAbility light = new MinerLightAbility(sequence);

        WaterAffinityAbility water = new WaterAffinityAbility(sequence);

        InvisibilityAbility invis = new InvisibilityAbility(sequence);
        DoorOpeningAbility door = new DoorOpeningAbility(sequence);
        ReachAbility reach = new ReachAbility(sequence);
        SpiritualityRegenAbility regen = new SpiritualityRegenAbility(sequence);

        WeaponProficiencyAbility weapon = new WeaponProficiencyAbility(sequence);
//        StatBonusAbility stats = new StatBonusAbility(sequence);

        CraftingGuiAbility craft = new CraftingGuiAbility(sequence);

        ArrayList<Ability> activeAbilities9 = new ArrayList<>();
        activeAbilities9.add(reach);
        activeAbilities9.add(invis);
        activeAbilities9.add(door);
        activeAbilities9.add(regen);
        activeAbilities9.add(craft);
        activeAbilities9.add(weapon);
//        activeAbilities9.add(stats);
        activeAbilities9.add(water);
        activeAbilities9.add(mining);
        activeAbilities9.add(light);

        ArrayList<Ability> passiveAbilities9 = new ArrayList<>();
        passiveAbilities9.add(regen);
        passiveAbilities9.add(weapon);
        passiveAbilities9.add(water);
        passiveAbilities9.add(mining);

        mng.setPathwayActives(activeAbilities9);
        mng.setPathwayPassives(passiveAbilities9);
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
