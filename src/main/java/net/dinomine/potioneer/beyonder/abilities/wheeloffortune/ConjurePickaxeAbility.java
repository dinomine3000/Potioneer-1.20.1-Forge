package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

public class ConjurePickaxeAbility extends Ability {

    public ConjurePickaxeAbility(int sequence){
        this.info = new AbilityInfo(5, 80, "Conjure Pickaxe", sequence, 10, 20*5);
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() && cap.getSpirituality() >= info.cost()) return true;
        if(cap.getSpirituality() >= info.cost()){
            if(target instanceof Player player){
                if(!replacePickIfPresent(player)){
                    if(!player.addItem(new ItemStack(ModItems.MINER_PICKAXE.get()))){
                        player.sendSystemMessage(Component.literal("Could not conjure pickaxe: Not enough space"));
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
            if(player.getInventory().getItem(i).is(ModItems.MINER_PICKAXE.get())){
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
