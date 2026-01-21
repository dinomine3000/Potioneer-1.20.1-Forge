package net.dinomine.potioneer.rituals;

import net.dinomine.potioneer.block.ModBlocks;
import net.dinomine.potioneer.block.custom.RitualPedestal;
import net.dinomine.potioneer.block.entity.ModBlockEntities;
import net.dinomine.potioneer.block.entity.RitualAltarBlockEntity;
import net.dinomine.potioneer.block.entity.RitualPedestalBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public record RitualInputData(FIRST_VERSE firstVerse, SECOND_VERSE secondVerse,
                              UUID caster, UUID target,
                              int pathwaySequenceId, BlockPos pos,
                              List<ItemStack> offerings, String thirdVerse,
                              String incense) {
    public enum FIRST_VERSE{
        EXALTATION, //mystery
        INSULTING, //red priest
        RESPECTFUL, //paragon
        CASUAL, //wheel of fortune
        DEFERENT //tyrant
    }

    public enum SECOND_VERSE{
        MEEK, //mystery
        ARROGANT, //red priest
        CURIOUS, //paragon
        COMPOSED, //wheel of fortune
        MODEST //tyrant
    }

    public enum ACTION{
        EMPTY, //nothing

        GUIDANCE, //pathway related items
        IMBUEMENT, //charm creation
        AID, //effect giving

        TELEPORT, //mystery
        CURSE, //mystery

        FIX, //paragon
        DUPLICATE, //paragon

        WEATHER, //tyrant
        DIVINATION, //tyrant

        BUFF, //red priest
        BLESSING, //red priest

        GIVE_LUCK, //wheel of fortune
        GIVE_UNLUCK, //wheel of fortune
        TRIGGER_LUCK_EVENT //wheel of fortune
    }

    public CompoundTag saveToNBT(){
        CompoundTag tag = new CompoundTag();
        tag.putString("firstVerse", firstVerse.toString());
        tag.putString("secondVerse", secondVerse.toString());
        tag.putString("thirdVerse", thirdVerse);
        tag.putString("incense", incense);
        tag.putInt("pathwaySequenceId", pathwaySequenceId);
        tag.putInt("blockPosX", pos.getX());
        tag.putInt("blockPosY", pos.getY());
        tag.putInt("blockPosZ", pos.getZ());
        if(caster != null) tag.putUUID("casterId", caster);
        if(target != null) tag.putUUID("targetId", target);
        saveItemStackList(tag, "offerings", offerings);
        return tag;
    }

    public static void saveItemStackList(CompoundTag tag, String key, List<ItemStack> items) {
        ListTag listTag = new ListTag();
        for (ItemStack stack : items) {
            CompoundTag itemTag = new CompoundTag();
            stack.save(itemTag);
            listTag.add(itemTag);
        }
        tag.put(key, listTag);
    }

    public static RitualInputData loadFromNBT(CompoundTag tag, Level level){
        FIRST_VERSE firstVerse = FIRST_VERSE.valueOf(tag.getString("firstVerse"));
        SECOND_VERSE secondVerse = SECOND_VERSE.valueOf(tag.getString("secondVerse"));
        String thirdVerse = tag.getString("thirdVerse");
        String incense = tag.getString("incense");
        int pathwayId = tag.getInt("pathwaySequenceId");
        UUID caster = null;
        if(tag.contains("casterId")) caster = tag.getUUID("casterId");
        UUID target = null;
        if(tag.contains("targetId")) target = tag.getUUID("targetId");
        ArrayList<ItemStack> stacks = loadItemStackList(tag, "offerings");
        BlockPos pos = new BlockPos(tag.getInt("blockPosX"), tag.getInt("blockPosY"), tag.getInt("blockPosZ"));
        return new RitualInputData(firstVerse, secondVerse, caster, target, pathwayId, pos, stacks, thirdVerse, incense);
    }

    public static ArrayList<ItemStack> loadItemStackList(CompoundTag tag, String key) {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (tag.contains(key, Tag.TAG_LIST)) {
            ListTag listTag = tag.getList(key, Tag.TAG_COMPOUND);
            for (Tag item : listTag) {
                if (item instanceof CompoundTag compound) {
                    items.add(ItemStack.of(compound));
                }
            }
        }
        return items;
    }

    public static List<ItemStack> getLiveItemStacks(RitualInputData data, Level level){
        return getPedestalsOfRitual(data, level).stream().map(RitualPedestalBlockEntity::getRenderStack).toList();
    }

    public static List<RitualPedestalBlockEntity> getPedestalsOfRitual(RitualInputData data, Level level){
        if(level.getBlockEntity(data.pos(), ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get()).isEmpty()) return List.of();
        RitualAltarBlockEntity altar = level.getBlockEntity(data.pos(), ModBlockEntities.RITUAL_ALTAR_BLOCK_ENTITY.get()).get();
        return altar.getPedestals();
    }
}
