package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.entity.LivingEntity;

public class CalamityIncreaseAbility extends Ability {

    private int addedChance;
    public CalamityIncreaseAbility(int sequence){
        this.info = new AbilityInfo(5, 272, "Calamity Increase", sequence, 0, getCooldown(), "calamity");
        addedChance = 2 + Math.max(7-sequence, 0);
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return false;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
        activate(cap, target);
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {
       // System.out.println("+100 on luck range");
        cap.getLuckManager().changeLuckRange(100, 100, 0);
        cap.getLuckManager().chanceLuckEventChange(addedChance);
    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
        //System.out.println("-100 on luck range");
        cap.getLuckManager().changeLuckRange(-100, -100, 0);
        cap.getLuckManager().chanceLuckEventChange(-addedChance);
    }
}
