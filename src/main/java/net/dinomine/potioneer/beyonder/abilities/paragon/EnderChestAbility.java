package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
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
//        this.info = new AbilityInfo(109, 152, "Ender Chest", 40 + sequence, 50, this.getMaxCooldown(), "ender_chest");
//        this.isActive = true;
        super(sequence);
        setCost(ignored -> 50);
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "ender_chest";
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()
                && target instanceof LocalPlayer player
                && cap.getSpirituality() >= cost()){
            player.playSound(SoundEvents.ENDER_CHEST_OPEN);
            return true;
        }
        if(cap.getSpirituality() > cost()){
            if(target instanceof Player player){
                ConjurerEnderChestContainer chest = new ConjurerEnderChestContainer(player.getEnderChestInventory());


                player.openMenu(new SimpleMenuProvider((id, playerInv, container) -> {
                    return ChestMenu.threeRows(id, playerInv, chest);
                }, Component.translatable("container.enderchest")));
                cap.requestActiveSpiritualityCost(cost());
                return putOnCooldown(target);
            }
        }

        return false;
    }
}
