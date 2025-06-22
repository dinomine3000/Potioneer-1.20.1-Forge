package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;

public class CheckLuckAbility extends Ability {

    public CheckLuckAbility(int sequence){
        this.info = new AbilityInfo(5, 104, "Luck Check", sequence, 0, getCooldown());
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        target.sendSystemMessage(Component.literal("Your luck is: "
                + cap.getLuckManager().getLuck()
                + "/" + cap.getLuckManager().getMaxPassiveLuck()));
        return true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public void passive(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void activate(EntityBeyonderManager cap, LivingEntity target) {
    }

    @Override
    public void deactivate(EntityBeyonderManager cap, LivingEntity target) {
    }
}
