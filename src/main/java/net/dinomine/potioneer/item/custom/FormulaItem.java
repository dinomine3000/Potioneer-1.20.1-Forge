package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

public class FormulaItem extends Item {
    public FormulaItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide()) return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);

        pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PotionRecipeData result = applyOrReadFormulaNbt(heldItem, (ServerLevel) pLevel, cap.getPathwaySequenceId(), cap);
            boolean error = heldItem.getTag().getBoolean("error");

            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) pPlayer),
                    new OpenScreenMessage(result, error));
        });
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {

//        if(event.getLevel() != null){
//            MinecraftServer lvl = event.getLevel().getServer();
//            if(lvl == null || lvl.overworld() != event.getLevel()) return;
//            PotionFormulaSaveData.from((ServerLevel) event.getLevel());
//        }
        return super.useOn(pContext);
    }

    public static PotionRecipeData applyOrReadFormulaNbt(ItemStack stack, ServerLevel pLevel, int id, LivingEntityBeyonderCapability cap){
        PotionRecipeData result;
        if(stack.hasTag() && stack.getTag().get("recipe_data") != null){
            result = PotionRecipeData.load(stack.getTag().getCompound("recipe_data"));
            return result;
        }
        PotionFormulaSaveData data = PotionFormulaSaveData.from((ServerLevel) pLevel);
        if(id < 0){
            result = data.getFormulaDataFromId(getRandomId(pLevel), pLevel);
        } else {
            int sequenceLevel = id % 10;
            int pathwayId = Math.floorDiv(id, 10);

            double chanceOfNextFormula = sequenceLevelFunction(sequenceLevel) + 0.05;
            double chanceOfSamePathwayFormula = 0.4;
            if(sequenceLevel > 0 && cap.getLuckManager().passesLuckCheck((float)chanceOfNextFormula, 10, 0, pLevel.random)){
                System.out.println("Generating next formula...");
                result = data.getFormulaDataFromId(id-1, pLevel);
            } else {
                if(cap.getLuckManager().passesLuckCheck((float)chanceOfSamePathwayFormula, 0, 0, pLevel.random)){
                    System.out.println("Generating pathway formula...");
                    result = data.getFormulaDataFromId(10*pathwayId + getRandomSequenceLevel(9, pLevel.random.nextDouble()), pLevel);
                } else {
                    result = data.getFormulaDataFromId(getRandomId(pLevel), pLevel);
                }
            }
        }

        int limit = 5;
        while(result == null && limit-- >= 0){
            System.out.println("Generated id is invalid. creating a new one...");
            result = data.getFormulaDataFromId(getRandomId(pLevel), pLevel);
        }
        if(result != null) writeToNbt(stack, result, false);
        else System.out.println("Warning: could not generate a proper formula.");
        return result;
    }

    private static int getRandomId(Level pLevel){
        return Pathways.getRandomPathwayId(pLevel.random)*10 + getRandomSequenceLevel(9, pLevel.random.nextDouble());
    }

    private static double sequenceLevelFunction(int sequenceLevel){
        return 0.00232250*Math.pow(sequenceLevel, 2.2) + 0.003;
    }

    /**
     * function that will check if the formula for the given sequence should be generated, based on a random number from 0 to 1
     * follows this desmos graph: <a href="https://www.desmos.com/calculator/ff70da0c33">...</a>
     * @param sequenceLevel - sequence level of the formula you want to generate.
     * @param rndNumber - random number from 0 to 1
     * @return
     */
    private static int getRandomSequenceLevel(int sequenceLevel, double rndNumber){
        if(sequenceLevel == 1) return 1;
        double chance = sequenceLevelFunction(sequenceLevel);
        if(rndNumber < chance) {
            return sequenceLevel;
        }
        return getRandomSequenceLevel(sequenceLevel - 1, Math.max(rndNumber - chance, 0));
//        this was my attempt at making this more readeable, dont feel like doing. you can have this to see the chances to generate each sequence (from sequence level 9 to 1)
//        ArrayList<Float> probabilities = new ArrayList<>(List.of(0.29494f, 0.22829f, 0.17095f, 0.12264f, 0.08311f, 0.05203f, 0.02904f, 0.01367f, 0.005322f));
//        return 9 - PotioneerMathHelper.ProbabilityHelper.pickRandom(probabilities, (float) rndNumber);
    }

    public static void writeToNbt(ItemStack formulaItem, PotionRecipeData data, boolean error){
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        CompoundTag finaltag = new CompoundTag();
        finaltag.putBoolean("error", error);
        finaltag.put("recipe_data", tag);
        formulaItem.setTag(finaltag);
    }
}
