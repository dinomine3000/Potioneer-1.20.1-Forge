package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.beyonder.player.PlayerLuckManager;
import net.dinomine.potioneer.config.PotioneerCommonConfig;
import net.dinomine.potioneer.item.ModItems;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.util.PotioneerMathHelper;
import net.dinomine.potioneer.util.misc.MysticalItemHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.Optional;

public class SealedBundleItem extends Item {

    public SealedBundleItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        Optional<LivingEntityBeyonderCapability> optCap = pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).resolve();
        if(pLevel.isClientSide() || optCap.isEmpty()) return new InteractionResultHolder<>(InteractionResult.FAIL, heldItem);
        LivingEntityBeyonderCapability cap = optCap.get();
        PlayerLuckManager luckManager = cap.getLuckManager();
        RandomSource random = pPlayer.getRandom();
        if(luckManager.passesLuckCheck((float)(double)PotioneerCommonConfig.SEALED_BUNDLE_CHARACTERISTIC_CHANCE.get(), 0, 0, random)){
            if(luckManager.passesLuckCheck((float)(double)PotioneerCommonConfig.SEALED_BUNDLE_ARTIFACT_CHANCE.get(), 0, 0, random)){
                ItemStack res = getRandomArtifact(cap, random);
                replacePlayerItem(pPlayer, heldItem, res);
                return new InteractionResultHolder<>(InteractionResult.SUCCESS, res);
            }
            ItemStack res = CharacteristicItem.createCharacteristic(PotioneerMathHelper.ProbabilityHelper.getRandomPathwaySequenceId(cap, random));
            replacePlayerItem(pPlayer, heldItem, res);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, res);
        }
        if(luckManager.passesLuckCheck((float)(double)PotioneerCommonConfig.SEALED_BUNDLE_MAIN_INGREDIENT_CHANCE.get(), 0, 0, random)){
            PotionFormulaSaveData saveData = PotionFormulaSaveData.from((ServerLevel) pLevel);
            ItemStack ingredient = saveData.getRandomMainIngredientFor(PotioneerMathHelper.ProbabilityHelper.getRandomPathwaySequenceId(cap, random), random);
            replacePlayerItem(pPlayer, heldItem, ingredient);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, ingredient);
        }
        if(luckManager.passesLuckCheck((float) (double)PotioneerCommonConfig.SEALED_BUNDLE_FORMULA_CHANCE.get(), 0, 0, random)){
            ItemStack formula = new ItemStack(ModItems.FORMULA.get());
            replacePlayerItem(pPlayer, heldItem, formula);
            return new InteractionResultHolder<>(InteractionResult.SUCCESS, formula);
        }
        ItemStack page = new ItemStack(ModItems.BEYONDER_PAGE.get());
        replacePlayerItem(pPlayer, heldItem, page);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, page);
    }

    private static ItemStack getRandomArtifact(LivingEntityBeyonderCapability cap, RandomSource random){
        List<Item> itemList = PotioneerCommonConfig.getRandomArtifactItems();
        ItemStack stack = new ItemStack(itemList.get(random.nextInt(itemList.size())));
        int charPathSeqId = PotioneerMathHelper.ProbabilityHelper.getRandomPathwaySequenceId(cap, random);
        MysticalItemHelper.generateSealedArtifact(stack, charPathSeqId, random);
        return stack;
    }

    private static void replacePlayerItem(Player player, ItemStack heldItem, ItemStack resultingItem){
//        heldItem.setCount(0);
        player.level().playSound(null, player.getOnPos(), SoundEvents.BUNDLE_DROP_CONTENTS, SoundSource.NEUTRAL, 1, 1);
//        if(!player.addItem(resultingItem))
//            player.drop(resultingItem, false, true);
    }
}
