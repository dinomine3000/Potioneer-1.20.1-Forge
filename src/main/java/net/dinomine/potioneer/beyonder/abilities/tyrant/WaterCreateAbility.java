package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

public class WaterCreateAbility extends Ability {

    public WaterCreateAbility(int sequence){
        this.info = new AbilityInfo(31, 104, "Conjure Water", 10 + sequence, 10, this.getCooldown(), "water_create");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof Player player){
            ItemStack waterStack = new ItemStack(Items.WATER_BUCKET);
            waterStack.use(target.level(), player, InteractionHand.MAIN_HAND);
//            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
//            if(block instanceof BlockHitResult rayTrace){
//                Level level = target.level();
//                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
//                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
//                    && level.getBlockState(targetPos).canBeReplaced()){
//                    level.setBlockAndUpdate(targetPos, Blocks.WATER.defaultBlockState());
//
//                    cap.requestActiveSpiritualityCost(info.cost());
//                    return true;
//                }
//            }
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
