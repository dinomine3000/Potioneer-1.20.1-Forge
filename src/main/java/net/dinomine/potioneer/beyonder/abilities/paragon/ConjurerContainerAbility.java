package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.ConjurerContainer;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.menus.ConjurerContainerMenu;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;

public class ConjurerContainerAbility extends Ability {
    public ConjurerContainerAbility(int sequence){
        this.info = new AbilityInfo(109, 176, "Conjure", 40 + sequence, 0, this.getCooldown(), "conjure_container");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()
                && target instanceof LocalPlayer player
                && cap.getSpirituality() >= info.cost()){
            player.playSound(SoundEvents.CHEST_OPEN);
            return true;
        }
        if(cap.getSpirituality() > info.cost() && !target.level().isClientSide()){
            if(target instanceof Player player){
                ConjurerContainer cont = cap.getConjurerContainer(0);
//
//                player.openMenu(new SimpleMenuProvider((id, playerInv, container) -> {
//                    return new ChestMenu(MenuType.GENERIC_9x1, id, playerInv, cont, 1);
//                }, Component.translatable("potioneer.menu.conjurer_menu")));
//                cap.requestActiveSpiritualityCost(info.cost());

                player.openMenu(new SimpleMenuProvider((id, playerInv, container) -> {
                    return new ConjurerContainerMenu(MenuType.GENERIC_9x1, id, playerInv, cont, 1);
                }, Component.translatable("potioneer.menu.conjurer_menu")));
                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            }
        }

        return false;
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
