package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ConjurePickaxeAbility extends Ability {

    @Override
    protected String getDescId(int sequenceLevel) {
        return "pick";
    }

    public ConjurePickaxeAbility(int sequence){
//        this.info = new AbilityInfo(5, 80, "Conjure Pickaxe", sequence, 10 + 10*(9-sequence), 20*5, "pick");
        super(sequence);
        setCost(level -> 10 + 10*(9-level));
        defaultMaxCooldown = 20*5;
    }

    @Override
    public boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= cost()) return true;
        if(cap.getSpirituality() >= cost()){
            if(target instanceof Player player){
                if(!replacePickIfPresent(player)){
                    ItemStack newPick = new ItemStack(ModItems.MINER_PICKAXE.get());
                    MysticismHelper.updateOrApplyMysticismTag(newPick, cost(), player);
                    if(!player.addItem(newPick)){
                        player.sendSystemMessage(Component.literal("Could not conjure pickaxe: Not enough space"));
                        return false;
                    }
                }
            }

            cap.requestActiveSpiritualityCost(cost());
            return putOnCooldown(target);
        }
        return false;
    }

    private boolean replacePickIfPresent(Player player){
        int size = player.getInventory().getContainerSize();
        for(int i = 0; i < size; i++){
            if(player.getInventory().getItem(i).is(ModItems.MINER_PICKAXE.get())){
                player.getInventory().getItem(i).setDamageValue(0);
                return true;
            }
        }
        return false;
    }
}
