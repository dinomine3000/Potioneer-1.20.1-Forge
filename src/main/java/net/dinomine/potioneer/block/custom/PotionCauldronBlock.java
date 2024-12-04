package net.dinomine.potioneer.block.custom;

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
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class PotionCauldronBlock extends BaseEntityBlock {

    public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(LEVEL);
    }

    public PotionCauldronBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 1));
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
        int level = pState.getValue(LEVEL);

        if(heldItemStack.isEmpty()){
            return InteractionResult.PASS;
        }

        if(item == Items.WATER_BUCKET){
            if(level < 3 && !pLevel.isClientSide()){
                if(!pPlayer.isCreative()){
                    pPlayer.setItemInHand(pHand, new ItemStack(Items.BUCKET));
                }

                pPlayer.awardStat(Stats.FILL_CAULDRON);
                changeWaterLevel(pLevel, pPos, pState, 1);
                pLevel.playSound(null, pPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1f, 1f);
            }
            return InteractionResult.SUCCESS;
        } else if(item == Items.BUCKET){
            if(level > 1 && !pLevel.isClientSide()){
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
            return InteractionResult.SUCCESS;
        }

        return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
    }



    private void setWaterLevel(Level pLevel, BlockPos pPos, int level){
        pLevel.setBlockAndUpdate(pPos, pLevel.getBlockState(pPos).setValue(LEVEL, level));
    }

    private void changeWaterLevel(Level pLevel, BlockPos pPos, BlockState pState, int diff){
        //pState.trySetValue(LEVEL, diff);
        setWaterLevel(pLevel, pPos, pLevel.getBlockState(pPos).getValue(LEVEL)+ diff);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return null;
    }
}
