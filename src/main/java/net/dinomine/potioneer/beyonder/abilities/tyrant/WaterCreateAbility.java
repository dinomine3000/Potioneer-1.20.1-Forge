package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

public class WaterCreateAbility extends Ability {
    private static final float actingProgress = 0.002f;

    public WaterCreateAbility(int sequence){
        this.info = new AbilityInfo(31, 104, "Conjure Water", 10 + sequence, 2, 1, "water_create");
    }

    @Override
    public boolean active(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof Player player){
            HitResult res = player.pick(player.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0, false);
            if(res instanceof BlockHitResult block){
                ItemStack waterStack = new ItemStack(Items.WATER_BUCKET);
                //waterStack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, block));
                waterStack.use(player.level(), player, InteractionHand.MAIN_HAND);
                cap.requestActiveSpiritualityCost(info.cost());
                cap.getActingManager().progressActing(actingProgress, 18);
                return true;
            }
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
    public void onAcquire(LivingEntityBeyonderCapability cap, LivingEntity target) {
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
