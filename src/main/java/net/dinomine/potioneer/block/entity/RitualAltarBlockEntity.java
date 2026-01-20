package net.dinomine.potioneer.block.entity;

import net.dinomine.potioneer.beyonder.abilities.AbilityFunctionHelper;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.RitualAltarBlock;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.menus.ritual_altar.RitualAltarMenu;
import net.dinomine.potioneer.particle.custom.IncenseSmokeParticleOptions;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.rituals.spirits.EvilSpirit;
import net.dinomine.potioneer.savedata.RitualSpiritsSaveData;
import net.dinomine.potioneer.util.misc.MysticismHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static net.dinomine.potioneer.util.BufferUtils.writeStringToBuffer;

public class RitualAltarBlockEntity extends BlockEntity implements MenuProvider {
    private static final int MAXIMUM_RITUAL_RANGE = 15;

    private RitualInputData.FIRST_VERSE firstVerse = RitualInputData.FIRST_VERSE.DEFERENT;
    private RitualInputData.SECOND_VERSE secondVerse = RitualInputData.SECOND_VERSE.ARROGANT;
    private String thirdVerse = "";
    private EvilSpirit spirit = null;
    private RitualInputData ritualInputData = null;
    private final int FLAT_COST = 40;
    private final int OFFERING_COST = 5;

    public void writeStringsToBuffer(FriendlyByteBuf buf){
        buf.writeBlockPos(getBlockPos());
        writeStringToBuffer(firstVerse.toString(), buf);
        writeStringToBuffer(secondVerse.toString(), buf);
        writeStringToBuffer(thirdVerse, buf);
    }

    public void updateVerses(String firstVerse, String secondVerse, String thirdVerse) {
        this.firstVerse = RitualInputData.FIRST_VERSE.valueOf(firstVerse);
        this.secondVerse = RitualInputData.SECOND_VERSE.valueOf(secondVerse);
        this.thirdVerse = thirdVerse;

        if (!level.isClientSide) {
            BlockState current = level.getBlockState(getBlockPos());
            BlockState updated = current.setValue(RitualAltarBlock.PAPER, !thirdVerse.isEmpty());

            if (!current.equals(updated)) {
                level.setBlock(getBlockPos(), updated, Block.UPDATE_ALL);
            }

            setChanged();
        }
    }


    public enum STATE{
        STANDBY,
        WORKING
    }
    public STATE state = STATE.STANDBY;
    private int timer = -1;

    private final ItemStackHandler candlesItemHandler = new ItemStackHandler(3){
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected int getStackLimit(int slot, @NotNull ItemStack stack) {
            return stack.is(ItemTags.CANDLES) ? 1 : 0;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                int candles = stacks.get(0).getCount() + stacks.get(1).getCount() + stacks.get(2).getCount();
                level.setBlock(getBlockPos(), getBlockState().setValue(RitualAltarBlock.CANDLES, candles), RitualAltarBlock.UPDATE_NONE);
            }
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ItemTags.CANDLES);
        }
    };

    private final ItemStackHandler incenseItemHandler = new ItemStackHandler(1){
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if(!level.isClientSide()){
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                level.setBlock(getBlockPos(), getBlockState().setValue(RitualAltarBlock.INCENSE, stacks.get(0).getCount() > 0), RitualAltarBlock.UPDATE_NONE);
            }
        }
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return stack.is(ModItems.VIAL.get());
        }
    };

    private LazyOptional<ItemStackHandler> candleOptional = LazyOptional.of(() -> candlesItemHandler);
    private LazyOptional<ItemStackHandler> incenseOptional = LazyOptional.of(() -> incenseItemHandler);
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            if(side != Direction.UP){
                return candleOptional.cast();
            }
            return incenseOptional.cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        incenseOptional.invalidate();
        candleOptional.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        System.out.println("[RitualAltarBlockEntity] Saving altar tag");
        pTag.put("candleInv", candlesItemHandler.serializeNBT());
        pTag.put("incenseInv", incenseItemHandler.serializeNBT());
        pTag.putString("firstVerse", firstVerse.toString());
        pTag.putString("secondVerse", secondVerse.toString());
        pTag.putString("thirdVerse", thirdVerse);
        pTag.putString("state", state.toString());
        pTag.putInt("timer", timer);
        if(spirit != null) pTag.put("spirit", spirit.saveToNBT());
        if(ritualInputData != null) pTag.put("ritualData", ritualInputData.saveToNBT());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        System.out.println("[RitualAltarBlockEntity] Loading altar tag");
        candlesItemHandler.deserializeNBT(pTag.getCompound("candleInv"));
        incenseItemHandler.deserializeNBT(pTag.getCompound("incenseInv"));
        this.firstVerse = RitualInputData.FIRST_VERSE.valueOf(pTag.getString("firstVerse"));
        this.secondVerse = RitualInputData.SECOND_VERSE.valueOf(pTag.getString("secondVerse"));
        this.thirdVerse = pTag.getString("thirdVerse");
        this.state = STATE.valueOf(pTag.getString("state"));
        this.timer = pTag.getInt("timer");
        if(pTag.contains("spirit")) this.spirit = EvilSpirit.fromNBT(pTag.getCompound("spirit"));
        if(pTag.contains("ritualData")) this.ritualInputData = RitualInputData.loadFromNBT(pTag.getCompound("ritualData"), level);
    }

    public RitualAltarBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get(), pPos, pBlockState);
    }

    public void dropIngredients(Level pLevel, BlockPos pPos) {
        if(state == STATE.WORKING){
            if(spirit != null){
                spirit.respondTo(ritualInputData, level);
            }
            return;
        }
        SimpleContainer inventory = new SimpleContainer(incenseItemHandler.getSlots() + candlesItemHandler.getSlots());
        for(int i = 0; i < incenseItemHandler.getSlots(); i++){
            inventory.setItem(i, incenseItemHandler.getStackInSlot(i));
        }
        for(int i = 0; i < candlesItemHandler.getSlots(); i++){
            inventory.setItem(i + 1, candlesItemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    public void onTriggerRitual(Player caster){
        if(state != STATE.STANDBY) return;
        Set<BlockPos> ritualArea = getEnclosedInkArea(level, worldPosition, MAXIMUM_RITUAL_RANGE);
        if(ritualArea == null){
            caster.sendSystemMessage(Component.translatable("message.potioneer.invalid_ink_area", MAXIMUM_RITUAL_RANGE));
            return;
        }
        if(thirdVerse.isEmpty()){
            caster.sendSystemMessage(Component.translatable("message.potioneer.invalid_prayer"));
            return;
        }

        if(getCandles() < 1){
            caster.sendSystemMessage(Component.literal("No candles"));
            return;
        }
        List<ItemStack> offerings = getOfferingsIn(ritualArea);
        Player target = getPlayerTargetInRitual(caster, ritualArea, offerings, caster.getInventory());
        Optional<LivingEntityBeyonderCapability> cap = caster.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        int cost = FLAT_COST + offerings.size()*OFFERING_COST;
        int pathwaySequenceId = -1;
        if(cap.isPresent()){
            if(cap.get().getSpirituality() < cost){
                caster.level().playSound(null, caster, SoundEvents.SOUL_ESCAPE, SoundSource.BLOCKS, 1, 1);
                return;
            } else {
                cap.get().requestActiveSpiritualityCost(cost);
            }
            pathwaySequenceId = cap.get().getPathwaySequenceId();
        }
        String incense = getIncenseString();

        ritualInputData = new RitualInputData(firstVerse, secondVerse, caster.getUUID(), target.getUUID(), pathwaySequenceId, getBlockPos(), offerings, thirdVerse, incense);

        System.out.println(ritualInputData);

        RitualSpiritsSaveData data = RitualSpiritsSaveData.from((ServerLevel) level);
        if(getCandles() == 1) spirit = data.getPlayerSpirit();
        else spirit = data.getSpiritForRitual(ritualInputData);
        /*if(spirit != null){
            cleanRitualArea();
        }*/
        state = STATE.WORKING;
        lockPedestals();
        timer = level.random.nextInt(20*2, 20*6);
        level.playSound(null, getBlockPos(), SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.HOSTILE, 2, 1);
        setChanged();
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag res = new CompoundTag();
        res.putString("state", state.toString());
        res.put("incenseInv", incenseItemHandler.serializeNBT());
        return res;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.state = STATE.valueOf(tag.getString("state"));
        incenseItemHandler.deserializeNBT(tag.getCompound("incenseInv"));
    }


    @org.jetbrains.annotations.Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }

    private void lockPedestals(){
        for(BlockPos pos: getOpenArea(level, worldPosition, MAXIMUM_RITUAL_RANGE)){
            if(level.getBlockState(pos).is(ModBlocks.RITUAL_PEDESTAL.get())){
                if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof RitualPedestalBlockEntity be){
                    be.beginRitual();
                }
            }
        }
    }

    private void cleanRitualArea() {
        for(BlockPos pos: getOpenArea(level, worldPosition, MAXIMUM_RITUAL_RANGE)){
            if(level.getBlockState(pos).is(ModBlocks.RITUAL_PEDESTAL.get())){
                if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof RitualPedestalBlockEntity be){
                    be.ritualOver();
                }
            }
        }

        incenseItemHandler.extractItem(0, 1, false);
        candlesItemHandler.extractItem(0, 1, false);
        candlesItemHandler.extractItem(1, 1, false);
        candlesItemHandler.extractItem(2, 1, false);
        level.setBlockAndUpdate(getBlockPos(), getBlockState()
                .setValue(RitualAltarBlock.INCENSE, false)
                .setValue(RitualAltarBlock.CANDLES, 0));

        updateClient();
    }

    private void updateClient(){
        setChanged();
        level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
    }

    private String getIncenseString(){
        ItemStack stack = incenseItemHandler.getStackInSlot(0);
        if(stack.hasTag() && stack.getTag().contains("potion_info")){
            return stack.getTag().getCompound("potion_info").getString("name");
        }
        return "";
    }

    private final int CONSUME_SPIRITUALITY = 20;
    private Player getPlayerTargetInRitual(Player caster, Set<BlockPos> ritualArea, List<ItemStack> offerings, Inventory casterInventory){
        if(getCandles() < 3) return caster;
        for(ItemStack stack: offerings){
            Player player = MysticismHelper.getPlayerFromMysticalItem(stack, (ServerLevel) level, CONSUME_SPIRITUALITY);
            if(player != null) return player;
        }
        List<Entity> entities = AbilityFunctionHelper.getEntitiesAroundPredicate(caster, MAXIMUM_RITUAL_RANGE,
                ent -> ent instanceof ItemEntity && ritualArea.contains(ent.getOnPos()));
        for(Entity ent: entities){
            if(ent instanceof ItemEntity itemEnt){
                Player player = MysticismHelper.getPlayerFromMysticalItem(itemEnt.getItem(), (ServerLevel) level, CONSUME_SPIRITUALITY);
                if(player != null) return player;
            }
        }

        for(ItemStack stack: casterInventory.items){
            Player player = MysticismHelper.getPlayerFromMysticalItem(stack, (ServerLevel) level, CONSUME_SPIRITUALITY);
            if(player != null) return player;
        }

        return caster;
    }

    private int getCandles(){
        return candlesItemHandler.getStackInSlot(0).getCount() +candlesItemHandler.getStackInSlot(1).getCount() +candlesItemHandler.getStackInSlot(2).getCount();
    }

    private List<ItemStack> getOfferingsIn(Set<BlockPos> ritualArea) {
        ArrayList<ItemStack> items = new ArrayList<>();
        for(BlockPos pos: ritualArea){
            if(level.getBlockState(pos).is(ModBlocks.RITUAL_PEDESTAL.get())){
                if(level.getBlockEntity(pos) != null && level.getBlockEntity(pos) instanceof RitualPedestalBlockEntity be){
                    items.add(be.getRenderStack());
                }
            }
        }
        return items;
    }

    public void serverTick(Level level, BlockPos blockPos, BlockState blockState){
        //System.out.println("Altar timer: " + timer);
        if(timer <= -1) return;
        if(timer-- %10 == 0){
            Set<BlockPos> ritualArea = getEnclosedInkArea(level, blockPos, MAXIMUM_RITUAL_RANGE);
            int y = 0;
            for(BlockPos pos: ritualArea){
                y = pos.getY();
                break;
            }
            List<Entity> entities = AbilityFunctionHelper.getEntitiesAroundPredicate(blockPos, level, MAXIMUM_RITUAL_RANGE, s -> true);
            for(Entity ent: entities){
                if(ent instanceof LivingEntity entity
                        && (ritualArea.contains(ent.getOnPos().atY(y))
                        || level.getBlockState(ent.getOnPos()).is(ModBlocks.RITUAL_INK.get())))
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 50, 2, false, false));
            }

            level.playSound(null, blockPos, SoundEvents.TOTEM_USE, SoundSource.PLAYERS, 2 - timer/60f, 2 - timer/30f);
        }

        if(timer < 0){
            state = STATE.STANDBY;
            if(spirit != null){
                spirit.respondTo(ritualInputData, level);
            }
            ritualInputData = null;
            spirit = null;

            cleanRitualArea();
        }
        setChanged();
    }

    public void clientTick(Level level, BlockPos blockPos, BlockState blockState) {
        ItemStack stack = incenseItemHandler.getStackInSlot(0);
        Direction dir = getBlockState().getValue(RitualAltarBlock.DIRECTION);
        Vec3 spawnPos = switch (dir){
            case EAST -> blockPos.getCenter().add(0, 0, 0.3);
            case WEST -> blockPos.getCenter().add(0, 0, -0.3);
            case NORTH -> blockPos.getCenter().add(0.3, 0, 0.1);
            case SOUTH -> blockPos.getCenter().add(-0.3, 0, -0.1);
            default ->  blockPos.getCenter();
        };
        if(!stack.isEmpty() && stack.hasTag() && stack.getTag().contains("potion_info")){
            if(level.random.nextInt(40) == 1){
//                if(stack.isDamageableItem()) stack.setDamageValue(stack.getDamageValue() + 1);
//                if(stack.getDamageValue() > stack.getMaxDamage()) incenseItemHandler.setStackInSlot(0, ItemStack.EMPTY);
                level.addParticle(new IncenseSmokeParticleOptions(stack.getTag().getCompound("potion_info").getInt("color")),
                        spawnPos.x, spawnPos.y, spawnPos.z, 0, 0.05, 0);
            }
        }

        if(state == STATE.WORKING){
            Vec3 center = worldPosition.getCenter();
            level.addParticle(ParticleTypes.SMOKE, true, center.x, center.y + 0.2, center.z, 0, 0.3, 0);
        }
    }

    private static Set<BlockPos> getEnclosedInkArea(Level level, BlockPos center, int maxRadius) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();

        queue.add(center);
        visited.add(center);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(dir);

                // Only process blocks at the same Y level
                if (next.getY() != center.getY() || visited.contains(next)) continue;

                Vec3 diffVec = next.getCenter().subtract(center.getCenter());
                if (diffVec.x > maxRadius || diffVec.z > maxRadius) {
                    // Escaped the boundary, area not closed
                    return null;
                }

                BlockState state = level.getBlockState(next);
                if (isInkBlock(state)) {
                    // Ink acts as wall – do not flood through
                    continue;
                }

                visited.add(next);
                queue.add(next);
            }
        }

        return visited;
    }

    private static Set<BlockPos> getOpenArea(Level level, BlockPos center, int maxRadius) {
        Set<BlockPos> visited = new HashSet<>();
        Queue<BlockPos> queue = new ArrayDeque<>();
        boolean escaped = false;

        queue.add(center);
        visited.add(center);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos next = current.relative(dir);

                // Only process blocks at the same Y level
                if (next.getY() != center.getY() || visited.contains(next)) continue;

                Vec3 diffVec = next.getCenter().subtract(center.getCenter());
                if (diffVec.x > maxRadius || diffVec.z > maxRadius) {
                    // Escaped the boundary
                    escaped = true;
                    continue;
                }

                BlockState state = level.getBlockState(next);
                if (isInkBlock(state)) {
                    // Treat ink as wall
                    continue;
                }

                visited.add(next);
                queue.add(next);
            }
        }

        // If escaped, return the full area in the radius
        if (escaped) {
            Set<BlockPos> fullArea = new HashSet<>();
            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

            for (int dx = -maxRadius; dx <= maxRadius; dx++) {
                for (int dz = -maxRadius; dz <= maxRadius; dz++) {
                    pos.set(center.getX() + dx, center.getY(), center.getZ() + dz);
                    if (pos.distManhattan(center) <= maxRadius) {
                        fullArea.add(pos.immutable());
                    }
                }
            }

            return fullArea;
        }

        return visited;
    }

    private static boolean isInkBlock(BlockState state) {
        return state.getBlock() == ModBlocks.RITUAL_INK.get(); // or a tag check
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("gui.potioneer.ritual_menu");
    }

    @org.jetbrains.annotations.Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new RitualAltarMenu(i, inventory, this, firstVerse.toString(), secondVerse.toString(), thirdVerse);
    }
}
