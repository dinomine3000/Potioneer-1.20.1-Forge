package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ConjureFireSwordAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "fire_sword";
    }

    public ConjureFireSwordAbility(int sequence){
//        this.info = new AbilityInfo(83, 56, "Create Sword", 30 + sequence, 25, 20*30, "fire_sword");
        super(sequence);
        setCost(ignored -> 25);
        defaultMaxCooldown = 20*30;
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost()) return true;
        if(cap.getSpirituality() >= cost()){
            if(target instanceof Player player){
                if(!replacePickIfPresent(player)){
                    ItemStack newSword = new ItemStack(ModItems.FIRE_SWORD.get());
                    MysticismHelper.updateOrApplyMysticismTag(newSword, cost(), player);
                    if(!player.addItem(newSword)){
                        player.sendSystemMessage(Component.literal("Could not conjure sword: Not enough space"));
                        return false;
                    }
                }
            }

            cap.requestActiveSpiritualityCost(cost());
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
}
