package net.dinomine.potioneer.beyonder.abilities.misc;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BoneMealAbility extends Ability {
    @Override
    protected String getDescId(int sequenceLevel) {
        return abilityId;
    }

    public BoneMealAbility(int sequence){
        super(sequence);
        setCost(level-> 2*(10-level));
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost()) return false;
        if(!(target instanceof Player player)) return false;
        Level level = target.level();
        if (!(level instanceof ServerLevel )) return false;
        HitResult hitResult = target.pick(5.0D, 0.0F, false);
        if (hitResult.getType() != HitResult.Type.BLOCK) return false;

        BlockHitResult blockHit = (BlockHitResult) hitResult;
        UseOnContext context = new UseOnContext(level, player, player.getUsedItemHand(), ItemStack.EMPTY, blockHit);
        Items.BONE_MEAL.useOn(context);
        cap.requestActiveSpiritualityCost(cost());
        return true;
    }
}
