package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class DamageArmorLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        target.getArmorSlots().forEach(armorPiece -> {
            if(armorPiece.isDamageableItem() && !luck.passesLuckCheck(0.4f, 20, 10, target.getRandom())){
                armorPiece.setDamageValue(Mth.clamp(armorPiece.getDamageValue() + target.getRandom().nextInt(armorPiece.getMaxDamage() - armorPiece.getDamageValue()), 0, armorPiece.getMaxDamage()));
            }
        });
    }
}
