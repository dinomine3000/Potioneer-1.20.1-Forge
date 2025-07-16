package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.Optional;

public class BlockSniffAbility extends Ability {

    public BlockSniffAbility(int sequence){
        this.info = new AbilityInfo(5, 56, "Block Finder", sequence, 10 + 20*(8-sequence), 30*20, "xray");
        this.isActive = true;
    }

    @Override
    public void onAcquire(EntityBeyonderManager cap, LivingEntity target) {

    }

    @Override
    public boolean active(EntityBeyonderManager cap, LivingEntity target) {
        if(cap.getSpirituality() < getInfo().cost() || !(target instanceof Player player)) return false;
        Level level = player.level();

        if(player.getMainHandItem().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(Tags.Blocks.ORES)){

            BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
            int radius = (9 - info.id()%10)*3 + 7;
            Optional<BlockPos> blocks = BlockPos.findClosestMatch(pos, radius, radius,
                    testPos -> level.getBlockState(testPos).is(blockItem.getBlock()));

            if(!blocks.isPresent()) return false;
            if(level.isClientSide()){
                blocks.ifPresent(match -> {
                    Vec3 pointing = match.getCenter().subtract(player.getEyePosition()).normalize();
                    float i = 0.4f;
                    while(i < 1){
                        Vec3 iterator = player.getEyePosition().add(pointing.scale(i));
                        float speedScale = 0.1f;
                        level.addAlwaysVisibleParticle(ParticleTypes.END_ROD, false,
                                iterator.x, iterator.y, iterator.z, speedScale*pointing.x, speedScale*pointing.y, speedScale*pointing.z);
                        i += 0.2;
                    }
                });
            } else {
                cap.getActingManager().progressActing(1/128d, 8);
                cap.requestActiveSpiritualityCost(info.cost());
            }
            //the early return above guarantees that the ability only returns true if it found anything.
            return true;
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
