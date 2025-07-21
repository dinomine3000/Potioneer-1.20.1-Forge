package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class RemoveEnchantmentAbility extends Ability {
    private static final Item sourceItem = Items.CAKE;
    private static final float percentCost = 0.4f;
    private static final float percentDelta = 0.2f;

    public RemoveEnchantmentAbility(int sequence){
        this.info = new AbilityInfo(109, 224, "Disenchant", 40 + sequence, 50, this.getCooldown(), "disenchant");
        this.isActive = true;
    }

    @Override
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {

    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) {
            return cap.getSpirituality() > info.cost();
        }
        if(cap.getSpirituality() > info.cost()){
            ItemStack item = target.getMainHandItem();
            if(item.isEnchanted()){
                item.getTag().remove("Enchantments");
                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            }
            if(item.is(Items.ENCHANTED_BOOK)){
                item.setCount(0);
                target.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.BOOK));
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
