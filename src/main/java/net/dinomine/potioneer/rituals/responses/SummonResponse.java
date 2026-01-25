package net.dinomine.potioneer.rituals.responses;

import net.dinomine.potioneer.rituals.RandomizableResponse;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Chicken;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class SummonResponse extends SpiritResponse implements RandomizableResponse<SummonResponse> {
    private final int mobId;

    public SummonResponse(int mobId){
        this.mobId = mobId;
    }

    @Override
    public void enactResponse(RitualInputData inputData, Level level) {
        for(int i = 0; i < level.random.nextInt(5); i++){
            Mob mob = createSpawner(this.mobId).apply(level);
            mob.setPos(new Vec3(inputData.pos().getX(), inputData.pos().getY(), inputData.pos().getZ()));
            mob.setTarget(level.getPlayerByUUID(inputData.caster()));
            level.addFreshEntity(mob);
        }
    }

    private Function<Level, Mob> createSpawner(int id){
        return switch (id){
            case 0 -> level -> new Skeleton(EntityType.SKELETON, level);
            case 1 -> level -> new Zombie(EntityType.ZOMBIE, level);
            case 2 -> level -> new Creeper(EntityType.CREEPER, level);
            case 3 -> level -> new Ghast(EntityType.GHAST, level);
            case 4 -> level -> new Witch(EntityType.WITCH, level);
            case 5 -> level -> new WitherSkeleton(EntityType.WITHER_SKELETON, level);
            case 6 -> level -> new Blaze(EntityType.BLAZE, level);
            case 7 -> level -> new EnderMan(EntityType.ENDERMAN, level);
            default -> level -> new Chicken(EntityType.CHICKEN, level);
        };
    }

    @Override
    public CompoundTag saveToNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("mobId", this.mobId);
        return envelopTag(tag, "summoning");
    }

    public static SummonResponse loadFromNbt(Tag tag){
        if(!(tag instanceof CompoundTag compoundTag)) throw new IllegalArgumentException("Error: Tag given is not a compound tag");
        return new SummonResponse(compoundTag.getInt("mobId"));
    }

    @Override
    public SummonResponse getRandom() {
        return new SummonResponse((int)(Math.random()*8));
    }
}
