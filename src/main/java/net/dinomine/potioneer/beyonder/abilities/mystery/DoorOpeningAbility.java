package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class DoorOpeningAbility extends Ability {

    public DoorOpeningAbility(int sequence){
        this.info = new AbilityInfo(57, 80, "Door Opening", 20+sequence, 5, 20);
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
        int newZ = dir.getNormal().getZ();
        int newX = dir.getNormal().getX();
        int iterations = (9-getSequence())*2 + 1;
        int i = 0;

        while(i <= iterations){
            if(level.getBlockState(pos.offset(newX*i, 0, newZ*i)).isSolid()
                    || level.getBlockState(pos.offset(newX*i, 1, newZ*i)).isSolid()){
//                System.out.println("wall check");
                if(!level.getBlockState(pos.offset(newX*(i+1), 0, newZ*(i+1))).isSolid()
                        && !level.getBlockState(pos.offset(newX*(i+1), 1, newZ*(i+1))).isSolid()){
//                    System.out.println("teleporting");
                    target.teleportRelative(newX*(i+1), 0, newZ*(i+1));
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
