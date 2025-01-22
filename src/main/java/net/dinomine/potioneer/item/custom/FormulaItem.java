package net.dinomine.potioneer.item.custom;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dinomine.potioneer.Potioneer;
import net.dinomine.potioneer.network.PacketHandler;
import net.dinomine.potioneer.network.messages.PlayerAdvanceMessage;
import net.dinomine.potioneer.network.messages.PlayerFormulaScreenSTCMessage;
import net.dinomine.potioneer.savedata.PotionFormulaSaveData;
import net.dinomine.potioneer.savedata.PotionRecipeData;
import net.minecraft.client.Minecraft;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class FormulaItem extends Item {
    public FormulaItem(Properties pProperties) {
        super(pProperties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack heldItem = pPlayer.getItemInHand(pUsedHand);
        if(pLevel.isClientSide()) return new InteractionResultHolder<ItemStack>(InteractionResult.SUCCESS, heldItem);
        PotionRecipeData result;

        if(heldItem.hasTag() && heldItem.getTag().get("recipe_data") != null){
            result = PotionRecipeData.load(heldItem.getTag().getCompound("recipe_data"));
        } else {
            PotionFormulaSaveData data = PotionFormulaSaveData.from((ServerLevel) pLevel);
            ArrayList<PotionRecipeData> list = data.getFormulas();

            int idx = (int) (Mth.clamp(Math.round(Math.random()*list.size() - 0.5f), 0, list.size()));
            result = data.getFormulas().get(idx);

            writeToNbt(heldItem, result);
        }

        PacketHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) pPlayer),
                new PlayerFormulaScreenSTCMessage(result));
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

    private void writeToNbt(ItemStack formulaItem, PotionRecipeData data){
        CompoundTag tag = new CompoundTag();
        data.save(tag);
        CompoundTag finaltag = new CompoundTag();
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
