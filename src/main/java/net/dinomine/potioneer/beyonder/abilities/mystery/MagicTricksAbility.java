package net.dinomine.potioneer.beyonder.abilities.mystery;

import net.dinomine.potioneer.beyonder.abilities.Ability;
import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.abilities.AbilityOptions;
import net.dinomine.potioneer.beyonder.abilities.AbilityWithOptions;
import net.dinomine.potioneer.beyonder.effects.BeyonderEffects;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.PaperDaggerProjectile;
import net.dinomine.potioneer.entities.custom.WindShearProjectile;
import net.dinomine.potioneer.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MagicTricksAbility extends AbilityWithOptions {
    public static final int CAST_COST = 15;
    private static final int FIRE_BLINK_RANGE = 16;
    private static final int FREEZE_RANGE = 8;
    private static final int MOB_EFFECT_RANGE = 8;
    private static final int TARGET_RANGE = 12;

    public MagicTricksAbility(int sequenceLevel) {
        super(sequenceLevel);
        AbilityOptions pOptions = new AbilityOptions()
                .addEmptyOption("ignite", Component.literal("Ignition"))
                .addEmptyOption("water", Component.literal("Water Affinity"))
                .addEmptyOption("fire", Component.literal("Fire Blink"))
                .addEmptyOption("paper", Component.literal("Paper Daggers"))
                .addEmptyOption("freeze", Component.literal("Freezing"))
                .addEmptyOption("effect", Component.literal("Effect Transfer"));
        AbilityOptions sOptions = new AbilityOptions()
                .addEmptyOption("flash", Component.literal("Flash Bang"))
                .addEmptyOption("noises", Component.literal("Noises"))
                .addEmptyOption("friction", Component.literal("No Friction"))
                .addEmptyOption("bouncy", Component.literal("Bounce"))
                .addEmptyOption("shock", Component.literal("Shock"))
                .addEmptyOption("fog", Component.literal("Fog"));
        setPrimaryOptions(pOptions);
        setSecondaryOptions(sOptions);
        defaultMaxCooldown = 20*5;
    }

    @Override
    protected boolean primaryWithArgument(BeyonderCapability cap, LivingEntity target, String args) {
        if(cap.getSpirituality() < CAST_COST) return false;
        if (args == null || args.isEmpty()) return false;
        boolean res = switch (args) {
            case "paper" -> doPaper(null, cap, target, true);
            case "water" -> doWater(cap, target);
            case "fire" -> doFire(cap, target);
            case "ignite" -> doIgnite(cap, target);
            case "freeze" -> doFreeze(cap, target);
            case "effect" -> doEffect(cap, target);
            default -> super.primaryWithArgument(cap, target, args);
        };
        if(res) cap.requestActiveSpiritualityCost(CAST_COST);
        return res;
    }

    @Override
    protected boolean secondaryWithArgument(BeyonderCapability cap, LivingEntity caster, String args) {
        if(cap.getSpirituality() < CAST_COST) return false;
        if (args == null || args.isEmpty()) return false;
        /*Optional<LivingEntity> optTarget = AbilityFunctionHelper.getTargetEntityClosestToCrosshair(caster, TARGET_RANGE, 3, true);
        if(optTarget.isEmpty()) return false;*/
        LivingEntity target = caster;
        /*Optional<BeyonderCapability> optCap = CapProvider.beyonder(target);
        if(optCap.isEmpty()) return false;*/
        BeyonderCapability targetCap = cap;
        boolean res = switch (args) {
            case "flash" -> doFlash(targetCap, target);
            case "noises" -> doNoises(targetCap, target);
            case "friction" -> doFriction(targetCap, target);
            case "bouncy" -> doBouncy(targetCap, target);
            case "shock" -> doShock(targetCap, target);
            case "fog" -> doFog(targetCap, target);
            default -> super.secondaryWithArgument(cap, target, args);
        };
        if(res) cap.requestActiveSpiritualityCost(CAST_COST);
        return res;
    }

    public static boolean doPaper(@Nullable Ability castingAbility, BeyonderCapability cap, LivingEntity caster, boolean consume) {
        boolean flag = false;
        if(consume && caster instanceof Player player && !player.isCreative()){
            for(ItemStack stack: player.inventoryMenu.getItems()){
                if(!stack.is(Items.PAPER)) continue;
                stack.shrink(1);
                flag = true;
                break;
            }
        } else flag = true;

        if(!flag) return false;
        Vec3 lookVector = caster.getLookAngle();
        for(int i = 0; i < caster.getRandom().nextInt(3, 4); i++){
            PaperDaggerProjectile projectile = new PaperDaggerProjectile(ModEntities.PAPER_DAGGER_PROJECTILE.get(), caster.level());
            projectile.setPos(caster.getEyePosition());
            projectile.setOwner(caster);
            projectile.setBaseDamage(0.05);
            projectile.shoot(lookVector.x, lookVector.y, lookVector.z, 3, 2);
            caster.level().addFreshEntity(projectile);
        }
        if(castingAbility != null) castingAbility.putOnCooldown(20, caster);
        caster.level().playSound(null, caster.getOnPos(), ModSounds.WHOOOOSH.get(), SoundSource.PLAYERS, 1F, (float) caster.getRandom().triangle(1, 0.2));
        return true;
    }

    private boolean doWater(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.MYSTERY_WATER_BREATHING.createInstance(sequenceLevel, 0, 20*3, true), cap, target);
        return true;
    }

    private boolean doFire(BeyonderCapability cap, LivingEntity target) {
        List<BlockPos> blocks = BlockPos.betweenClosedStream(target.getOnPos().offset(-FIRE_BLINK_RANGE, -FIRE_BLINK_RANGE, -FIRE_BLINK_RANGE), target.getOnPos().offset(FIRE_BLINK_RANGE, FIRE_BLINK_RANGE, FIRE_BLINK_RANGE))
                .filter(pos -> target.level().getBlockState(pos).is(Blocks.FIRE) || target.level().getBlockState(pos).is(Blocks.SOUL_FIRE)).map(BlockPos::immutable).toList();
        if(blocks.isEmpty()) return false;
        if(target.level().isClientSide()) return true;
        BlockPos chosen = blocks.get(target.getRandom().nextInt(blocks.size()));
        BlinkAbility.teleport(target, (ServerLevel) target.level(), chosen, target.getXRot(), target.getYRot());
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.MYSTERY_FIRE_RES.createInstance(sequenceLevel, 0, 20*4, true), cap, target);
        return true;
    }

    private boolean doIgnite(BeyonderCapability cap, LivingEntity target) {
        Level level = target.level();
        BlockPos center = target.getOnPos();
        List<BlockPos> flammableBlocks = BlockPos.betweenClosedStream(
                center.offset(-FIRE_BLINK_RANGE, -FIRE_BLINK_RANGE, -FIRE_BLINK_RANGE),
                center.offset(FIRE_BLINK_RANGE, FIRE_BLINK_RANGE, FIRE_BLINK_RANGE)
        ).filter(pos -> {
            BlockState state = level.getBlockState(pos);
            for (Direction dir : Direction.values()) {
                if (state.isFlammable(level, pos, dir)) {
                    return true;
                }
            }
            return false;
        }).map(BlockPos::immutable).toList();

        if(flammableBlocks.isEmpty()) return false;
        if(level.isClientSide()) return true;

        int firesToPlace = target.getRandom().nextInt(5) + 2;
        boolean flagSuccess = false;
        for(int i = 0; i < firesToPlace; i++){
            BlockPos sourcePos = flammableBlocks.get(target.getRandom().nextInt(flammableBlocks.size()));

            BlockState sourceState = level.getBlockState(sourcePos);
            Direction validDir = Direction.UP;
            for (Direction dir : Direction.values()) {
                if (sourceState.isFlammable(level, sourcePos, dir)) {
                    validDir = dir;
                    break;
                }
            }

            BlockPos firePos = sourcePos.relative(validDir);

            if (BaseFireBlock.canBePlacedAt(level, firePos, validDir)) {
                BlockState fireState = BaseFireBlock.getState(level, firePos);
                level.setBlock(firePos, fireState, Block.UPDATE_ALL_IMMEDIATE);
                flagSuccess = true;
            } else if (BaseFireBlock.canBePlacedAt(level, sourcePos.above(), Direction.UP)) {
                // fallback to top placement if face placement is blocked
                BlockState fireState = BaseFireBlock.getState(level, sourcePos.above());
                level.setBlock(sourcePos.above(), fireState, Block.UPDATE_ALL_IMMEDIATE);
                flagSuccess = true;
            }
        }
        return flagSuccess;
    }

    private boolean doFreeze(BeyonderCapability cap, LivingEntity target) {
        Level level = target.level();
        BlockPos center = target.getOnPos();
        List<BlockPos> blocks = BlockPos.betweenClosedStream(center.offset(-FREEZE_RANGE, -FREEZE_RANGE, -FREEZE_RANGE), center.offset(FREEZE_RANGE, FREEZE_RANGE, FREEZE_RANGE))
                .filter(pos -> level.getBlockState(pos).is(Blocks.WATER) && level.getBlockState(pos).getFluidState().isSource()).map(BlockPos::immutable).toList();
        if(blocks.isEmpty()) return false;
        if(level.isClientSide()) return true;
        for(BlockPos pos: blocks){
            level.setBlock(pos, Blocks.ICE.defaultBlockState(), Block.UPDATE_ALL);
        }
        return true;
    }

    private boolean doEffect(BeyonderCapability cap, LivingEntity target) {
        List<MobEffectInstance> effects = target.getActiveEffects().stream().filter(eff -> !eff.getEffect().isBeneficial()).toList();
        if(effects.isEmpty()) return false;
        List<LivingEntity> hits = AbilityFunctionHelper.getNonAllyLivingEntitiesAround(target, MOB_EFFECT_RANGE);
        if(hits.isEmpty()) return false;
        for(LivingEntity hit: hits){
            int attempts = target.getRandom().nextInt(3) + 1;
            for(int i = 0; i < attempts; i++)
                hit.addEffect(new MobEffectInstance(effects.get(target.getRandom().nextInt(effects.size()))));
        }
        for(MobEffectInstance eff: effects) target.removeEffect(eff.getEffect());
        return true;
    }

    private boolean doFlash(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_FLASH.createInstance(sequenceLevel, 0, 20*2, false), cap, target);
        return true;
    }

    private boolean doNoises(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_MOB_NOISES.createInstance(sequenceLevel, 0, 20*15, false), cap, target);
        return true;
    }

    private boolean doFriction(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.MYSTERY_FRICTIONLESS.createInstance(sequenceLevel, 0, 20*15, false), cap, target);
        return true;
    }

    private boolean doBouncy(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrRefreshEffect(BeyonderEffects.MYSTERY_BOUNCY.createInstance(sequenceLevel, 0, 20*15, false), cap, target);
        return true;
    }

    private boolean doShock(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_SHOCK.createInstance(sequenceLevel, 0, 20, false), cap, target);
        return true;
    }

    private boolean doFog(BeyonderCapability cap, LivingEntity target) {
        cap.getEffectsManager().addOrReplaceEffect(BeyonderEffects.MYSTERY_FOG.createInstance(sequenceLevel, 0, 20*7, false), cap, target);
        return true;
    }

    @Override
    protected String getMainDescId(int sequenceLevel) {
        return "tricks";
    }
}