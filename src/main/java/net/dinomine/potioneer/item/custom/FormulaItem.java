package net.dinomine.potioneer.item.custom;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.beyonder.player.BeyonderStatsProvider;
import net.dinomine.potioneer.beyonder.player.EntityBeyonderManager;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerFormulaScreenSTCMessage;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FormulaItem extends Item {
    public FormulaItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide()) return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, heldItem);

        pPlayer.getCapability(BeyonderStatsProvider.BEYONDER_STATS).ifPresent(cap -> {
            PotionRecipeData result = applyOrReadFormulaNbt(heldItem, pLevel, cap.getPathwayId(), cap);
            boolean error = heldItem.getTag().getBoolean("error");

            PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) pPlayer),
                    new PlayerFormulaScreenSTCMessage(result, error));
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

    public static PotionRecipeData applyOrReadFormulaNbt(ItemStack stack, Level pLevel, int id, EntityBeyonderManager cap){
        PotionRecipeData result;
        if(stack.hasTag() && stack.getTag().get("recipe_data") != null){
            result = PotionRecipeData.load(stack.getTag().getCompound("recipe_data"));
        } else {
            PotionFormulaSaveData data = PotionFormulaSaveData.from((ServerLevel) pLevel);
            if(id < 0){
                result = data.getFormulaDataFromId(getRandomId(pLevel));
            } else {
                int sequenceLevel = id % 10;
                int pathwayId = Math.floorDiv(id, 10);

                double chanceOfNextFormula = sequenceLevelFunction(sequenceLevel) + 0.05;
                double chanceOfSamePathwayFormula = 0.3;
                if(sequenceLevel > 0 && pLevel.random.nextFloat() < cap.getLuckManager().checkLuck((float)chanceOfNextFormula)){
                    cap.getLuckManager().consumeLuck(10);
                    System.out.println("Generating next formula...");
                    result = data.getFormulaDataFromId(id-1);
                } else {
                    if(pLevel.random.nextFloat() < cap.getLuckManager().checkLuck((float)chanceOfSamePathwayFormula)){
                        System.out.println("Generating pathway formula...");
                        result = data.getFormulaDataFromId(10*pathwayId + getRandomSequenceLevel(9, pLevel.random.nextDouble()));
                    } else {
                        result = data.getFormulaDataFromId(getRandomId(pLevel));
                    }
                }
            }

            while(result == null){
                System.out.println("Random generated id is invalid. creating a new one...");
                result = data.getFormulaDataFromId(getRandomId(pLevel));
            }
            writeToNbt(stack, result, false);
        }

        return result;
    }

    private static int getRandomId(Level pLevel){
        int id = pLevel.random.nextInt(5)*10 + getRandomSequenceLevel(9, pLevel.random.nextDouble());
        //System.out.println(id);
        return id;
    }

    private static double sequenceLevelFunction(int sequenceLevel){
        return 0.0023225*Math.pow(sequenceLevel, 2.2) + 0.003;
    }

    /**
     * function that will check if the formula for the given sequence should be generated, based on a random number from 0 to 1
     * @param sequenceLevel - sequence level of the formula you want to generate.
     * @param rndNumber - random number from 0 to 1
     * @return
     */
    private static int getRandomSequenceLevel(int sequenceLevel, double rndNumber){
        if(sequenceLevel == 1) return 1;
        double chance = sequenceLevelFunction(sequenceLevel);
        if(rndNumber < chance) {
            System.out.println("generated sequence level: " + sequenceLevel);
            return sequenceLevel;
        }
        return getRandomSequenceLevel(sequenceLevel - 1, Math.max(rndNumber - chance, 0));
    }

    private void writeToNbt(ItemStack formulaItem, PotionRecipeData data){
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        CompoundTag finaltag = new CompoundTag();
        finaltag.put("recipe_data", tag);
        formulaItem.setTag(finaltag);
    }

    public static void writeToNbt(ItemStack formulaItem, PotionRecipeData data, boolean error){
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        CompoundTag finaltag = new CompoundTag();
        finaltag.putBoolean("error", error);
        finaltag.put("recipe_data", tag);
        formulaItem.setTag(finaltag);
    }

    private static JsonObject readJsonObject(ResourceLocation path) throws IOException {
        ResourceLocation newPath = path.withPrefix("../../data/" + Potioneer.MOD_ID + "/recipes/");
        InputStream in = Minecraft.getInstance().getResourceManager().open(newPath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Gson gson = new Gson();
        JsonElement je = gson.fromJson(reader, JsonElement.class);
        JsonObject json = je.getAsJsonObject();
        in.close();
        return json;
    }

    private static NonNullList<ItemStack> itemsFromJson(JsonArray pItemArray) {
        NonNullList<ItemStack> nonnulllist = NonNullList.create();

        for(int i = 0; i < pItemArray.size(); ++i) {
            ItemStack ingredient = ShapedRecipe.itemStackFromJson(pItemArray.get(i).getAsJsonObject());
            nonnulllist.add(ingredient);
        }

        return nonnulllist;
    }
}
