package net.dinomine.potioneer.util.misc;

import com.mojang.datafixers.util.Pair;
import net.dinomine.potioneer.beyonder.pathways.BeyonderPathway;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.item.custom.FormulaItem;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.recipe.PotionRecipeData;
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

public class MysticismHelper {
    public static final int radius = 16;
    public static final String mysticismTagId = "potioneer_mysticism";
    public static final String spiritualityTagId = "spirituality";
    public static final String totalSpiritualityId = "total_spirituality";
    public static final String playerNameTagId = "players";

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
    public static DivinationResult doDivination(ItemStack item, Player seer, int radius, BlockPos position, int sequenceId, RandomSource random){
        if(seer.level().isClientSide()) return new DivinationResult(false, new ArrayList<>(), -1, 0f, "", ItemStack.EMPTY);
        ServerLevel level = (ServerLevel) seer.level();
        PotionFormulaSaveData savedData = PotionFormulaSaveData.from(level);
        Optional<LivingEntityBeyonderCapability> capability = seer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
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
                String clue = "potioneer.beyonder.sequence." + BeyonderPathway.getSequenceNameFromId(sequenceId - 1, false);
                int resSequence = sequenceId - 1;
                ArrayList<BlockPos> responsePositions = new ArrayList<>();
                return new DivinationResult(yesNo, responsePositions, resSequence, 1f, clue, ItemStack.EMPTY);
            } else {
                //gives you a clue for your next ingredient, as well as their positions
                //YesNo are become false
                boolean yesNo = false;
                ItemStack itemStack = savedData.getRandomItemFromFormulaFor(sequenceId - 1, random);
                String clue = savedData.getClueForIngredient(itemStack);
                List<BlockPos> positions = findItemInArea(seer, itemStack, position, radius, level);
                int resSequence = sequenceId - 1;
                return new DivinationResult(yesNo, positions, resSequence, 0f, clue, itemStack);
            }
        }

        //if item exists
        if(item.hasTag() && item.getTag().contains(mysticismTagId)){
            //if the item is mystical...
            CompoundTag mysticalTag = item.getTag().getCompound(mysticismTagId);
            Player target = getPlayerFromMysticismTag(mysticalTag, level, 0);
            if(target != null) {
                Optional<LivingEntityBeyonderCapability> cap = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(cap.isPresent()){
                    int targetSequence = cap.get().getPathwaySequenceId();
                    float hp = target.getHealth() / target.getMaxHealth();
                    float spiritualityPercent = cap.get().getSpirituality() / cap.get().getMaxSpirituality();
                    float sanityPercent = cap.get().getSanity() / 100f;
                    float hunger = target.getFoodData().getFoodLevel() / 20f;
                    float status = 0.3f*hp + 0.4f*spiritualityPercent + 0.2f*sanityPercent + 0.1f*hunger;
                    boolean yesNo = targetSequence % 10 < sequenceId % 10 || status > 0.5;
                    List<BlockPos> positions = new ArrayList<>();
                    positions.add(target.getOnPos());

                    return new DivinationResult(yesNo, positions, targetSequence, status, target.getName().getString(), target.getMainHandItem());
                }
            }
        }

        if(item.hasTag() && item.is(ModItems.CHARACTERISTIC.get()) && item.getTag().contains("beyonder_info")){
            //if the item is mystical...
            CompoundTag mysticalTag = item.getTag().getCompound(mysticismTagId);
            CompoundTag beyonderTag = item.getTag().getCompound("beyonder_info");
            int charSequence = beyonderTag.getInt("id");
            boolean yesNo = charSequence == sequenceId - 1;
            float status = yesNo ? 1f : 0f;
            String clue = "potioneer.beyonder.sequence." + BeyonderPathway.getSequenceNameFromId(charSequence, false);
            ItemStack stack = savedData.getRandomItemFromFormulaFor(charSequence, random);
            List<BlockPos> positions = findItemInArea(seer, stack, position, 64, level);

            Player target = getPlayerFromMysticismTag(mysticalTag, level, 0);
            if(target != null) {
                Optional<LivingEntityBeyonderCapability> cap = target.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
                if(cap.isPresent()){
                    int targetSequence = cap.get().getPathwaySequenceId();
                    float hp = target.getHealth() / target.getMaxHealth();
                    float spiritualityPercent = cap.get().getSpirituality() / cap.get().getMaxSpirituality();
                    float sanityPercent = cap.get().getSanity() / 100f;
                    float hunger = target.getFoodData().getFoodLevel() / 20f;
                    status = 0.3f*hp + 0.4f*spiritualityPercent + 0.2f*sanityPercent + 0.1f*hunger;
                    yesNo = targetSequence % 10 < sequenceId % 10 || status > 0.5;
                    positions.add(target.getOnPos());
                    clue = target.getName().getString();
                    stack = target.getMainHandItem();

                }
            }
            return new DivinationResult(yesNo, positions, charSequence, status, clue, stack);
        }

        if(item.is(ModItems.BEYONDER_POTION.get())){
            if(item.hasTag() && item.getTag().contains("potion_info")){
                CompoundTag potionTag = item.getTag().getCompound("potion_info");
                String name = potionTag.getString("name");

                boolean yesNo;
                int potionSequence = -1;
                String clue = "";
                float status = 0.5f;

                if(name.equalsIgnoreCase("conflict")){
                    yesNo = false;
                    status = 0.0f;
                    clue = "Death";
                } else {
                    yesNo = true;
                    try{
                        potionSequence = Integer.parseInt(name);
                        clue = "potioneer.beyonder.sequence." + BeyonderPathway.getSequenceNameFromId(potionSequence, false);
                        status = potionSequence%10 == (sequenceId - 1) % 10 ? 1f : 0.7f;
                    } catch (Exception e){
                        clue = name;
                    }
                }
                return new DivinationResult(yesNo, new ArrayList<>(), potionSequence, status, clue, ItemStack.EMPTY);
            }
        }
        else if(item.is(ModItems.FORMULA.get())) {
            Optional<LivingEntityBeyonderCapability> cap = seer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
            if(cap.isPresent()){
                PotionRecipeData data = FormulaItem.applyOrReadFormulaNbt(item, level, sequenceId, cap.get());
                boolean yesNo = savedData.isFormulaCorrect(data);
                float status = 0f;
                if(yesNo){
                    status = data.id() == sequenceId - 1 ? 1f : 0.7f;
                }
                ArrayList<BlockPos> positions = new ArrayList<>();
                int i = 0;
                for(i = 0; i < data.main().size(); i++){
                    ItemStack stack = data.main().get(i);
                    List<BlockPos> matches = findItemInArea(seer, stack, position, radius, level);
                    if(!matches.isEmpty()) {
                        positions = new ArrayList<>(matches);
                        break;
                    }
                }

                ItemStack stack;
                if(i == data.main().size()){
                    stack = data.main().get(random.nextInt(data.main().size()));
                } else {
                    stack = data.main().get(i);
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
        if(savedData.isIngredientForSequence(item, sequenceId - 1)){
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
            clue = BeyonderPathway.getPathwayName(highestSequence, true);
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

    public static Player getPlayerFromMysticalItem(ItemStack stack, ServerLevel level, int toConsume){
        if(stack.hasTag() && stack.getTag().contains(mysticismTagId)){
            return getPlayerFromMysticismTag(stack.getTag().getCompound(mysticismTagId), level, toConsume);
        }
        return null;
    }

    private static Player getPlayerFromMysticismTag(CompoundTag mysticalTag, ServerLevel level, int toConsume) {
        CompoundTag spirituality = mysticalTag.getCompound(spiritualityTagId);
        CompoundTag names = mysticalTag.getCompound(playerNameTagId);
        float originalTotalSpirituality = mysticalTag.getFloat(totalSpiritualityId);
        int i = 0;
        int bestIndex = 0;
        float bestSpirituality = -1;
        UUID bestName = UUID.randomUUID();
        while(spirituality.contains("spirituality_" + i)){
            float testSpirituality = spirituality.getFloat("spirituality_" + i);
            if(testSpirituality > bestSpirituality){
                UUID name = names.getUUID("player_" + i);
                if(level.getPlayerByUUID(name) != null){
                    bestIndex = i;
                    bestSpirituality = testSpirituality;
                    bestName = name;
                }
            }
            i++;
        }
        if(bestSpirituality != -1){
            if(bestSpirituality - toConsume <= 0){
                spirituality.remove("spirituality_" + bestIndex);
                names.remove("player_" + bestIndex);
                mysticalTag.putFloat(totalSpiritualityId, originalTotalSpirituality - bestSpirituality);
            } else {
                spirituality.putFloat("spirituality_" + bestIndex, bestSpirituality - toConsume);
                mysticalTag.putFloat(totalSpiritualityId, originalTotalSpirituality - toConsume);
            }
            return level.getPlayerByUUID(bestName);
        }
        mysticalTag.putFloat(totalSpiritualityId, 0f);
        return null;
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

    public static CompoundTag generateMysticismTag(){
        CompoundTag mystTag = new CompoundTag();
        CompoundTag spiritualityTag = new CompoundTag();
        CompoundTag nameTag = new CompoundTag();
        mystTag.put(spiritualityTagId, spiritualityTag);
        mystTag.put(playerNameTagId, nameTag);
        mystTag.putFloat(totalSpiritualityId, 0f);
        return mystTag;
    }

    public static float getSpiritualityOfItem(ItemStack stack){
        if(!stack.hasTag() || !stack.getTag().contains(mysticismTagId)) return 0f;

        CompoundTag mystTag = stack.getTag().getCompound(mysticismTagId);
        return mystTag.getFloat(totalSpiritualityId);
    }

    public static void updateMysticismTag(ItemStack stack, float spiritualityAmountToAdd, Player target){

    }

    /**
     * this method adds the player into the items NBT registry, creating the myst tag if it doesnt exist.
     * @param stack
     * @param spiritualityAmount
     * @param target
     */
    public static void updateOrApplyMysticismTag(ItemStack stack, float spiritualityAmount, Player target) {
        CompoundTag mystTag;
        if(!stack.hasTag() || !stack.getTag().contains(mysticismTagId)){
            mystTag = generateMysticismTag();
        } else {
            mystTag = stack.getTag().getCompound(mysticismTagId);
        }

        CompoundTag spiritualityTag = mystTag.getCompound(spiritualityTagId);
        CompoundTag nameTag = mystTag.getCompound(playerNameTagId);

        //to generate other types of tags that dont include the player (like spirituality-heavy coins found in archeology)
        if(target != null) {
            int i = 0;
            boolean flag = false;
            //tries to find a valid index i:
            //an index i is valid if its an index that corresponds to the player
            //if it couldnt find that player, it then searches for an available spot to write their information
            //in the end, you get an i that corresponds to either the players old spot, or a new one if its the first time writing this player in.
            for(String key: nameTag.getAllKeys()){
                if(nameTag.getUUID(key).equals(target.getUUID())){
                    flag = true;
                    break;
                }
                i++;
            }
            if(!flag){
                i = 0;
                while(spiritualityTag.contains("spirituality_" + i)){
                    i++;
                }
            }

            float oldSpirituality = spiritualityTag.getFloat("spirituality_" + i);
            if(oldSpirituality + spiritualityAmount <= 0){
                spiritualityTag.remove("spirituality_" + i);
                spiritualityTag.remove("player_" + i);
            } else {
                spiritualityTag.putFloat("spirituality_" + i, oldSpirituality + spiritualityAmount);
                nameTag.putUUID("player_" + i, target.getUUID());
            }

        }

        //calculates the sum everytime for consistency
        float sum = 0f;
        for(String key: spiritualityTag.getAllKeys()){
            sum += spiritualityTag.getFloat(key);
        }

        mystTag.putFloat(totalSpiritualityId, sum);
        CompoundTag og = stack.getOrCreateTag();
        og.put(mysticismTagId, mystTag);
    }
}
