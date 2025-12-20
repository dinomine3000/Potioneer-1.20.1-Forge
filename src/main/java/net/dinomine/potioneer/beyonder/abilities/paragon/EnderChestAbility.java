package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.menus.ConjurerEnderChestContainer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;

public class EnderChestAbility extends Ability {
    public EnderChestAbility(int sequence){
        this.info = new AbilityInfo(109, 152, "Ender Chest", 40 + sequence, 50, this.getMaxCooldown(), "ender_chest");
        this.isActive = true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()
                && target instanceof LocalPlayer player
                && cap.getSpirituality() >= info.cost()){
            player.playSound(SoundEvents.ENDER_CHEST_OPEN);
            return true;
        }
        if(cap.getSpirituality() > info.cost()){
            if(target instanceof Player player){
                ConjurerEnderChestContainer chest = new ConjurerEnderChestContainer(player.getEnderChestInventory());


                player.openMenu(new SimpleMenuProvider((id, playerInv, container) -> {
                    return ChestMenu.threeRows(id, playerInv, chest);
                }, Component.translatable("container.enderchest")));
                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            }
        }

        return false;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void activate(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public void deactivate(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }
}
