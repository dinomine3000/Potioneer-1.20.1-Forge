package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

public class VanishingLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        Enchantment vanishing = Enchantments.VANISHING_CURSE;
        target.getArmorSlots().forEach(armorPiece -> {
            armorPiece.enchant(vanishing, 1);
        });
        target.getMainHandItem().enchant(vanishing, 1);
    }
}
