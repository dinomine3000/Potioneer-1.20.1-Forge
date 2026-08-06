package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.WaterAffinityEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.custom.RulePylonBlock;
import net.dinomine.potioneer.block.entity.RulePylonBlockEntity;
import net.dinomine.potioneer.savedata.DimensionChunkSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RulePylonAbility extends Ability implements IAreaOfJurisdiction {

    public RulePylonAbility(int sequenceLevel) {
        super(sequenceLevel);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "rule_pylon";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target, CompoundTag args) {
        if(target.level().isClientSide) return true;
        return AbilityFunctionHelper.placeBlockAtReach(target.level(), cap, target, this::placeBlock);
    }

    public static int getMaxPylons(int sequenceLevel){return 1;}

    private boolean placeBlock(Level level, BlockPos positionToPlace, LivingEntityBeyonderCapability cap, LivingEntity player){
        if(!canAddNewPylon(level.getServer(), player)) return false;
        return doPlacePylon((ServerLevel) level, positionToPlace, player);
    }

    private boolean doPlacePylon(ServerLevel level, BlockPos pos, LivingEntity target){
        return RulePylonBlock.placePylon(level, pos, Set.of(Law.UNDERWATER), target, getSequenceLevel());
    }

    private boolean canAddNewPylon(MinecraftServer server, LivingEntity owner){
        int max = getMaxPylons(sequenceLevel);
        Set<BlockPos> pylons = DimensionChunkSavedData.getAllPylonPositionsOwnedBy(server, owner);
        return pylons.size() < max;
    }

    @Override
    public void passive(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide) return;
        if(target.tickCount%(20*3) == target.getId()){
            List<BlockPos> center = new ArrayList<>();
            List<Integer> sides = new ArrayList<>();
            List<String> dims = new ArrayList<>();
            DimensionChunkSavedData.collectAojDataForOwner(target.level().getServer(), target, center, sides, dims);
            CompoundTag dataTag = getData();
            CompoundTag aojTag = getCompoundTag(center, sides, dims);
            dataTag.put("aoj", aojTag);
            setData(dataTag, target);
        }
    }

    @Override
    public List<BlockPos> getCenters(String dimensionLocation) {
        return getCentersFromTag(getData().getCompound("aoj"), dimensionLocation);
    }

    @Override
    public List<Integer> getSides(String dimensionLocation) {
        return getSideFromTag(getData().getCompound("aoj"), dimensionLocation);
    }

    public record Rule(String id, Component title){
        public static final Rule BLOCK_BREAK = new Rule("block_break", Component.literal("Can't break Blocks"));

        public static Rule byId(String id){
            return switch (id){
                case "block_break" -> BLOCK_BREAK;
                default -> throw new IllegalStateException("Unexpected value: " + id);
            };
        }
    }
    public record Punishment(String id, Component title, Execution execution){

        public static final Punishment STRIKE = new Punishment("strike", Component.literal("Strike"), (target, targetCap, tribunal) -> {

        });
        public static final Punishment DISABLE = new Punishment("disable", Component.literal("Disable Abilities"), (target, targetCap, tribunal) -> {

        });
        public static final Punishment IMPRISON = new Punishment("imprison", Component.literal("Imprisonment"), (target, targetCap, tribunal) -> {

        });

        public static Punishment byId(String id){
            return switch (id){
                case "strike" -> STRIKE;
                case "disable" -> DISABLE;
                case "imprison" -> IMPRISON;
                default -> throw new IllegalStateException("Unexpected value: " + id);
            };
        }

        public interface Execution{
            void execute(LivingEntity target, LivingEntityBeyonderCapability targetCap, @Nullable Entity tribunal);
        }
    }
    public record Law(String id, Component title, Execution execution){
        public static final Law UNDERWATER = new Law("underwater", Component.literal("Breathe Underwater"), be -> {
            Set<LivingEntity> hits = new HashSet<>();
            for(ChunkPos pos: be.getClaimedChunks()) hits.addAll(AbilityFunctionHelper.getLivingEntitiesInChunk(be.getLevel(), pos));
            hits.forEach(ent ->{
                WaterAffinityEffect eff = (WaterAffinityEffect) BeyonderEffects.getEffect(BeyonderEffects.TYRANT_WATER_AFFINITY.getEffectId()).createInstance(be.getSequenceLevel(), 0, 20*10, true);
                ent.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> cap.getEffectsManager().addOrRefreshEffect(eff, cap, ent));
            });
        });

        public static Law byId(String id){
            return switch (id){
                case "underwater" -> UNDERWATER;
                default -> throw new IllegalStateException("Unexpected value: " + id);
            };
        }

        public interface Execution{
            void execute(RulePylonBlockEntity be);
        }
    }
}
