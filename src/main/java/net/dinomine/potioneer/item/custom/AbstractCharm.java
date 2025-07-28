package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.util.misc.ArtifactHelper;
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
            System.out.println("Creating charm");
            ArtifactHelper.makeCharm(stack, 29, 40*5);
            return InteractionResultHolder.success(stack);
        }
        if(pPlayer.isCrouching()) activateCharmOnSelf(pPlayer, stack, pUsedHand);
        else throwCharm(pPlayer, stack, pUsedHand);

        System.out.println("end");
        return InteractionResultHolder.success(stack);
    }

    private boolean isWorkingCharm(ItemStack stack){
        return stack.hasTag() && stack.getTag().contains(ArtifactHelper.CHARM_TAG_ID);
    }

    private void activateCharmOnSelf(Player player, ItemStack stack, InteractionHand usedHand){
        BeyonderEffect eff = ArtifactHelper.getEffectFromCharm(stack);
        player.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().addOrReplaceEffect(eff, cap, player)){
                player.setItemInHand(usedHand, ItemStack.EMPTY);
            }
        });
    }

    private void throwCharm(Player player, ItemStack stack, InteractionHand usedHand){
        List<Entity> targets = AbilityFunctionHelper.getLivingEntitiesLooking(player, 15);
        if(targets.isEmpty()) return;
        for(Entity ent: targets){
            if(!(ent instanceof LivingEntity livingEntity)) continue;
            applyEffectToAnother(livingEntity, stack);
            player.setItemInHand(usedHand, ItemStack.EMPTY);
            return;
        }
    }

    private void applyEffectToAnother(LivingEntity target, ItemStack stack){
        BeyonderEffect eff = ArtifactHelper.getEffectFromCharm(stack);
        target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(cap.getEffectsManager().addOrReplaceEffect(eff, cap, target)){
            }
        });
    }
}
