package net.dinomine.potioneer.item.custom;

import net.dinomine.potioneer.beyonder.pathways.Pathways;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.LivingEntityBeyonderCapability;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.OpenScreenMessage;
import net.dinomine.potioneer.recipe.PotionRecipeData;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.util.PotioneerMathHelper;
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

    public static PotionRecipeData applyOrReadFormulaNbt(ItemStack stack, ServerLevel pLevel, int id, LivingEntityBeyonderCapability cap){
        PotionRecipeData result;
        if(stack.hasTag() && stack.getTag().get("recipe_data") != null){
            result = PotionRecipeData.load(stack.getTag().getCompound("recipe_data"));
            return result;
        }
        PotionFormulaSaveData data = PotionFormulaSaveData.from((ServerLevel) pLevel);
        result = data.getFormulaDataFromId(PotioneerMathHelper.ProbabilityHelper.getRandomPathwaySequenceId(cap, pLevel.random), pLevel);

        int limit = 5;
        while(result == null && limit-- >= 0){
            System.out.println("Generated id is invalid. creating a new one...");
            result = data.getFormulaDataFromId(PotioneerMathHelper.ProbabilityHelper.getRandomId(pLevel.random), pLevel);
        }
        if(result != null) writeToNbt(stack, result, false);
        else System.out.println("Warning: could not generate a proper formula.");
        return result;
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
