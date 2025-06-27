package net.dinomine.potioneer.beyonder.abilities.wheeloffortune;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityInfo;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;

import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Predicate;

public class BlockSniffAbility extends Ability {

    public BlockSniffAbility(int sequence){
        this.info = new AbilityInfo(5, 56, "Block Finder", sequence, 10*(10-sequence), 30*20, "xray");
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
            if(level.isClientSide()){
//                System.out.println("Found an ore on hand");
                BlockPos pos = BlockPos.containing(player.getX(), player.getY(), player.getZ());
                int radius = (9 - info.id()%10)*3 + 7;
                AABB box = new AABB(
                        pos.getX()-radius, pos.getY()-radius, pos.getZ()-radius,
                        pos.getX()+radius, pos.getY()+radius, pos.getZ()+radius
                );

                Optional<BlockPos> blocks = BlockPos.findClosestMatch(pos, radius, radius, testPos -> {
                    return level.getBlockState(testPos).is(blockItem.getBlock());
                });

//                System.out.println(blocks);

                blocks.ifPresent(match -> {
//                    System.out.println(match);
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
//                System.out.println("Blocks: " + blocks);
//                ArrayList<BlockPos> matches = new ArrayList<>(blocks.stream().filter(block ->
//                {
//                    return level.getBlockState(block).is(blockItem.getBlock());
//                }).toList());
//                System.out.println("Matches: " + matches);
//
//                if(matches.isEmpty()){
//                    player.sendSystemMessage(Component.literal("Could not find any desired ores"));
//                } else {
//                    matches.sort((a, b) -> {
//                        return (int)(pos.distToCenterSqr(a.getX(), a.getY(), a.getZ())
//                                - pos.distToCenterSqr(b.getX(), b.getY(), b.getZ()));
//                    });
//                    BlockPos resPos = matches.get(0);
//                }
//                double dist = (9 - info.id()%10)*8 + 5;
//                float temp = 0.7f;
//                Vec3 lookAngle = target.getLookAngle();
//                while(temp < dist){
//                    Vec3 itVector = target.getEyePosition().add(lookAngle.scale(temp));
//                    level.addParticle(ParticleTypes.POOF, itVector.x, itVector.y, itVector.z,0, -0.02f, 0);
//                    temp += 0.4f;
//                }
            } else {
                cap.requestActiveSpiritualityCost(info.cost());
//                Vec3 lookAngle = target.getLookAngle();
//                Vec3 pos = target.position();
//                int radius = (9 - info.id()%10)*8 + 5;
//                AABB box = new AABB(
//                        pos.x-radius, pos.y-radius, pos.z-radius,
//                        pos.x+radius, pos.y+radius, pos.z+radius
//                );
//                ArrayList<Entity> hits = new ArrayList<>(level.getEntities(target, box, new Predicate<Entity>() {
//                    @Override
//                    public boolean test(Entity entity) {
//                        if(entity instanceof LivingEntity living){
//                            double dist = living.position().subtract(target.position()).length();
//                        System.out.println(dist);
//                        System.out.println(height);
//                            boolean hit = living.getBoundingBoxForCulling().intersects(target.getEyePosition(),
//                                    target.getEyePosition().add(lookAngle.scale(dist+1)));
//                        System.out.println(hit);
//                            return hit;
//                        }
//                        return false;
//                    }
//                }));
//                hits.forEach(ent -> {
//                    int pow = (10-info.id()%10);
//                    ent.hurt(level.damageSources().indirectMagic(target, target), (float) (0.384f*Math.pow(pow, 2) + 2.461f*pow + 3.938f));
//                });
//                level.playSound(null, target.getOnPos().above(), SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.PLAYERS, 1, 1);
            }
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
