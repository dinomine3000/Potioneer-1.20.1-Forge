package net.dinomine.potioneer.beyonder.abilities.paragon;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class FuelAbility extends Ability {
    private static final Item sourceItem = Items.CAKE;
    private static final float percentCost = 0.4f;
    private static final float percentDelta = 0.2f;
    private static final Logger log = LoggerFactory.getLogger(FuelAbility.class);

    public FuelAbility(int sequence){
        this.info = new AbilityInfo(109, 32, "Create Golden Drop", 40 + sequence, 0, this.getCooldown());
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        float adjustedPercent = percentCost - ((float) (9 - getSequence()) / 9 * percentDelta);
        if(target.level().isClientSide() && cap.getSpirituality() > cap.getMaxSpirituality()*adjustedPercent) return true;
        if(cap.getSpirituality() > cap.getMaxSpirituality()*adjustedPercent){
            if(target instanceof Player player && target.getMainHandItem().is(sourceItem)){
                player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.GOLDEN_ROP.get()));
                cap.requestActiveSpiritualityCost(cap.getMaxSpirituality()*adjustedPercent);
                return true;
            }
        }
        return false;
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
