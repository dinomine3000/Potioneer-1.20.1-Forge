package net.dinomine.potioneer.block.custom;

import net.dinomine.potioneer.block.entity.PotionCauldronBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlock extends BaseEntityBlock {

    public static final IntegerProperty WATER_LEVEL = BlockStateProperties.LEVEL_CAULDRON;
    public static final BooleanProperty RESULT = BlockStateProperties.TRIGGERED;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(WATER_LEVEL);
        pBuilder.add(RESULT);
    }

    public PotionCauldronBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(WATER_LEVEL, 1));
        this.registerDefaultState(this.stateDefinition.any().setValue(RESULT, false));
    }


    private static final VoxelShape SHAPE = Block.box(0, 1, 0, 16, 14, 16);
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        ItemStack heldItemStack = pPlayer.getItemInHand(pHand);

        Item item = heldItemStack.getItem();
        int level = pState.getValue(WATER_LEVEL);

        BlockEntity be = pLevel.getBlockEntity(pPos);
        if(be instanceof PotionCauldronBlockEntity cauldron){
            if(item == Items.WATER_BUCKET){
                if(level < 3 && !pLevel.isClientSide()){
                    if(!pPlayer.isCreative()){
                        pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                    }
                    pPlayer.awardStat(Stats.FILL_CAULDRON);
                    changeWaterLevel(pLevel, pPos, pState, 1);
                    pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
                }
                cauldron.craft();
                return InteractionResult.SUCCESS;
            } else if(item == Items.BUCKET){
                if(level > 1 && !pLevel.isClientSide() && !cauldron.hasResult()){
                    if(!pPlayer.isCreative()){
                        heldItemStack.shrink(1);
                        if(heldItemStack.isEmpty()){
                            pPlayer.setItemInHand(pHand, new ItemStack(Items.WATER_BUCKET));
                        } else if(!pPlayer.getInventory().add(new ItemStack(Items.WATER_BUCKET))){
                            pPlayer.drop(new ItemStack(Items.WATER_BUCKET), false);
                        }
                    }

                    pPlayer.awardStat(Stats.USE_CAULDRON);
                    changeWaterLevel(pLevel, pPos, pState, -1);
                    pLevel.playSound(null, pPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1f, 1f);
                }
                cauldron.craft();
                return InteractionResult.SUCCESS;
            } else {
                if(item == Items.GLASS_BOTTLE && !pLevel.isClientSide()){
                    if(cauldron.hasResult()){
                        ItemStack res = cauldron.extractResult();
                        heldItemStack.shrink(1);
                        setWaterLevel(pLevel, pPos, 1);
                        pLevel.playSound(null, pPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1f, 1f);

                        if(heldItemStack.isEmpty()){
                            pPlayer.setItemInHand(pHand, res);
                        } else if(!pPlayer.getInventory().add(res)){
                            pPlayer.drop(res, false);
                        }
                    }
                } else if(!pLevel.isClientSide()){
                    if(heldItemStack.isEmpty()){
                        ItemStack rem = cauldron.removeItem();
                        if(!pPlayer.getInventory().add(rem)){
                            pPlayer.drop(rem, false);
                        }
                    } else {
                        if(pPlayer.isCreative()){
                            cauldron.addIngredient(heldItemStack, false);
                        } else {
                            cauldron.addIngredient(heldItemStack, true);
                        }
                    }
                }

                return InteractionResult.SUCCESS;
            }
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }


    private void setWaterLevel(Level pLevel, BlockPos pPos, int level){
        pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(WATER_LEVEL, level));
    }

    private void changeWaterLevel(Level pLevel, BlockPos pPos, BlockState pState, int diff){
        //pState.trySetValue(LEVEL, diff);
        setWaterLevel(pLevel, pPos, pLevel.getBlockState(pPos).getValue(WATER_LEVEL)+ diff);
    }

    @Override
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        boolean result = pState.getValue(RESULT);
        if(pLevel.getBlockState(pPos).getBlock() != this.asBlock() && !result && !pLevel.isClientSide()){
            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) pLevel.getBlockEntity(pPos);
            assert be != null;
            be.dropIngredients(pLevel, pPos);
        }
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        boolean result = state.getValue(RESULT);
        System.out.println("Destroyed. Did it have result?:" + result);
        if(!result && !level.isClientSide()){
            PotionCauldronBlockEntity be = (PotionCauldronBlockEntity) level.getBlockEntity(pos);
            assert be != null;
            be.dropIngredients(level, pos);
        }


        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new PotionCauldronBlockEntity(blockPos, blockState);
    }
}
