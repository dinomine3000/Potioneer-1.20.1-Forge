package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static net.dinomine.potioneer.config.PotioneerCommonConfig.CHANCE_TO_MAKE_ARTIFACT_ON_DEATH;

public class CharacteristicHelper {

    public static void addCharacteristicToLevel(int pathwaySequenceId, Level level, @Nullable Player player, Vec3 position, RandomSource random){
        if(pathwaySequenceId < 0) return;
        if(player != null && random.nextFloat() < CHANCE_TO_MAKE_ARTIFACT_ON_DEATH.get()){
            for(ItemStack stack: player.getInventory().items){
                if(MysticalItemHelper.isValidItemForArtifact(stack)){
                    ItemStack stackCopy = stack.copy();
                    MysticalItemHelper.generateSealedArtifact(stackCopy, pathwaySequenceId, random);
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
        CompoundTag root = new CompoundTag();

        CompoundTag charInfo = new CompoundTag();
        charInfo.putInt("id", pathwaySequenceId);
        root.put("beyonder_info", charInfo);
        characteristic.setTag(root);

        if(player != null) MysticismHelper.updateOrApplyMysticismTag(characteristic, 20, player);


        CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), level, characteristic.copy(), pathwaySequenceId);
        entity.setSequenceId(pathwaySequenceId);
        entity.moveTo(position.offsetRandom(random, 1f).add(0, 1, 0));
        level.addFreshEntity(entity);
    }

    public static void addCharacteristicToLevel(List<Integer> characList, Level level, @Nullable Player player, Vec3 position, RandomSource random){
        for(int characId: characList){
            addCharacteristicToLevel(characId, level, player, position, random);
        }
    }
}
