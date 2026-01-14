package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.Optional;

public class BlockSniffAbility extends Ability {

    public BlockSniffAbility(int sequence){
        super(sequence);
//        this.info = new AbilityInfo(5, 56, "Block Finder", sequence, 10 + 5*(9-sequence), 30*20, "xray");
//        this.isActive = true;
        setCost(level -> 10 + 5*(9-sequence));
        defaultMaxCooldown = 15*20;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "xray";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(cap.getSpirituality() < cost() || !(target instanceof Player player)) return false;
        Level level = player.level();

        if(player.getMainHandItem().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(Tags.Blocks.ORES)){

            BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
            int radius = (9 - getSequenceLevel())*3 + 7;
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
                        i += 0.2F;
                    }
                });
            } else {
                cap.getCharacteristicManager().progressActing(1/128d, 8);
                cap.requestActiveSpiritualityCost(cost());
            }
            //the early return above guarantees that the ability only returns true if it found anything.
            return true;
        }
        return false;
    }
}
