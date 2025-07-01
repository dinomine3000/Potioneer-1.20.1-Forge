package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ConjureFireSwordAbility extends Ability {

    public ConjureFireSwordAbility(int sequence){
        this.info = new AbilityInfo(83, 56, "Create Sword", 30 + sequence, 25, 20*30, "fire_sword");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;
        if(cap.getSpirituality() >= info.cost()){
            if(target instanceof Player player){
                if(!replacePickIfPresent(player)){
                    ItemStack newSword = new ItemStack(ModItems.FIRE_SWORD.get());
                    MysticismHelper.updateOrApplyMysticismTag(newSword, info.cost(), player);
                    if(!player.addItem(newSword)){
                        player.sendSystemMessage(Component.literal("Could not conjure sword: Not enough space"));
                        return false;
                    }
                }
            }

            cap.requestActiveSpiritualityCost(info.cost());
            return true;
        }
        return false;
    }

    private boolean replacePickIfPresent(Player player){
        int size = player.getInventory().getContainerSize();
        for(int i = 0; i < size; i++){
            if(player.getInventory().getItem(i).is(ModItems.FIRE_SWORD.get())){
                player.getInventory().getItem(i).setDamageValue(0);
                return true;
            }
        }
        return false;
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
