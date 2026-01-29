package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.pathways.WheelOfFortunePathway;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BlockAppraisalAbility extends Ability {

    public BlockAppraisalAbility(int sequence){
        super(sequence);
//        this.info = new AbilityInfo(5, 56, "Block Finder", sequence, 10 + 5*(9-sequence), 30*20, "xray");
//        this.isActive = true;
        defaultMaxCooldown = 15*20;
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "block_appraisal";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        int radius = (9 - getSequenceLevel())*3 + 7;
        if(cap.getSpirituality() < cost() || !(target instanceof Player player)) return false;
        Level level = player.level();

        if(player.getMainHandItem().getItem() instanceof BlockItem blockItem
                && blockItem.getBlock().defaultBlockState().is(Tags.Blocks.ORES)){

            BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
            Optional<BlockPos> blocks = BlockPos.findClosestMatch(pos, radius, radius,
                    testPos -> level.getBlockState(testPos).is(blockItem.getBlock()));

            if(blocks.isEmpty()) return false;
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
                cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.APPRAISER_ACTING_APPRAISE, 8);
                cap.requestActiveSpiritualityCost(cost());
            }
            //the early return above guarantees that the ability only returns true if it found anything.
            return true;
        }
        if(target.level().isClientSide())
            target.sendSystemMessage(Component.translatable("ability.potioneer.no_ore_in_hand"));
        return false;
    }

    @Override
    protected boolean secondary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        cap.requestActiveSpiritualityCost(cost()/2f);
        int radius = (9 - getSequenceLevel())*3 + 7;
        if(target.level().isClientSide()){
            renderParticles(target.level(), radius, target.position().x, target.position().y, target.position().z);
            putOnCooldown(20, target);
            return false;
        }
        ServerLevel level = (ServerLevel) target.level();
        AABB bb = new AABB(target.getX() - radius, target.getY() - radius, target.getZ() - radius,
                            target.getX() + radius, target.getY() + radius, target.getZ() + radius);
        Map<Block, Integer> resultMap = new HashMap<>();
        level.getBlockStates(bb).forEach(blockState -> {
            if(blockState.getBlock().defaultBlockState().is(Tags.Blocks.ORES)){
                resultMap.merge(blockState.getBlock(), 1, Integer::sum);
            }
        });
        if(resultMap.isEmpty()){
            putOnCooldown(20, target);
            return false;
        }
        MutableComponent component = Component.empty();
        for(Map.Entry<Block, Integer> entry: resultMap.entrySet()){
            component.append(entry.getKey().getName()).append(Component.literal(": " + entry.getValue() + "\n"));
        }
        target.sendSystemMessage(Component.translatable("ability.potioneer.ores_found", component));
        cap.getCharacteristicManager().progressActing(WheelOfFortunePathway.APPRAISER_ACTING_APPRAISE, 8);
        return true;
    }

    private void renderParticles(Level level, double radius, double xPos, double yPos, double zPos) {
        ParticleMaker.particleExplosionGrid(level, radius, xPos, yPos, zPos);
        level.playSound(null, xPos, yPos, zPos, SoundEvents.ENCHANTMENT_TABLE_USE, SoundSource.PLAYERS, 1, (float) level.random.triangle(1d, 0.2d));
    }
}
