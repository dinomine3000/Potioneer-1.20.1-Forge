package net.dinomine.potioneer.beyonder.abilities.redpriest;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import static net.dinomine.potioneer.block.custom.MinerLightSourceBlock.WATERLOGGED;

public class PriestLightAbility extends Ability {

    public PriestLightAbility(int sequence){
        this.info = new AbilityInfo(83, 200, "Light", 30 + sequence, 10, this.getCooldown(), "priest_light");
    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > info.cost() && target instanceof Player player){
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                Level level = target.level();
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                Direction clickedFace = rayTrace.getDirection();
                if(!level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced()){

                    BlockState torchState;

                    if (clickedFace == Direction.UP) {
                        torchState = Blocks.TORCH.defaultBlockState();
                    } else {
                        torchState = Blocks.WALL_TORCH.defaultBlockState().setValue(WallTorchBlock.FACING, clickedFace);
                    }

                    if (torchState.canSurvive(level, targetPos) && level.getBlockState(targetPos).isAir()) {
                        level.setBlock(targetPos, torchState, 3); // flags = 3 (update neighbors + render)
                        level.playSound(null, targetPos, Blocks.TORCH.getSoundType(torchState, level, targetPos, null).getPlaceSound(),
                                SoundSource.BLOCKS, 1.0F, 1.0F);
                        cap.requestActiveSpiritualityCost(info.cost());
                    }

                    return true;
                }
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
