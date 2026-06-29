package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;

import static net.minecraft.world.level.block.Block.dropResources;

public class WaterSpellAbility extends AbilityWithOptions {
    private static final int CONJURE_COST = 5;
    private static final int ABSORB_COST = 3;
    private static final int DROWNING_COST = 30;
    private static final Direction[] ALL_DIRECTIONS = Direction.values();
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public WaterSpellAbility(int sequenceLevel) {
        super(sequenceLevel);
        addSecondaryOptions(new AbilityOptions()
                .addEmptyOption("create", Component.literal("Conjure Water"))
                .addEmptyOption("consume", Component.literal("Consume Water")));
    }

    @Override
    protected String getDescId(int sequenceLevel) {
        return "water_spell";
    }

    @Override
    protected boolean primary(LivingEntityBeyonderCapability cap, LivingEntity target) {
        return primaryWithArgument(cap, target, "drowning");
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("drowning")){
            if(target.level().isClientSide()) return true;
            if(cap.getSpirituality() > DROWNING_COST){
                double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
                int duration = 20*10*(10-sequenceLevel);
                AllySystemSaveData saveData = AllySystemSaveData.from((ServerLevel) target.level());
                ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius, ent -> !saveData.areEntitiesAllies(ent, target));
                hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
                for(LivingEntity entity: hits){
                    //if(entity.is(target)) continue;
                    entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap ->
                            victimCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_DROWNING.createInstance(getSequenceLevel(), 0, duration, true), victimCap, entity));
                }
                ParticleMaker.summonAOEParticles(target.level(), target.getEyePosition(), (int)(2*radius), radius, ParticleMaker.Preset.AOE_END_ROD);
                //target.level().playSound(null, target.getOnPos(), SoundEvents.MINECART_INSIDE_UNDERWATER, SoundSource.PLAYERS, 1, 1);
                cap.requestActiveSpiritualityCost(DROWNING_COST);
                return true;
            }
        }
        return false;
    }

    @Override
    protected boolean secondaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("create")){
            if(target.level().isClientSide()) return true;
            if(cap.getSpirituality() > CONJURE_COST && target instanceof Player player){
                HitResult res = player.pick(player.getAttributeValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0, false);
                if(res instanceof BlockHitResult){
                    ItemStack waterStack = new ItemStack(Items.WATER_BUCKET);
                    waterStack.use(player.level(), player, InteractionHand.MAIN_HAND);
                    cap.requestActiveSpiritualityCost(CONJURE_COST);
                    return true;
                }
            }
        } else if (args.equalsIgnoreCase("consume")){
            if(target.level().isClientSide()) return true;
            ServerLevel level = (ServerLevel) target.level();
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
                if(removeWaterBreadthFirstSearch(level, targetPos, (int) radius)){
                    cap.requestActiveSpiritualityCost(-ABSORB_COST);
                    target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 3, false, false, true));
                    if(target instanceof Player player) player.getFoodData().eat(2, 1);
                    return true;
                }
            }
        }
        return false;
    }

    //copied from SpongeBlock class
    private static boolean removeWaterBreadthFirstSearch(Level pLevel, BlockPos pPos, int radius) {
        return BlockPos.breadthFirstTraversal(pPos, radius, 65, (position, consumer) -> {
            for(Direction direction : ALL_DIRECTIONS) {
                consumer.accept(position.relative(direction));
            }

        }, (positionToEmpty) -> {
            BlockState blockstate = pLevel.getBlockState(positionToEmpty);

            Block block = blockstate.getBlock();
            if (block instanceof BucketPickup bucketpickup) {
                if (!bucketpickup.pickupBlock(pLevel, positionToEmpty, blockstate).isEmpty()) {
                    return true;
                }
            }

            if (blockstate.getBlock() instanceof LiquidBlock) {
                pLevel.setBlock(positionToEmpty, Blocks.AIR.defaultBlockState(), 3);
            } else {
                if (!blockstate.is(Blocks.KELP) && !blockstate.is(Blocks.KELP_PLANT) && !blockstate.is(Blocks.SEAGRASS) && !blockstate.is(Blocks.TALL_SEAGRASS)) {
                    return false;
                }

                BlockEntity blockentity = blockstate.hasBlockEntity() ? pLevel.getBlockEntity(positionToEmpty) : null;
                dropResources(blockstate, pLevel, positionToEmpty, blockentity);
                pLevel.setBlock(positionToEmpty, Blocks.AIR.defaultBlockState(), 3);
            }
            return true;
        }) > 1;
    }
}
