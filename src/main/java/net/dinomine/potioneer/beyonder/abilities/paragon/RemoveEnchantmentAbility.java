package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RemoveEnchantmentAbility extends Ability {
    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "disenchant";
    }
    /*public RemoveEnchantmentAbility(int sequence){
//        this.info = new AbilityInfo(109, 224, "Disenchant", 40 + sequence, 50, this.getMaxCooldown(), "disenchant");
//        this.isActive = true;
        super(sequence);
        setCost(ignored -> 50);
    }


    @Override
    public boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) {
            if(cap.getSpirituality() > cost()) return true;
        }
        if(cap.getSpirituality() > cost()){
            ItemStack item = target.getMainHandItem();
            if(item.isEnchanted()){
                item.getTag().remove("Enchantments");
                cap.requestActiveSpiritualityCost(cost());
                return true;
            }
            if(item.is(Items.ENCHANTED_BOOK)){
                item.setCount(0);
                target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK));
                return true;
            }
        }

        return false;
    }*/
}
