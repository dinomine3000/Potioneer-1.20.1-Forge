package net.dinomine.potioneer.util.misc;

import com.mojang.datafixers.util.Pair;
import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderCapability;
import net.dinomine.potioneer.beyonder.player.CapProvider;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.FormulaItem;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static net.dinomine.potioneer.util.misc.ModNbtUtils.*;
import static net.dinomine.potioneer.util.misc.ModNbtUtils.MysticismTag.*;

public class MysticismHelper {
    public static final int radius = 16;
    public static final float divinationCost = 20f;


    /**
     * divination on item or potion or formula
     *
     * divines a potion
     * if its a formula of a beyonder potion, replies with whether its accurate or not and a clue for one of its ingredients
     * if its a potion, replies with its sequence and whether or not its safe to consume
     * if its a food item, replies also with whether its harmful or not
     *
     * tries to locate the item (dropped items and/or mobs that drop it and in players inventories) around the player
     * block items (to find 1 copy of that block)
     */
    public static DivinationResult doDivination(ItemStack item, Player seer, int radius, BlockPos position, int pathwaySequenceId, RandomSource random){
        if(seer.level().isClientSide()) return new DivinationResult(false, new ArrayList<>(), -1, 0f, "", ItemStack.EMPTY);
        ServerLevel level = (ServerLevel) seer.level();
        PotionFormulaSaveData savedData = PotionFormulaSaveData.from(level);
        Optional<BeyonderCapability> capability = seer.getCapability(CapProvider.BEYONDER_STATS).resolve();
        //progress acting for hydro shaman by 0.25% per divination
        capability.ifPresent(cap -> cap.getCharacteristicManager().progressActing(1 / 400f, 18));
        if(item.isEmpty()){
            //dream divination / miscelaneous divination
            //return something to do with the next step the player should take to advance
            //could give the name of the next sequence or a clue for an ingredient
            float trigger = random.nextFloat();
            if(trigger < 0.5f){
                //gives you the name of your next sequence.
                //YesNo are become true
                boolean yesNo = true;
                String clue = "beyonder.potioneer.sequence." + Pathways.getPathwayBySequenceId(pathwaySequenceId - 1).getSequenceNameFromId(pathwaySequenceId - 1, false);
                int resSequence = pathwaySequenceId - 1;
                ArrayList<BlockPos> responsePositions = new ArrayList<>();
                return new DivinationResult(yesNo, responsePositions, resSequence, 1f, clue, ItemStack.EMPTY);
            } else {
                //gives you a clue for your next ingredient, as well as their positions
                //YesNo are become false
                boolean yesNo = false;
                ItemStack ingredient = savedData.getRandomItemFromFormulaFor(pathwaySequenceId - 1, random);
                String clue = savedData.getClueForIngredient(ingredient);
                List<BlockPos> positions = findItemInArea(seer, ingredient, position, radius, level);
                int resSequence = pathwaySequenceId - 1;
                return new DivinationResult(yesNo, positions, resSequence, 0f, clue, ingredient);
            }
        }

        //if item exists
        if(hasTag(TAGS.MYSTICISM, item)){
            //if the item is mystical...
            CompoundTag mysticalTag = getTagFromItem(TAGS.MYSTICISM, item);
            Player target = getPlayerFromMysticismTag(mysticalTag, level, 0);
            if(target != null) {
                Optional<BeyonderCapability> cap = target.getCapability(CapProvider.BEYONDER_STATS).resolve();
                if(cap.isPresent()){
                    int targetSequence = cap.get().getPathwaySequenceId();
                    float hp = target.getHealth() / target.getMaxHealth();
                    float spiritualityPercent = cap.get().getSpirituality() / cap.get().getMaxSpirituality();
                    float sanityPercent = cap.get().getSanity() / 100f;
                    float hunger = target.getFoodData().getFoodLevel() / 20f;
                    float status = 0.3f*hp + 0.4f*spiritualityPercent + 0.2f*sanityPercent + 0.1f*hunger;
                    boolean yesNo = targetSequence % 10 < pathwaySequenceId % 10 || status > 0.5;
                    List<BlockPos> positions = new ArrayList<>();
                    positions.add(target.getOnPos());

                    return new DivinationResult(yesNo, positions, targetSequence, status, target.getName().getString(), target.getMainHandItem());
                }
            }
        }

        if(item.is(ModItems.CHARACTERISTIC.get()) && hasTag(ModNbtUtils.TAGS.BEYONDER, item)){
            //if the item is mystical...
            CompoundTag mysticalTag = getTagFromItem(TAGS.MYSTICISM, item);
            CompoundTag beyonderTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.BEYONDER, item);
            int charSequence = ModNbtUtils.BeyonderInfoTag.getAssociatedPathSeqLevel(beyonderTag);
            boolean yesNo = charSequence == pathwaySequenceId - 1;
            float status = yesNo ? 1f : 0f;
            String clue = "beyonder.potioneer.sequence." + Pathways.getPathwayBySequenceId(charSequence).getSequenceNameFromId(charSequence, false);
            ItemStack stack = savedData.getRandomItemFromFormulaFor(charSequence, random);
            List<BlockPos> positions = findItemInArea(seer, stack, position, 64, level);

            Player target = getPlayerFromMysticismTag(mysticalTag, level, 0);
            if(target != null) {
                Optional<BeyonderCapability> cap = target.getCapability(CapProvider.BEYONDER_STATS).resolve();
                if(cap.isPresent()){
                    int targetSequence = cap.get().getPathwaySequenceId();
                    float hp = target.getHealth() / target.getMaxHealth();
                    float spiritualityPercent = cap.get().getSpirituality() / cap.get().getMaxSpirituality();
                    float sanityPercent = cap.get().getSanity() / 100f;
                    float hunger = target.getFoodData().getFoodLevel() / 20f;
                    status = 0.3f*hp + 0.4f*spiritualityPercent + 0.2f*sanityPercent + 0.1f*hunger;
                    yesNo = targetSequence % 10 < pathwaySequenceId % 10 || status > 0.5;
                    positions.add(target.getOnPos());
                    clue = target.getName().getString();
                    stack = target.getMainHandItem();

                }
            }
            return new DivinationResult(yesNo, positions, charSequence, status, clue, stack);
        }

        if(item.is(ModItems.BEYONDER_POTION.get())){
            if(hasTag(ModNbtUtils.TAGS.POTION, item)){
                CompoundTag potionTag = ModNbtUtils.getTagFromItem(ModNbtUtils.TAGS.POTION, item);
                String name = ModNbtUtils.PotionInfoTag.getPotionName(potionTag);
                boolean complete = ModNbtUtils.PotionInfoTag.isPotionComplete(potionTag);

                boolean yesNo;
                int potionSequence = -1;
                String clue;
                float status = 0.5f;

                if(ModNbtUtils.PotionInfoTag.isConflictingPotion(potionTag)){
                    yesNo = false;
                    status = 0.0f;
                    clue = "Death";
                } else {
                    yesNo = complete;
                    try{
                        potionSequence = Integer.parseInt(name);
                        clue = "beyonder.potioneer.sequence." + Pathways.getPathwayBySequenceId(potionSequence).getSequenceNameFromId(potionSequence, false);
                        status = potionSequence%10 == (pathwaySequenceId - 1) % 10 ? 1f : 0.7f;
                    } catch (Exception e){
                        clue = name;
                    }
                }
                return new DivinationResult(yesNo, new ArrayList<>(), potionSequence, status, clue, ItemStack.EMPTY);
            }
        }
        else if(item.is(ModItems.FORMULA.get())) {
            Optional<BeyonderCapability> cap = seer.getCapability(CapProvider.BEYONDER_STATS).resolve();
            if(cap.isPresent()){
                PotionRecipeData data = FormulaItem.applyOrReadFormulaNbt(item, level, pathwaySequenceId, cap.get());
                boolean yesNo = savedData.isFormulaCorrect(data);
                float status = 0f;
                if(yesNo){
                    status = data.id() == pathwaySequenceId - 1 ? 1f : 0.7f;
                }
                ArrayList<BlockPos> positions = new ArrayList<>();
                int i;
                for(i = 0; i < data.main().size(); i++){
                    ItemStack stack = data.main().get(i).getStack();
                    if(stack.isEmpty()) continue;
                    List<BlockPos> matches = findItemInArea(seer, stack, position, radius, level);
                    if(!matches.isEmpty()) {
                        positions = new ArrayList<>(matches);
                        break;
                    }
                }

                ItemStack stack;
                if(i == data.main().size()){
                    stack = data.main().get(random.nextInt(data.main().size())).getStack();
                } else {
                    stack = data.main().get(i).getStack();
                }

                return new DivinationResult(yesNo, positions, data.id(), status, savedData.getClueForIngredient(stack), stack);
            }
        }

        boolean yesNo = true;
        float status = 0.5f;
        if(item.isEdible()){
            List<Pair<MobEffectInstance, Float>> effects = item.getFoodProperties(seer).getEffects();
            if(!effects.isEmpty()){
                ArrayList<MobEffectCategory> weights = new ArrayList<>(
                        effects.stream().map(pair -> pair.getFirst().getEffect().getCategory()).toList()
                );
                if(weights.contains(MobEffectCategory.HARMFUL)) yesNo = false;
                for(MobEffectCategory category: weights){
                    switch (category){
                        case BENEFICIAL -> status += 1f/weights.size();
                        case HARMFUL -> status -= 1f/weights.size();
                    }
                }
            }
        }

        List<BlockPos> positions = findItemInArea(seer, item, position, radius, level);

        //if its not an edible item, the yesno value is whether or not it was found in the specified area.
        //and superimposing that is whether or not the item is used for your next potion. even if its edible,
        //itll always be true if its your next ingredient
        if(!item.isEdible()) {yesNo = !positions.isEmpty();}
        if(savedData.isIngredientForSequence(item, pathwaySequenceId - 1)){
            yesNo = true;
            status = 1f;
        }

        //the sequence information is the highest sequence that uses this item for its potion
        int highestSequence = savedData.getHighestSequenceForItem(item);
        String clue = "";
        if(!positions.isEmpty()){
            BlockPos to = positions.get(0);
            int dx = to.getX() - position.getX();
            int dz = to.getZ() - position.getZ();

            // Determine which axis is dominant
            if (Math.abs(dx) > Math.abs(dz)) {
                clue = dx > 0 ? Direction.EAST.getName() : Direction.WEST.getName();
            } else if (Math.abs(dz) > 0) {
                clue = dz > 0 ? Direction.SOUTH.getName() : Direction.NORTH.getName();
            }
        } else {
            clue = Pathways.getPathwayBySequenceId(highestSequence).getSequenceNameFromId(highestSequence, true);
        }
        return new DivinationResult(yesNo, positions, highestSequence, status, clue,ItemStack.EMPTY);
    }

    private static BlockPos findPlayerRestricted(Player player, BlockPos center, int radius, ServerLevel level){
        BlockPos position = player.getOnPos();
        AABB box = AABB.ofSize(center.getCenter(), radius, radius, radius);
        return box.contains(position.getCenter()) ? player.getOnPos() : null;
    }

    private static List<BlockPos> findEntity(Entity entity, BlockPos center, int radius, ServerLevel level) {
        List<Entity> entities = level.getEntities(entity, AABB.ofSize(center.getCenter(), radius, radius, radius));
        return entities.stream().map(Entity::getOnPos).toList();
    }

    public static Player getPlayerFromMysticalItem(ItemStack stack, Level level, int toConsume){
        if(hasTag(TAGS.MYSTICISM, stack))
            return getPlayerFromMysticismTag(getTagFromItem(TAGS.MYSTICISM, stack), level, toConsume);
        return null;
    }

    private static Player getPlayerFromMysticismTag(CompoundTag mysticalTag, Level level, int toConsume) {
        if(mysticalTag == null) return null;
        UUID id = getPlayerIdFromMysticalTag(mysticalTag, level, toConsume);
        if(id == null) return null;
        return level.getPlayerByUUID(id);
    }
    public static UUID getPlayerIdFromMysticalItem(ItemStack stack, int toConsume){
        if(hasTag(TAGS.MYSTICISM, stack)){
            return getPlayerIdFromMysticalTag(getTagFromItem(TAGS.MYSTICISM, stack), null, toConsume);
        }
        return null;
    }

    private static UUID getPlayerIdFromMysticalTag(CompoundTag mysticalTag, Level level, int toConsume){
        return ModNbtUtils.MysticismTag.getPlayerIdFromMysticalTag(mysticalTag, level, toConsume);
    }


    public static DivinationResult doDivination(ItemStack item, Player seer, int sequenceId, RandomSource random){
        BlockPos pos = seer.getOnPos();
        return doDivination(item, seer, radius, pos, sequenceId, random);
    }

    private static List<BlockPos> findItemInArea(Player player, ItemStack item, BlockPos center, int radius, Level level){
        AABB box = AABB.ofSize(center.getCenter(), radius, radius, radius);
        List<Entity> entities = level.getEntities(new ItemEntity(level, 0, 0, 0, item), box);
        List<BlockPos> itemEntitiesFound = entities.stream().filter(entity -> (entity instanceof ItemEntity itemEntity) && itemEntity.getItem().is(item.getItem())).map(Entity::getOnPos).toList();
        List<? extends Player> players = level.players().stream().filter(testPlayer -> testPlayer.getInventory().contains(item) && !testPlayer.is(player)).toList();

        ArrayList<BlockPos> result = new ArrayList<>(itemEntitiesFound);
        result.addAll(players.stream().map(Entity::getOnPos).toList());
        return result;
    }

    public static float getSpiritualityOfItem(ItemStack stack){
        if(!hasTag(ModNbtUtils.TAGS.MYSTICISM, stack)) return 0f;
        return getSpiritualityOfTag(getTagFromItem(ModNbtUtils.TAGS.MYSTICISM, stack));
    }

    public static String getPlayerNameOfItem(ItemStack stack){
        if(!hasTag(ModNbtUtils.TAGS.MYSTICISM, stack)) return "";
        return getPlayerNameFromTag(getTagFromItem(ModNbtUtils.TAGS.MYSTICISM, stack));
    }

    /**
     * this method adds the player into the items NBT registry, creating the myst tag if it doesnt exist.
     * @param stack
     * @param spiritualityAmount
     * @param target
     */
    public static void updateOrApplyMysticismTag(ItemStack stack, float spiritualityAmount, Player target) {
        CompoundTag mystTag;
        if(hasTag(ModNbtUtils.TAGS.MYSTICISM, stack)) mystTag = getTagFromItem(ModNbtUtils.TAGS.MYSTICISM, stack);
        else mystTag = generateNewMysticismTag();
        setItemRootTag(stack, updateOrApplyTagInfluence(mystTag, spiritualityAmount, target), TAGS.MYSTICISM);
    }
}
