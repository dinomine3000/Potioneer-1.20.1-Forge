package net.dinomine.potioneer.beyonder.player.luck.luckevents;

import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ItemGenLuckEvent extends LuckEvent {
    @Override
    public void triggerEvent(LivingEntityBeyonderCapability cap, PlayerLuckManager luck, LivingEntity target) {
        List<Item> items = getPossibleItems(target.level(), cap, target.getRandom());
        if(target instanceof Player player){
            player.addItem(
                    new ItemStack(items.get(target.getRandom().nextInt(items.size())))
                            .copyWithCount(luck.getRandomNumber(0, 3, true, target.getRandom()))
            );
            player.level().playSound(null, player.getOnPos(), SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.NEUTRAL, 1, 1);
        }
    }

    public static List<Item> getPossibleItems(Level level, LivingEntityBeyonderCapability cap, RandomSource random){
        List<String> itemKeys = PotioneerCommonConfig.ITEM_GEN_LUCK_EVENT_ITEMS.get();
        List<Item> res = new ArrayList<>();
        for(String key: itemKeys){
            ResourceLocation rl = ResourceLocation.tryParse(key);
            if (rl == null) continue;

            Item item = ForgeRegistries.ITEMS.getValue(rl);
            if (item != null && item != Items.AIR) {
                res.add(item);
            }
        }
        if(PotioneerCommonConfig.ITEM_GEN_LUCK_EVENT_INCLUDE_ALL_FORMULA.get() == PotioneerCommonConfig.ITEM_GEN_EVENT.NONE) return res;
        if(level instanceof ServerLevel sLevel){
            PotionFormulaSaveData saveData = PotionFormulaSaveData.from(sLevel);
            if(PotioneerCommonConfig.ITEM_GEN_LUCK_EVENT_INCLUDE_ALL_FORMULA.get() == PotioneerCommonConfig.ITEM_GEN_EVENT.ONE){
                res.add(saveData.getRandomItemFromFormulaFor(PotioneerMathHelper.ProbabilityHelper.getRandomPathwaySequenceId(cap.getPathwaySequenceId(), cap.getLuckManager(), random, cap.getCharacteristicManager().getAptitudePathway()), random).getItem());
            } else {
                res.addAll(saveData.getAllItems());
            }
        }
        return res;
    }
}
