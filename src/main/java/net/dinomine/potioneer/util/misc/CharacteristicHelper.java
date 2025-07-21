package net.dinomine.potioneer.util.misc;

import net.dinomine.potioneer.entities.ModEntities;
import net.dinomine.potioneer.entities.custom.CharacteristicEntity;
import net.dinomine.potioneer.item.ModItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class CharacteristicHelper {

    public static void addCharacteristicToLevel(int sequenceId, Level level, @Nullable Player player, Vec3 position, RandomSource random){
        ItemStack characteristic = new ItemStack(ModItems.CHARACTERISTIC.get());
        CompoundTag root = new CompoundTag();

        CompoundTag charInfo = new CompoundTag();
        charInfo.putInt("id", sequenceId);
        root.put("beyonder_info", charInfo);
        characteristic.setTag(root);

        if(player != null) MysticismHelper.updateOrApplyMysticismTag(characteristic, 20, player);


        CharacteristicEntity entity = new CharacteristicEntity(ModEntities.CHARACTERISTIC.get(), level, characteristic.copy(), sequenceId);
        entity.setSequenceId(sequenceId);
        entity.moveTo(position.offsetRandom(random, 1f).add(0, 1, 0));
        level.addFreshEntity(entity);
    }
}
