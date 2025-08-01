package net.dinomine.potioneer.menus.ritual_altar;

import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.entity.RitualAltarBlockEntity;
import net.dinomine.potioneer.menus.ModMenuTypes;
import net.dinomine.potioneer.rituals.RitualInputData;
import net.dinomine.potioneer.util.BufferUtils;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.Nullable;

public class RitualAltarMenu extends AbstractContainerMenu {
    public final RitualAltarBlockEntity blockEntity;
    private final Level level;
    private RitualInputData.FIRST_VERSE firstVerse;
    private RitualInputData.SECOND_VERSE secondVerse;
    private String thirdVerse;

    public RitualAltarMenu(int pContainerId, Inventory inv, FriendlyByteBuf buf) {
        this(pContainerId, inv,
                inv.player.level().getBlockEntity(buf.readBlockPos()),
                BufferUtils.readString(buf),
                BufferUtils.readString(buf),
                BufferUtils.readString(buf));
    }

    public RitualAltarMenu(int pContainerId, Inventory inv, BlockEntity entity, String firstVerse, String secondVerse, String thirdVerse){
        super(ModMenuTypes.RITUAL_ALTAR_MENU.get(), pContainerId);
        checkContainerSize(inv, 4);
        blockEntity = (RitualAltarBlockEntity) entity;
        this.level = inv.player.level();

        addPlayerHotbar(inv);
        addPlayerInventory(inv);

        //candles
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.DOWN).ifPresent(cap -> {
            this.addSlot(new SlotItemHandler(cap, 0, 129, 28));
            this.addSlot(new SlotItemHandler(cap, 1, 147, 19));
            this.addSlot(new SlotItemHandler(cap, 2, 147, 37));
        });

        //incense
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER, Direction.UP).ifPresent(cap -> {
            this.addSlot(new SlotItemHandler(cap, 0, 25, 28));
        });

        this.firstVerse = RitualInputData.FIRST_VERSE.valueOf(firstVerse);
        this.secondVerse = RitualInputData.SECOND_VERSE.valueOf(secondVerse);
        this.thirdVerse = thirdVerse;
    }

    public String getVerse(int idx){
        return switch(idx){
            case 1 -> firstVerse.toString();
            case 2 -> secondVerse.toString();
            case 3 -> thirdVerse;
            default -> "";
        };
    }

    // CREDIT GOES TO: diesieben07 | https://github.com/diesieben07/SevenCommons
    // must assign a slot number to each of the slots used by the GUI.
    // For this container, we can see both the tile inventory's slots as well as the player inventory slots and the hotbar.
    // Each time we add a Slot to the container, it automatically increases the slotIndex, which means
    //  0 - 8 = hotbar slots (which will map to the InventoryPlayer slot numbers 0 - 8)
    //  9 - 35 = player inventory slots (which map to the InventoryPlayer slot numbers 9 - 35)
    //  36 - 44 = TileInventory slots, which map to our TileEntity slot numbers 0 - 8)
    private static final int HOTBAR_SLOT_COUNT = 9;
    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT;
    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int VANILLA_FIRST_SLOT_INDEX = 0;
    private static final int TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT;

    // THIS YOU HAVE TO DEFINE!
    private static final int TE_INVENTORY_SLOT_COUNT = 4;  // must be the number of slots you have!
    @Override
    public ItemStack quickMoveStack(Player playerIn, int pIndex) {
        Slot sourceSlot = slots.get(pIndex);
        if (sourceSlot == null || !sourceSlot.hasItem()) return ItemStack.EMPTY;  //EMPTY_ITEM
        ItemStack sourceStack = sourceSlot.getItem();
        ItemStack copyOfSourceStack = sourceStack.copy();

        // Check if the slot clicked is one of the vanilla container slots
        if (pIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!moveItemStackTo(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX
                    + TE_INVENTORY_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;  // EMPTY_ITEM
            }
        } else if (pIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!moveItemStackTo(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            System.out.println("Invalid slotIndex:" + pIndex);
            return ItemStack.EMPTY;
        }
        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.getCount() == 0) {
            sourceSlot.set(ItemStack.EMPTY);
        } else {
            sourceSlot.setChanged();
        }
        sourceSlot.onTake(playerIn, sourceStack);
        return copyOfSourceStack;
    }


    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.RITUAL_ALTAR.get());
    }

    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 84 + i * 18));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    public void writeVerse(String value) {
        thirdVerse = value;
    }

    public void increaseVerse(int verseIdx){
        if(verseIdx == 1){
            firstVerse = switch (firstVerse){
                case EXALTATION -> RitualInputData.FIRST_VERSE.DEFERENT;
                case DEFERENT -> RitualInputData.FIRST_VERSE.CASUAL;
                case CASUAL -> RitualInputData.FIRST_VERSE.INSULTING;
                case INSULTING -> RitualInputData.FIRST_VERSE.RESPECTFUL;
                case RESPECTFUL -> RitualInputData.FIRST_VERSE.EXALTATION;
            };
        } else {
            secondVerse = switch (secondVerse){
                case MEEK -> RitualInputData.SECOND_VERSE.ARROGANT;
                case ARROGANT -> RitualInputData.SECOND_VERSE.COMPOSED;
                case COMPOSED -> RitualInputData.SECOND_VERSE.CURIOUS;
                case CURIOUS -> RitualInputData.SECOND_VERSE.MODEST;
                case MODEST -> RitualInputData.SECOND_VERSE.MEEK;
            };
        }
    }
}
