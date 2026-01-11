package net.dinomine.potioneer.beyonder.effects.paragon;

import net.dinomine.potioneer.beyonder.abilities.Abilities;
import net.dinomine.potioneer.beyonder.abilities.paragon.DurabilityRegenAbility;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class BeyonderDurabilityEffect extends BeyonderEffect {
    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
    }

    @Override
    protected void doTick(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.tickCount%29 == 0){
            if(sequenceLevel <= 7){
                if(target instanceof Player player){
                    Inventory inv = player.getInventory();
                    int size = inv.getContainerSize();
                    for (int i = 0; i < size; i++) {
                        if(inv.getItem(i).isDamageableItem()){
                            ItemStack item = inv.getItem(i);
                            if(item.getDamageValue() > 0 && cap.getSpirituality() > cost) cap.requestActiveSpiritualityCost(cost);
                            item.setDamageValue(Math.max(item.getDamageValue() - 10*(10-sequenceLevel), 0));
                        }
                    }
                }
            }
            ItemStack mainItem = target.getMainHandItem();
            if(mainItem.isDamageableItem() && mainItem.getDamageValue() > 0 && cap.getSpirituality() > cost){
                cap.requestActiveSpiritualityCost(cost);
                mainItem.setDamageValue(Math.max(mainItem.getDamageValue() - 5*(int)Math.pow(10-sequenceLevel, 2) - 15, 0));
            }

            ItemStack offhandItem = target.getOffhandItem();
            if(offhandItem.getDamageValue() > 0 && cap.getSpirituality() > cost/4f){
                cap.requestActiveSpiritualityCost(cost/4f);
                offhandItem.setDamageValue(Math.max(offhandItem.getDamageValue() - 5*(int)Math.pow(10-sequenceLevel, 2) - 15, 0));
            }
        }

    }

    @Override
    public void stopEffects(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getAbilitiesManager().isEnabled(Abilities.DURABILITY_REGEN.getAblId(), sequenceLevel%10)){
            cap.getAbilitiesManager().setAbilityEnabled(Abilities.DURABILITY_REGEN.getAblId(), sequenceLevel%10, false, cap, target);
            cap.getAbilitiesManager().putAbilityOnCooldown(Abilities.DURABILITY_REGEN.getAblId(), sequenceLevel%10, 20*5, target);
        }
    }
}
