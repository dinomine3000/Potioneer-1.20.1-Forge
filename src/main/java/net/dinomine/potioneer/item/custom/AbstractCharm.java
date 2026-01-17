package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.entities.custom.CharmEntity;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;

public class AbstractCharm extends Item {
    public AbstractCharm(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide())
            return InteractionResultHolder.success(stack);
        if(!isWorkingCharm(stack)){
            MysticalItemHelper.makeCharm(stack, BeyonderEffects.TYRANT_WATER_AFFINITY.getEffectId(), 17, 40*5);
            return InteractionResultHolder.success(stack);
        }
        throwCharm(pPlayer, stack, pUsedHand, !pPlayer.isCrouching());;

        return InteractionResultHolder.success(stack);
    }

    private boolean isWorkingCharm(ItemStack stack){
        return stack.hasTag() && stack.getTag().contains(MysticalItemHelper.CHARM_TAG_ID);
    }

    private void activateCharmOnSelf(Player player, ItemStack stack, InteractionHand usedHand){
        BeyonderEffect eff = MysticalItemHelper.getEffectFromCharm(stack);
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().addOrReplaceEffect(eff, cap, player)){
                player.setItemInHand(usedHand, ItemStack.EMPTY);
            }
        });
    }

    private void throwCharm(Player player, ItemStack stack, InteractionHand usedHand, boolean targetAnother){
        LivingEntity target = null;
        if(!targetAnother){
            target = player;
        } else {
            List<Entity> targets = AbilityFunctionHelper.getLivingEntitiesLooking(player, 32);
            if(targets.isEmpty()) target = player;
            else {
                for(Entity ent: targets){
                    if(!(ent instanceof LivingEntity livingEntity)) continue;
                    if(livingEntity.getUUID().compareTo(player.getUUID()) == 0) continue;
                    target = livingEntity;
                    break;
                }
                if(target == null) target = player;
            }
        }

        int pathwayId = MysticalItemHelper.getPathwayIdFromCharm(stack);
        player.level().playSound(null, player, SoundEvents.ALLAY_AMBIENT_WITHOUT_ITEM, SoundSource.PLAYERS, 1.4f, 1.3f - player.getRandom().nextFloat()*0.6f);
        player.setItemInHand(usedHand, ItemStack.EMPTY);
        BeyonderEffect eff = MysticalItemHelper.getEffectFromCharm(stack);
        player.level().addFreshEntity(CharmEntity.createCharm(target.getUUID(), eff, player, pathwayId));
    }
}
