package net.dinomine.potioneer.block.custom;

import com.mojang.math.Constants;
import net.dinomine.potioneer.beyonder.pages.PageRegistry;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.dinomine.potioneer.rituals.spirits.Deity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ScriptureStandBlock extends Block {
    public static final IntegerProperty BOOK = IntegerProperty.create("book", 0, 6);
    public static final DirectionProperty DIRECTION = HorizontalDirectionalBlock.FACING;
    public ScriptureStandBlock(Properties pProperties) {
        super(pProperties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(BOOK, 0)
                .setValue(DIRECTION, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        super.createBlockStateDefinition(pBuilder);
        pBuilder.add(BOOK, DIRECTION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        ItemStack stack = pContext.getItemInHand();
        CompoundTag tag = stack.getOrCreateTag();
        return this.defaultBlockState().setValue(DIRECTION, pContext.getHorizontalDirection().getOpposite()).setValue(BOOK, tag.getInt("stand_id"));
    }

    private static final VoxelShape SHAPE = Block.box(2, 0, 2, 14, 16, 14);
    public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if(pLevel.isClientSide()) return InteractionResult.SUCCESS;

        if(pPlayer.isCreative() && pPlayer.isCrouching()){
            pLevel.setBlock(pPos, pState.setValue(BOOK, (pState.getValue(BOOK) + 1) % 6), Block.UPDATE_ALL);
            return InteractionResult.SUCCESS;
        }
        int pathwayId = getPathwayId(pState);
        Deity deity = Pathways.getPathwayById(pathwayId).getDefaultDeity();
        if(deity == null) return InteractionResult.SUCCESS;
        pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            if(!pPlayer.isCrouching()){
                pPlayer.sendSystemMessage(Component.translatable("reputation.potioneer.show", deity.getTitle(), cap.getReputation(pathwayId)));
                if(cap.getReligion() != pathwayId){
                    pPlayer.sendSystemMessage(Component.translatable("reputation.potioneer.confirmation", deity.getTitle()));
                }
                if(cap.canPray(pLevel) && cap.getReligion() == pathwayId){
                    pPlayer.sendSystemMessage(Component.translatable("reputation.potioneer.prayer_available", deity.getTitle()));
                }
            } else if(cap.getReligion() != pathwayId){
                pPlayer.sendSystemMessage(deity.getFieltyMessage());
                pLevel.playSound(null, pPos, SoundEvents.BEACON_POWER_SELECT, SoundSource.PLAYERS);
                cap.setReligion(pathwayId);
                int pageId = PageRegistry.getIdOfPage(deity.getInfoPage());
                cap.addPage(pageId);
                PacketHandler.sendMessageSTC(new OpenScreenMessage(OpenScreenMessage.Screen.Book, pageId), pPlayer);
            }
        });
        return InteractionResult.SUCCESS;
    }

    @Override
    public List<ItemStack> getDrops(BlockState pState, LootParams.Builder pParams) {
        ItemStack stack = new ItemStack(this.asItem());

        CompoundTag tag = new CompoundTag();
        tag.putInt("stand_id", pState.getValue(BOOK));

        stack.setTag(tag);

        return List.of(stack);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        ItemStack stack = new ItemStack(this.asItem());

        CompoundTag tag = new CompoundTag();
        tag.putInt("stand_id", state.getValue(BOOK));

        stack.setTag(tag);

        return stack;
    }

    public static int getPathwayId(BlockState pState){
        if(pState.is(ModBlocks.SCRIPTURE_STAND.get())){
            return pState.getValue(BOOK) - 1;
        }
        return -1;
    }
}
