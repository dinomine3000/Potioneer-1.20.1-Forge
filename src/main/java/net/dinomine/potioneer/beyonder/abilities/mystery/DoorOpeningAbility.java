package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
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
    private int cost = 5;

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "door_opening";
    }

    @Override
    protected boolean primary(BeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide() || cap.getSpirituality() < cost) return false;
        Level level = target.level();
        BlockPos pos = target.getOnPos().above();
        Direction dir = target.getDirection();

        HitResult block = target.pick(target.getAttribute(ForgeMod.BLOCK_REACH.get()).getValue() + 0.5, 0f, false);
        if(block instanceof BlockHitResult rayTrace){
            BlockState blockTar = level.getBlockState(rayTrace.getBlockPos());
            if(blockTar.is(BlockTags.DOORS) && blockTar.getBlock() instanceof DoorBlock door){
                door.setOpen(null, level, blockTar, rayTrace.getBlockPos(), !blockTar.getValue(OPEN));
                cap.requestActiveSpiritualityCost(cost);
                return true;
            }
        }
        int newZ = dir.getNormal().getZ();
        int newX = dir.getNormal().getX();
        int range = (9-getSequenceLevel())*4 + 2;
        int i = 0;

        while(i <= range){
            if(!isValidBlockposToTeleportTo(pos.offset(newX*i, 0, newZ*i), level)){
                if(isValidBlockposToTeleportTo(pos.offset(newX*(i+1), 0, newZ*(i+1)), level)){
                    BlockPos endPos = pos.offset(newX*(i+1), 0, newZ*(i+1));
                    if(BlinkAbility.teleport(target, (ServerLevel) level, endPos, target.getXRot(), target.getYRot())){
                        cap.requestActiveSpiritualityCost(cost*(1+i));
                    }
                    return true;
                }
            }
//            System.out.println("iterating i");
            i++;
        }
        if(target instanceof Player player){
            player.displayClientMessage(Component.translatable("message.potioneer.door_opening_too_thick"), true);
        }
        return false;
    }

    public static boolean isValidBlockposToTeleportTo(BlockPos pos, Level level){
        BlockState stateUnder = level.getBlockState(pos);
        BlockState stateAbove = level.getBlockState(pos.above());
        return (stateUnder.isAir() || stateUnder.getCollisionShape(level, pos).isEmpty())
                && (stateAbove.isAir() || stateAbove.getCollisionShape(level, pos).isEmpty());
    }
}
