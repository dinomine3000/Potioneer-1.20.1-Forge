package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import static net.minecraft.world.level.block.DoorBlock.OPEN;

public class DoorOpeningAbility extends Ability {

    public DoorOpeningAbility(int sequence){
        this.info = new AbilityInfo(57, 80, "Door Opening", 20+sequence, 40 + 10*(9-sequence), 20, "door_opening");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(target.level().isClientSide() || cap.getSpirituality() < info.cost()) return false;
        Level level = target.level();
        BlockPos pos = target.getOnPos().above();
        Direction dir = target.getDirection();

        HitResult block = target.pick(target.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue() + 0.5, 0f, false);
        if(block instanceof BlockHitResult rayTrace){
            BlockState blockTar = level.getBlockState(rayTrace.getBlockPos());
            if(blockTar.is(BlockTags.DOORS)){
                ((DoorBlock) blockTar.getBlock()).setOpen(null, level, blockTar, rayTrace.getBlockPos(), !blockTar.getValue(OPEN));
                cap.requestActiveSpiritualityCost(info.cost());
                return true;
            }
        }
        int newZ = dir.getNormal().getZ();
        int newX = dir.getNormal().getX();
        int iterations = (9-getSequence())*2 + 1;
        int i = 0;

        while(i <= iterations){
            if(level.getBlockState(pos.offset(newX*i, 0, newZ*i)).isCollisionShapeFullBlock(level, pos)
                    || level.getBlockState(pos.offset(newX*i, 1, newZ*i)).isCollisionShapeFullBlock(level, pos)){
//                System.out.println("wall check");
                if(!level.getBlockState(pos.offset(newX*(i+1), 0, newZ*(i+1))).isCollisionShapeFullBlock(level, pos)
                        && !level.getBlockState(pos.offset(newX*(i+1), 1, newZ*(i+1))).isCollisionShapeFullBlock(level, pos)){
//                    System.out.println("teleporting");
                    //target.teleportRelative(newX*(i+1), 0, newZ*(i+1));
                    BlockPos endPos = new BlockPos((int) target.getX() + newX*(i+1), (int) target.getY(), (int) target.getZ() + newZ*(i+1));
                    target.teleportTo(endPos.getX() + 0.5f, endPos.getY(), endPos.getZ() + 0.5f);
                    cap.requestActiveSpiritualityCost(info.cost()*(1+i));
                    level.playSound(null,
                            pos.offset(newX*(i+1), 0, newZ*(i+1)), SoundEvents.ENDERMAN_TELEPORT,
                            SoundSource.PLAYERS, 1, 1);
                    return true;
                }
            }
//            System.out.println("iterating i");
            i++;
        }
        if(target instanceof Player player){
            player.displayClientMessage(Component.literal("Wall is too thick for your level."), true);
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
