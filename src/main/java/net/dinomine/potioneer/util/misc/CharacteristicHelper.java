package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.util.ModCompoundTags;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.stream.Collectors;

import static net.dinomine.potioneer.config.PotioneerCommonConfig.CHANCE_TO_MAKE_ARTIFACT_ON_DEATH;

public class CharacteristicHelper {

    public static void addCharacteristicToLevel(List<Integer> pathwaySequenceIds, Level level, @Nullable Player player, Vec3 position, RandomSource random){
        if(pathwaySequenceIds.isEmpty()) return;
        if(player != null && random.nextFloat() < CHANCE_TO_MAKE_ARTIFACT_ON_DEATH.get()){
            for(ItemStack stack: player.getInventory().items){
                if(MysticalItemHelper.isValidItemForArtifact(stack)){
                    ItemStack stackCopy = stack.copy();
                    MysticalItemHelper.generateSealedArtifact(stackCopy, pathwaySequenceIds, random);
                    MysticismHelper.updateOrApplyMysticismTag(stackCopy, 50, player);
                    stack.setCount(0);

                    Vec3 pos = position.offsetRandom(random, 1f).add(0, 1, 0);
                    ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, stackCopy);
                    entity.setInvulnerable(true);
                    level.addFreshEntity(entity);
                    return;
                }
            }
        }
        ItemStack characteristic = new ItemStack(ModItems.CHARACTERISTIC.get());

        ModCompoundTags.BeyonderInfoTag.setTagForItem(characteristic, pathwaySequenceIds);

        if(player != null) MysticismHelper.updateOrApplyMysticismTag(characteristic, 20, player);


        CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), level, characteristic.copy(), pathwaySequenceIds);
        entity.setSequenceId(pathwaySequenceIds);
        entity.moveTo(position.offsetRandom(random, 1f).add(0, 1, 0));
        level.addFreshEntity(entity);
    }

    public static boolean isSameHouseOfTen(List<Integer> numbers) {
        int house = Math.floorDiv(numbers.get(0), 10);

        for (int num : numbers) {
            if (Math.floorDiv(num, 10) != house) {
                return false;
            }
        }

        return true;
    }

    public static void addCharacteristicsToLevel(List<List<Integer>> characList, Level level, @Nullable Player player, Vec3 position, RandomSource random){
        for(List<Integer> characIds: characList){
            addCharacteristicToLevel(characIds, level, player, position, random);
        }
    }


    /**
     * given a list of numbers, returns a list of every number that is closest to its lower multiple of 10.
     * This way, we get the highest level for each pathway
     * @param nums, a list of characteristics like [17, 18, 19, 25, 24, 29, 37, 36]
     * @return the best levels for each pathway [17, 24, 36] for the above example
     */
    public static List<Integer> closestToLowerTens(List<Integer> nums) {
        Map<Integer, Integer> bestPerTen = new HashMap<>();

        for (int n : nums) {
            int base = (n / 10) * 10;
            int dist = n - base;

            bestPerTen.compute(base, (k, currentBest) -> {
                if (currentBest == null) return n;
                int currentDist = currentBest - base;
                return dist < currentDist ? n : currentBest;
            });
        }

        return new ArrayList<>(bestPerTen.values());
    }
}
