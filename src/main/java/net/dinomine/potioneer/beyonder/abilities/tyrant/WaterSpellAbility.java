package net.dinomine.potioneer.beyonder.abilities.tyrant;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.damages.PotioneerDamage;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffect;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.effects.tyrant.WaterJetEffect;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.WaterTrapBlockEntity;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.effects.GeneralAreaEffectMessage;
import net.dinomine.potioneer.savedata.AllySystemSaveData;
import net.dinomine.potioneer.sound.ModSounds;
import net.dinomine.potioneer.util.ParticleMaker;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
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
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static net.dinomine.potioneer.block.custom.BaseLightSourceBlock.WATERLOGGED;
import static net.minecraft.world.level.block.Block.dropResources;

public class WaterSpellAbility extends AbilityWithOptions {
    private static final int CONJURE_COST = 5;
    private static final int ABSORB_COST = 2;
    private static final int DROWNING_COST = 30;
    private static final int WATER_TRAP_COST = 30;
    private static final int WATER_JET_COST = 10;
    private static final int HEALING_COST = 30;
    /**
     * pass the sequence level or pathway-sequence id to define the abilities sequence level
     * abilities that depend on changing pathways like Cogitation, that exists for every pathway, need to process their own pathway-sequence id here.
     * I dont ask specifically for sequence level OR pathway id, but if you want to choose one, pass along the pathwaySequenceId.
     *
     * @param sequenceLevel
     */
    public WaterSpellAbility(int sequenceLevel) {
        super(sequenceLevel, 20);
        updateOptions(sequenceLevel);
    }

    private void updateOptions(int sequenceLevel){
        AbilityOptions pOption = new AbilityOptions()
                .addEmptyOption("drowning", Component.literal("Drowning"))
                .addEmptyOption("water_trap", Component.literal("Water Trap"))
                .addEmptyOption("water_prison", Component.literal("Water Prison"))
                .addEmptyOption("water_jet", Component.literal("Water Jet"));
        if(sequenceLevel < 8) pOption.addEmptyOption("healing", Component.literal("Healing"));
        AbilityOptions sOptions = new AbilityOptions()
                .addEmptyOption("create", Component.literal("Conjure Water"))
                .addEmptyOption("consume", Component.literal("Consume Water"))
                .addEmptyOption("remove_trap", Component.literal("Absorb Water Trap"));
        setPrimaryOptions(pOption);
        setSecondaryOptions(sOptions);
    }

    @Override
    public void upgradeToLevel(int level, LivingEntityBeyonderCapability cap, LivingEntity target) {
        super.upgradeToLevel(level, cap, target);
        updateOptions(level);
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "water_spell";
    }

    @Override
    protected LinkedHashSet<String> getAllDescId(int sequenceLevel) {
        LinkedHashSet<String> res = new LinkedHashSet<>();
        res.add("water_spell_1");
        res.add("water_spell_2");
        res.add("water_spell_3");
        res.add("water_spell_4");
        res.add("water_spell_5");
        res.add("water_spell_6");
        if(sequenceLevel < 8)
            res.add("water_spell_7");
        return res;
    }

    @Override
    protected boolean primaryWithArgument(LivingEntityBeyonderCapability cap, LivingEntity target, String args) {
        if(args.equalsIgnoreCase("drowning")) return applyDrowning(cap, target);
        else if(args.equalsIgnoreCase("water_trap")) return placeWaterTrap(cap, target);
        else if(args.equalsIgnoreCase("water_prison")) return applyWaterPrison(cap, target);
        else if(args.equalsIgnoreCase("water_jet")) return doWaterJet(cap, target);
        else if(args.equalsIgnoreCase("healing")) return doHealing(cap, target);
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
        }
        else if (args.equalsIgnoreCase("consume")){
            if(target.level().isClientSide()) return true;
            ServerLevel level = (ServerLevel) target.level();
            HitResult block = target.pick(target.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5f, 0f, false);
            if(block instanceof BlockHitResult rayTrace){
                BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
                double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
                int blocksRemoved = removeWaterBreadthFirstSearch(level, targetPos, (int) radius);
                if(blocksRemoved > 0){
                    cap.requestActiveSpiritualityCost(-ABSORB_COST*blocksRemoved);
                    //target.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 3, false, false, true));
                    setNextCooldownAs(50);
                    return true;
                }
            }
        }
        else if (args.equalsIgnoreCase("remove_trap")) return absorbWaterTrap(cap, target);
        return false;
    }


    private final int HEALING_RADIUS = 16;
    private boolean doHealing(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < HEALING_COST) return false;
        if(target.level().isClientSide()) return true;

        AllySystemSaveData data = AllySystemSaveData.from((ServerLevel) target.level());
        List<LivingEntity> alliesAround = AbilityFunctionHelper.getLivingEntitiesAround(target, HEALING_RADIUS, ent -> data.areEntitiesAllies(ent, target));
        if(alliesAround.isEmpty()) return false;

        boolean healFlag = false;
        List<ServerPlayer> playerAllies = new ArrayList<>();
        for(LivingEntity ally: alliesAround){
            if(ally.is(target)) continue;
            healFlag = true;
            ally.heal(6);
            if(ally instanceof ServerPlayer player) playerAllies.add(player);
        }
        if(!healFlag) return false;

        target.hurt(PotioneerDamage.tyrantHealing((ServerLevel) target.level()), alliesAround.size());
        if(target instanceof ServerPlayer playerTarget) playerAllies.add(playerTarget);
        setNextCooldownAs(20*10);
        cap.requestActiveSpiritualityCost(HEALING_COST);
        PacketHandler.sendToPlayers(new GeneralAreaEffectMessage(ParticleMaker.Preset.AOE_END_ROD, target.getOnPos().getCenter().toVector3f(), HEALING_RADIUS), playerAllies);
        return true;
    }

    private boolean doWaterJet(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(cap.getSpirituality() < WATER_JET_COST) return false;
        if(target.level().isClientSide()) return true;
        if(cap.getEffectsManager().hasEffect(BeyonderEffects.TYRANT_WATER_JET)) return false;
        BeyonderEffect waterJetEffect = BeyonderEffects.TYRANT_WATER_JET.createInstance(getSequenceLevel(), WaterJetEffect.DURATION, false);
        cap.getEffectsManager().addOrReplaceEffect(waterJetEffect, cap, target);
        cap.requestActiveSpiritualityCost(WATER_JET_COST);
        setNextCooldownAs(WaterJetEffect.DURATION);
        return true;
    }

    private boolean applyWaterPrison(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > cost()){
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
            AllySystemSaveData saveData = AllySystemSaveData.from((ServerLevel) target.level());
            ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius, ent -> !saveData.areEntitiesAllies(ent, target));
            for(LivingEntity entity: hits){
                if(entity.is(target)) continue;
                entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap ->
                        victimCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_WATER_PRISON.createInstance(getSequenceLevel(), 0, 20*30, true), victimCap, entity));
            }
            ParticleMaker.summonAOEParticles(target.level(), target.getEyePosition(), (int)(2*radius), radius, ParticleMaker.Preset.AOE_END_ROD);
            target.level().playSound(null, target.getOnPos(), ModSounds.WATER_PRISON.get(), SoundSource.PLAYERS, 1, 1);
            cap.requestActiveSpiritualityCost(cost());
            return true;
        }
        return false;
    }

    protected boolean applyDrowning(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return true;
        if(cap.getSpirituality() > DROWNING_COST){
            double radius = target.getAttributeBaseValue(ForgeMod.ENTITY_REACH.get()) + (10 - getSequenceLevel());
            int duration = 20*10*(10-sequenceLevel);
            AllySystemSaveData saveData = AllySystemSaveData.from((ServerLevel) target.level());
            ArrayList<LivingEntity> hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius, ent -> !saveData.areEntitiesAllies(ent, target));
            //hits = AbilityFunctionHelper.getLivingEntitiesAround(target, radius);
            for(LivingEntity entity: hits){
                if(entity.is(target)) continue;
                entity.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(victimCap ->
                        victimCap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.TYRANT_DROWNING.createInstance(getSequenceLevel(), 0, duration, true), victimCap, entity));
            }
            ParticleMaker.summonAOEParticles(target.level(), target.getEyePosition(), (int)(2*radius), radius, ParticleMaker.Preset.AOE_END_ROD);
            //target.level().playSound(null, target.getOnPos(), SoundEvents.MINECART_INSIDE_UNDERWATER, SoundSource.PLAYERS, 1, 1);
            //cap.requestActiveSpiritualityCost(DROWNING_COST);
            return true;
        }
        return false;
    }

    //copied from SpongeBlock class
    private static int removeWaterBreadthFirstSearch(Level pLevel, BlockPos pPos, int radius) {
        return BlockPos.breadthFirstTraversal(pPos, radius, 65, (position, consumer) -> {
            for(Direction direction : Direction.values()) {
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
        });
    }

    protected boolean absorbWaterTrap(LivingEntityBeyonderCapability cap, LivingEntity target){
        if(target.level().isClientSide()) return true;
        if(!(target instanceof Player player)) return false;
        HitResult block = player.pick(player.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
        Level level = player.level();
        if(block instanceof BlockHitResult rayTrace){
            //if the block youre targeting is a water trap and its yours
            if(level.getBlockState(rayTrace.getBlockPos()).is(ModBlocks.WATER_TRAP_BLOCK.get())){
                BlockEntity be = level.getBlockEntity(rayTrace.getBlockPos());
                if(be instanceof WaterTrapBlockEntity waterBe && waterBe.isOwner(player.getUUID())){
                    waterBe.markForAbsorption();
                    level.destroyBlock(rayTrace.getBlockPos(), false, target);
                    cap.requestActiveSpiritualityCost(-WATER_TRAP_COST/2f);
                    return true;
                }
            }
        }
        return false;
    }

    protected boolean placeWaterTrap(LivingEntityBeyonderCapability cap, LivingEntity target) {
        if(target.level().isClientSide()) return true;
        if(!(target instanceof Player player)) return false;
        HitResult block = player.pick(player.getAttributeBaseValue(ForgeMod.BLOCK_REACH.get()) + 0.5, 0f, false);
        Level level = player.level();
        if(block instanceof BlockHitResult rayTrace){
            BlockPos targetPos = rayTrace.getBlockPos().relative(rayTrace.getDirection());
            //otherwise, if the block you are targeting can be replaced
            if(cap.getSpirituality() > WATER_TRAP_COST
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.WATER)
                    && level.getBlockState(rayTrace.getBlockPos()).canBeReplaced()){
                placeBlock(level, rayTrace.getBlockPos(), cap, player);
                return true;

            }
            //otherwise, if the block on the side you are targeting can be replaced
            else if(cap.getSpirituality() > WATER_TRAP_COST
                    && !level.getBlockState(rayTrace.getBlockPos()).is(Blocks.AIR)
                    && level.getBlockState(targetPos).canBeReplaced())
            {
                placeBlock(level, targetPos, cap, player);
                return true;
            }
        }

        //by this point, normal placement failed, so we use a custom algorithm
        Vec3 eyePos = target.getEyePosition();
        BlockPos headPos = BlockPos.containing(eyePos);
        Vec3 lookDir = target.getLookAngle().normalize();
        float jump = 0.05f;
        BlockPos found = null;
        while(found == null){
            eyePos = eyePos.add(lookDir.scale(jump));
            BlockPos testPos = BlockPos.containing(eyePos);
            if(Math.abs(testPos.getX() - headPos.getX()) <= 1 &&
                    Math.abs(testPos.getY() - headPos.getY()) <= 1 &&
                    Math.abs(testPos.getZ() - headPos.getZ()) <= 1) continue;
            found = testPos;
        }

        placeBlock(level, found, cap, player);
        return true;
    }

    private void placeBlock(Level level, BlockPos positionToPlace, LivingEntityBeyonderCapability cap, Player player){
        boolean water = level.getFluidState(positionToPlace).getType() == Fluids.WATER;
        level.setBlockAndUpdate(positionToPlace,
                ModBlocks.WATER_TRAP_BLOCK.get().defaultBlockState().setValue(WATERLOGGED, water));
        WaterTrapBlockEntity be = (WaterTrapBlockEntity) level.getBlockEntity(positionToPlace);
        if(be != null) be.setPlacedByPlayer(player.getUUID(), cap.getSequenceLevel());
        cap.requestActiveSpiritualityCost(WaterSpellAbility.WATER_TRAP_COST);
        setNextCooldownAs(20*5);
    }
}
